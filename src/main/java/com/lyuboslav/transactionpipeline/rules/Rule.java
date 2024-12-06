package com.lyuboslav.transactionpipeline.rules;

public abstract class Rule {

	private Rule nextRule;

	public Rule addNextRule(Rule nextRule) {
		this.nextRule = nextRule;
		return nextRule;
	}

	public abstract boolean applyRule(TransactionContext transactionContext);

	protected boolean applyNextRule(TransactionContext transactionContext) {
		if(nextRule == null) {
			return true;
		}

		return nextRule.applyRule(transactionContext);
	}
}
