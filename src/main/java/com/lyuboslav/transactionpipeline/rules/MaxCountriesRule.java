package com.lyuboslav.transactionpipeline.rules;

import com.lyuboslav.transactionpipeline.model.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MaxCountriesRule extends Rule {

	private final int targetMinutes;

	private final int maxCountries;

	public MaxCountriesRule(int targetMinutes, int maxCountries) {
		this.targetMinutes = targetMinutes;
		this.maxCountries = maxCountries;
	}

	@Override
	public boolean applyRule(TransactionContext transactionContext) {
		Transaction lastTransaction = transactionContext.getLastTransaction();
		List<Transaction> recentTransactions = transactionContext.getRecentTransactions();

		Instant targetTime = lastTransaction.getTimestamp().minusSeconds(targetMinutes * 60L);

		Set<String> countries = recentTransactions.stream()
				.takeWhile(transaction -> transaction.getTimestamp().isAfter(targetTime))
				.map(Transaction::getCountry)
				.limit(maxCountries)
				.collect(Collectors.toSet());

		countries.add(lastTransaction.getCountry());
		if (countries.size() >= maxCountries) {
			logger.info("The transaction does not comply with the max countries rule.");
			return false;
		}

		return applyNextRule(transactionContext);
	}
}
