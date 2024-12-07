package com.lyuboslav.transactionpipeline.rules;

import com.lyuboslav.transactionpipeline.model.Transaction;

import java.time.Instant;
import java.util.List;

public class RateLimitRule extends Rule {

	private final int targetMinutes;
	private final int maxTransactions;

	public RateLimitRule(int targetMinutes, int maxTransactions) {
		this.targetMinutes = targetMinutes;
		this.maxTransactions = maxTransactions;
	}

	@Override
	public boolean applyRule(TransactionContext transactionContext) {
		List<Transaction> recentTransactions = transactionContext.getRecentTransactions();
		Transaction lastTransaction = transactionContext.getLastTransaction();

		Instant targetTime = lastTransaction.getTimestamp().minusSeconds(targetMinutes * 60L);

		List<Transaction> relevantTransactions = recentTransactions.stream()
				.takeWhile(transaction -> transaction.getTimestamp().isAfter(targetTime))
				.limit(maxTransactions)
				.toList();

		if (relevantTransactions.size() == maxTransactions) {
			return false;
		}

		return applyNextRule(transactionContext);
	}
}
