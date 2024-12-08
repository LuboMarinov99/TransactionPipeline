package com.lyuboslav.transactionpipeline.service;

import com.lyuboslav.transactionpipeline.model.Transaction;
import com.lyuboslav.transactionpipeline.rules.*;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.*;

@Service
public class TransactionService {
	public static final String USER_TRANSACTIONS_KEY_TEMPLATE = "user:%s:transactions";
	public static final int AMOUNT_OF_MINUTES = 30;
	private final RedissonClient redissonClient;
	private final Rule rule;
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	public TransactionService(RedissonClient redissonClient, Rule ruleConfig) {
		this.redissonClient = redissonClient;
		this.rule = ruleConfig;
	}

	public boolean isTransactionValid(Transaction transaction) {
		Instant startTime = Instant.now();
		String key = getTransactionKey(transaction.getUserId());
		addTransaction(key, transaction);

		List<Transaction> recentTransactionsForUser = getRecentTransactionsForUser(key, transaction.getTimestamp());
		TransactionContext transactionContext = getTransactionContext(recentTransactionsForUser);

		boolean ruleChainResult = rule.applyRule(transactionContext);
		Instant endTime = Instant.now();
		logger.info("Transaction validation took: {} ms", endTime.toEpochMilli() - startTime.toEpochMilli());

		return ruleChainResult;
	}

	/**
	 * Adds a transaction to the cache. Transactions are grouped by user and are stored in ordered by their timestamp.
	 * Each time a transaction is added for a user, the TTL of the whole set is extended by 30 minutes.
	 *
	 * @param key         - The cache key.
	 * @param transaction - The incoming transaction to be analysed.
	 */
	private void addTransaction(String key, Transaction transaction) {
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);
		Instant relativeNow = transaction.getTimestamp();
		Instant expireIn = relativeNow.plusSeconds(AMOUNT_OF_MINUTES * 60);

		sortedSet.add(transaction.getTimestamp().toEpochMilli(), transaction);

		redissonClient.getKeys().expireAt(key, expireIn.toEpochMilli());
	}

	private TransactionContext getTransactionContext(List<Transaction> recentTransactionsForUser) {
		if (recentTransactionsForUser.isEmpty()) {
			throw new RuntimeException("No recent transactions found for user");
		}
		reverse(recentTransactionsForUser);
		Transaction lastTransaction = recentTransactionsForUser.removeFirst();

		return new TransactionContext(lastTransaction, unmodifiableList(recentTransactionsForUser));
	}

	/**
	 * Retrieve the transactions for a user that are within the last 30 minutes. Transactions older than the threshold are removed.
	 *
	 * @param key       - The cache key
	 * @param timestamp - The timestamp of the incoming transaction (The relative now)
	 * @return - A list of transactions that are within the last 30 minutes.
	 */
	private List<Transaction> getRecentTransactionsForUser(String key, Instant timestamp) {
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);

		long relativeNow = timestamp.toEpochMilli();
		long minutesAgo = timestamp.minusSeconds(AMOUNT_OF_MINUTES * 60).toEpochMilli();

		sortedSet.removeRangeByScore(0, true, minutesAgo, false);

		return (List<Transaction>) sortedSet.valueRange(minutesAgo, true, relativeNow, true);
	}

	private String getTransactionKey(String userId) {
		return USER_TRANSACTIONS_KEY_TEMPLATE.formatted(userId);
	}
}
