package com.lyuboslav.transactionpipeline.rules;

import java.util.List;

public class BlacklistedCountryRule extends Rule {

	private final List<String> blacklistedCountries;

	public BlacklistedCountryRule(List<String> blacklistedCountries) {
		this.blacklistedCountries = blacklistedCountries;
	}

	@Override
	public boolean applyRule(TransactionContext transactionContext) {
		if (blacklistedCountries.contains(transactionContext.getLastTransaction().getCountry())) {
			logger.info("The transaction does not comply with the blacklisted country rule.");
			return false;
		}

		return applyNextRule(transactionContext);
	}
}
