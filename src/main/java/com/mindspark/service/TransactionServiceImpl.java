package com.mindspark.service;

import com.mindspark.dto.TransactionDetails;
import com.mindspark.model.Transaction;
import com.mindspark.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDetails transactionDetails) {

        Transaction transaction = Transaction.builder()
                .transactionId(transactionDetails.getTransactionId())
                .transactionType(transactionDetails.getTransactionType())
                .accountNumber(transactionDetails.getAccountNumber())
                .amount(transactionDetails.getAmount())
                .build();
    }
}
