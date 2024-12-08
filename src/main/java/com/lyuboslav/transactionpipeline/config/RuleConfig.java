package com.lyuboslav.transactionpipeline.config;

import com.lyuboslav.transactionpipeline.rules.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RuleConfig {

	@Bean
	public Rule getRuleChain() {
		Rule rule = new BlacklistedCountryRule(List.of("Russia", "China", "North Korea"));
		rule
				.addNextRule(new MaxDistanceRule())
				.addNextRule(new RateLimitRule(1, 10))
				.addNextRule(new MaxCountriesRule(10, 3));

		return rule;
	}
}
