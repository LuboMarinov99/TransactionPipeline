package com.lyuboslav.transactionpipeline.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

public class Transaction {

	@JsonProperty
	private String tranId;

	@JsonProperty
	private String userId;

	@JsonProperty
	private double amount;

	@JsonProperty
	private Instant timestamp;

	@JsonProperty
	private String country;

	@JsonProperty
	private double latCoord;

	@JsonProperty
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
