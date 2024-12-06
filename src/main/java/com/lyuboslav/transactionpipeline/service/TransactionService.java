package com.lyuboslav.transactionpipeline.service;

import com.lyuboslav.transactionpipeline.model.Transaction;
import com.lyuboslav.transactionpipeline.rules.BlacklistedCountryRule;
import com.lyuboslav.transactionpipeline.rules.MaxDistanceRule;
import com.lyuboslav.transactionpipeline.rules.Rule;
import com.lyuboslav.transactionpipeline.rules.TransactionContext;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.*;

@Service
public class TransactionService {
	public static final String USER_TRANSACTIONS_KEY_TEMPLATE = "user:%s:transactions";
	public static final int AMOUNT_OF_MINUTES = 10;
	private final RedissonClient redissonClient;

	public TransactionService(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	public boolean isTransactionValid(Transaction transaction) {
		String key = getTransactionKey(transaction.getUserId());
		addTransaction(key, transaction);

		List<Transaction> recentTransactionsForUser = getRecentTransactionsForUser(key);
		Transaction lastTransaction = recentTransactionsForUser.removeLast();
		TransactionContext transactionContext = new TransactionContext(lastTransaction, recentTransactionsForUser);

		Rule rule = new BlacklistedCountryRule(List.of("Russia", "China"));
		rule.addNextRule(new MaxDistanceRule());

		return rule.applyRule(transactionContext);
	}

	private void addTransaction(String key, Transaction transaction) {
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);

		//todo revert
		//sortedSet.add(transaction.getTimestamp().toEpochMilli(), transaction);
		sortedSet.add(System.currentTimeMillis(), transaction);

		redissonClient.getKeys().expire(key, AMOUNT_OF_MINUTES, MINUTES);
	}

	private ArrayList<Transaction> getRecentTransactionsForUser(String key) {
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);

		long now = System.currentTimeMillis();
		long minutesAgo = now - (AMOUNT_OF_MINUTES * 60 * 1000);

		return (ArrayList<Transaction>) sortedSet.valueRange(minutesAgo, true, now, true);
	}

	private String getTransactionKey(String userId) {
		return USER_TRANSACTIONS_KEY_TEMPLATE.formatted(userId);
	}
}
