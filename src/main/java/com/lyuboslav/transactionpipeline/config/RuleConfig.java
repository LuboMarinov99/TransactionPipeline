package com.lyuboslav.transactionpipeline.config;

import com.lyuboslav.transactionpipeline.rules.BlacklistedCountryRule;
import com.lyuboslav.transactionpipeline.rules.MaxDistanceRule;
import com.lyuboslav.transactionpipeline.rules.RateLimitRule;
import com.lyuboslav.transactionpipeline.rules.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RuleConfig {

	@Bean
	public Rule getRuleChain() {
		return new
				BlacklistedCountryRule(List.of("Russia", "China"))
				.addNextRule(new MaxDistanceRule())
				.addNextRule(new RateLimitRule(1, 10));
	}
}
