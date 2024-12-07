package com.lyuboslav.transactionpipeline.service;

import com.lyuboslav.transactionpipeline.model.Transaction;
import com.lyuboslav.transactionpipeline.rules.*;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;

@Service
public class TransactionService {
	public static final String USER_TRANSACTIONS_KEY_TEMPLATE = "user:%s:transactions";
	public static final int AMOUNT_OF_MINUTES = 30;
	private final RedissonClient redissonClient;
	private final Rule rule;

	public TransactionService(RedissonClient redissonClient, Rule ruleConfig) {
		this.redissonClient = redissonClient;
		this.rule = ruleConfig;
	}

	public boolean isTransactionValid(Transaction transaction) {
		String key = getTransactionKey(transaction.getUserId());
		addTransaction(key, transaction);

		List<Transaction> recentTransactionsForUser = getRecentTransactionsForUser(key, transaction.getTimestamp());
		TransactionContext transactionContext = getTransactionContext(recentTransactionsForUser);

		return rule.applyRule(transactionContext);
	}

	private void addTransaction(String key, Transaction transaction) {
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);
		Instant relativeNow = transaction.getTimestamp();
		Instant expireIn = relativeNow.plusSeconds(AMOUNT_OF_MINUTES * 60);

		sortedSet.add(transaction.getTimestamp().toEpochMilli(), transaction);

		redissonClient.getKeys().expireAt(key, expireIn.toEpochMilli());
	}

	private TransactionContext getTransactionContext(List<Transaction> recentTransactionsForUser) {
		reverse(recentTransactionsForUser);
		Transaction lastTransaction = recentTransactionsForUser.removeFirst();

		return new TransactionContext(lastTransaction, unmodifiableList(recentTransactionsForUser));
	}

	private ArrayList<Transaction> getRecentTransactionsForUser(String key, Instant timestamp) {
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);

		long relativeNow = timestamp.toEpochMilli();
		long minutesAgo = timestamp.minusSeconds(AMOUNT_OF_MINUTES * 60).toEpochMilli();

		return (ArrayList<Transaction>) sortedSet.valueRange(minutesAgo, true, relativeNow, true);
	}

	private String getTransactionKey(String userId) {
		return USER_TRANSACTIONS_KEY_TEMPLATE.formatted(userId);
	}
}
