package com.mindspark.service;

import java.time.LocalDate;
import java.util.List;

import com.mindspark.dto.BankResponse;
import com.mindspark.dto.CreditDebitRequest;
import com.mindspark.dto.EnquiryRequest;
import com.mindspark.dto.UserRequest;
import com.mindspark.model.User;

public interface UserService {

	BankResponse createAccount(UserRequest userRequest);
	
	BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
	
	String nameEnquiry(EnquiryRequest request);
	
	public List<User>getAllUsers(LocalDate date);
	
	public User getAccountDetail(String accountNumber);

	User updateUser(User user);

	User findByUserId(long id);

	List<User> findByFirstName(String firstName);

	User findUserByEmail(String email);

	List<User> getAllUser();

	public BankResponse creditAccount(CreditDebitRequest request);

	public BankResponse debitAccount(CreditDebitRequest request);
}
