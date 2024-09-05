package com.mindspark.service;

import com.mindspark.dto.TransactionDetails;
import com.mindspark.model.Transaction;

public interface TransactionService {

    public void saveTransaction(TransactionDetails transaction);
}
