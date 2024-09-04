package com.mindspark.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AccountInfo {

	private String accountName;
	private BigDecimal accountBalance;
	private String accountNumber;
}
