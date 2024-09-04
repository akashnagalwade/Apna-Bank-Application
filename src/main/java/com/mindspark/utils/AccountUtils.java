package com.mindspark.utils;

import java.time.Year;

public class AccountUtils {

	public static final String ACCOUNT_EXISTS_CODE = "001";

	public static final String ACCOUNT_EXIST_MESSAGE = "This user already has an account created!";

	public static final String ACCOUNT_CREATION_SUCCESS = "002";
	
	public static final String ACCOUNT_CREATION_MESSAGE = "Account has been created successfully!";
	
	public static final String ACCOUNT_NOT_EXIST_CODE = "003";
	
	public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User provided a account number does not exist..";
	
	public static final String ACCOUNT_FOUND_CODE = "004";
	
	public static final String ACCOUNT_FOUND_MESSEAGE = "User Found this Account Number";

	public static final String ACCOUNT_CREDIT_SUCCESS = "005";

	public static final String ACCOUNT_CREDIT_MESSAGE = "Account has been credited successfully!";

	public static final String ACCOUNT_DEBIT_SUCCESS = "006";

	public static final String ACCOUNT_DEBIT_MESSAGE = "Account has been debited successfully!";

	public static final String INSUFFICIENT_BALANCE_CODE = "007";

	public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance in your Account !";



	public static String generateAccountNumber() {

		/* 2023 + random six digit 2023112233 */
		Year currentYear = Year.now();

		int min = 100000;
		int max = 999999;

		// generate random number between min and max
		int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

		// convert the current and randomNUmber to String, then concatinate then
		String year = String.valueOf(currentYear);
		String randomNumber = String.valueOf(randNumber);

		StringBuilder accountNumber = new StringBuilder();
		return accountNumber.append(year).append(randNumber).toString();
	}
}
