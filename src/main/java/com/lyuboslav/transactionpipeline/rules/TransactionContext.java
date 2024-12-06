package com.lyuboslav.transactionpipeline.rules;

import com.lyuboslav.transactionpipeline.model.Transaction;

import java.util.List;

public class TransactionContext {

	private final Transaction lastTransaction;

	private final List<Transaction> recentTransactions;

	public TransactionContext(Transaction lastTransaction, List<Transaction> recentTransactions) {
		this.lastTransaction = lastTransaction;
		this.recentTransactions =recentTransactions;
	}

	public Transaction getLastTransaction() {
		return lastTransaction;
	}

	public List<Transaction> getRecentTransactions() {
		return recentTransactions;
	}
}
