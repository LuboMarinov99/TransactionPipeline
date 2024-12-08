package com.lyuboslav.transactionpipeline.rules;

import com.lyuboslav.transactionpipeline.model.Transaction;

import java.util.List;

public class MaxDistanceRule extends Rule {

	private static final double EARTH_RADIUS_KM = 6371.0;
	private static final double MAX_DISTANCE_KILOMETERS = 300.0;

	@Override
	public boolean applyRule(TransactionContext transactionContext) {
		List<Transaction> transactions = transactionContext.getRecentTransactions();
		Transaction lastTransaction = transactionContext.getLastTransaction();

		boolean isInvalid = transactions.stream()
				.anyMatch(transaction -> isDistanceMoreThanMaxKm(
						lastTransaction.getLatCoord(),
						lastTransaction.getLongCoord(),
						transaction.getLatCoord(),
						transaction.getLongCoord()
				));
		if (isInvalid) {
			return false;
		}

		return applyNextRule(transactionContext);
	}

	private static boolean isDistanceMoreThanMaxKm(double lat1, double lon1, double lat2, double lon2) {
		double lat1Rad = Math.toRadians(lat1);
		double lon1Rad = Math.toRadians(lon1);
		double lat2Rad = Math.toRadians(lat2);
		double lon2Rad = Math.toRadians(lon2);

		double deltaLat = lat2Rad - lat1Rad;
		double deltaLon = lon2Rad - lon1Rad;

		double a = Math.pow(Math.sin(deltaLat / 2), 2)
				+ Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(deltaLon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double distance = EARTH_RADIUS_KM * c;

		return distance > MAX_DISTANCE_KILOMETERS;
	}
}
