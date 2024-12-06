package com.lyuboslav.transactionpipeline.controller;

import com.lyuboslav.transactionpipeline.model.Transaction;
import com.lyuboslav.transactionpipeline.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping
	public ResponseEntity<Boolean> createTransaction(@RequestBody Transaction transaction) {
		boolean isValid = transactionService.isTransactionValid(transaction);
		return ResponseEntity.ok(isValid);
	}
}
