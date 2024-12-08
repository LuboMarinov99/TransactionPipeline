package com.lyuboslav.transactionpipeline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.Instant;

public class Transaction {

	@JsonProperty
	@NotNull(message = "Transaction ID cannot be null")
	private String tranId;

	@JsonProperty
	@NotNull(message = "User ID cannot be null")
	private String userId;

	@JsonProperty
	@NotNull(message = "Amount cannot be null")
	private double amount;

	@JsonProperty
	@PastOrPresent(message = "Timestamp must be in the past or present")
	private Instant timestamp;

	@JsonProperty
	@NotNull
	private String country;

	@JsonProperty
	@NotNull
	@DecimalMin(value = "-90.00", message = "Latitude must be >= -90")
	@DecimalMax(value = "90.00", message = "Latitude must be <= 90")
	private double latCoord;

	@JsonProperty
	@NotNull
	@DecimalMin(value = "-180.00", message = "Longitude must be >= -180")
	@DecimalMax(value = "180.00", message = "Longitude must be <= 180")
	private double longCoord;

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Instant getTimestamp() {
		if (timestamp == null) {
			this.setTimestamp(Instant.now());
		}

		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLatCoord() {
		return latCoord;
	}

	public void setLatCoord(double latCoord) {
		this.latCoord = latCoord;
	}

	public double getLongCoord() {
		return longCoord;
	}

	public void setLongCoord(double longCoord) {
		this.longCoord = longCoord;
	}
}
