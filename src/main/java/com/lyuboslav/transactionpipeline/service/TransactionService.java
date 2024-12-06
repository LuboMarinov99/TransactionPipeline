package com.lyuboslav.transactionpipeline.service;

import com.lyuboslav.transactionpipeline.model.Transaction;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import static java.util.concurrent.TimeUnit.*;

@Service
public class TransactionService {
	public static final String USER_TRANSACTIONS_KEY_TEMPLATE = "user:%s:transactions";
	public static final int AMOUNT_OF_MINUTES = 10;
	private final RedissonClient redissonClient;

	public TransactionService(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	public void addTransaction(Transaction transaction) {
		String key = getTransactionKey(transaction.getUserId());
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);

		sortedSet.add(transaction.getTimestamp().toEpochMilli(), transaction);
		redissonClient.getKeys().expire(key, AMOUNT_OF_MINUTES, MINUTES);
	}

	public ArrayList<Transaction> getRecentTransactionsForUser(String userId) {
		String key = getTransactionKey(userId);
		RScoredSortedSet<Transaction> sortedSet = redissonClient.getScoredSortedSet(key);

		long now = System.currentTimeMillis();
		long minutesAgo = now - (AMOUNT_OF_MINUTES * 60 * 1000);

		return (ArrayList<Transaction>) sortedSet.valueRange(minutesAgo, true, now, true);
	}

	private String getTransactionKey(String userId) {
		return USER_TRANSACTIONS_KEY_TEMPLATE.formatted(userId);
	}
}
