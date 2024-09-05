package com.mindspark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransactionDetails {

    private String transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
}
