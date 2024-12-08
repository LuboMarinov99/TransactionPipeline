package com.lyuboslav.transactionpipeline.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Rule {

	protected static final Logger logger = LoggerFactory.getLogger(Rule.class);

	private Rule nextRule;

	public Rule addNextRule(Rule nextRule) {
		this.nextRule = nextRule;
		return nextRule;
	}

	public abstract boolean applyRule(TransactionContext transactionContext);

	protected boolean applyNextRule(TransactionContext transactionContext) {
		if (nextRule == null) {
			return true;
		}

		return nextRule.applyRule(transactionContext);
	}
}
