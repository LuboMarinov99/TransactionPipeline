package com.lyuboslav.transactionpipeline.rules;

import java.util.List;

public class BlacklistedCountryRule extends Rule {

	private List<String> blacklistedCountries;

	public BlacklistedCountryRule(List<String> blacklistedCountries) {
		this.blacklistedCountries = blacklistedCountries;
	}

	@Override
	public boolean applyRule(TransactionContext transactionContext) {
		if (blacklistedCountries.contains(transactionContext.getLastTransaction().getCountry())) {
			return false;
		}

		return applyNextRule(transactionContext);
	}

	public void changeBlacklistedCountries(List<String> blacklistedCountries) {
		this.blacklistedCountries = blacklistedCountries;
	}
}
