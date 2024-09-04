package com.mindspark.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.mindspark.dto.CreditDebitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindspark.dto.BankResponse;
import com.mindspark.dto.EnquiryRequest;
import com.mindspark.dto.UserRequest;
import com.mindspark.model.User;
import com.mindspark.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/createuser")
	public BankResponse createAccount(@RequestBody UserRequest userRequest) {
		return userService.createAccount(userRequest);
	}

	@GetMapping("/allUsers")
	public List<User> getAllUsers(){
		return userService.getAllUser();
	}

	@GetMapping
	public List<User> getAllUserWithDate(@RequestParam(value = "date") String date){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
	    LocalDate date1 = LocalDate.parse(date, formatter);
	    return userService.getAllUsers(date1);
	}

	@PostMapping("/balanceEnquiry")
	public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
		return userService.balanceEnquiry(enquiryRequest);
	}

	@GetMapping("/nameEnquiry")
	public String nameEnquiry(@RequestBody EnquiryRequest request){
		return userService.nameEnquiry(request);
	}
	
	
	@GetMapping("/{accountNumber}")
	public User getAccountDetail(@PathVariable String accountNumber) {
		System.out.println("-----");
		return userService.getAccountDetail(accountNumber);
	}
	
	@PutMapping("/{id}")
	public String updateUser(@RequestBody User user) {
	    userService.updateUser(user);
	    return "Record is updated";
	}
	@GetMapping("users/{id}")
	public User getUser(@PathVariable long id) {
		return userService.findByUserId(id);
	}
	
	@GetMapping("/name/{firstName}")
	public List<User> getUserByName(@PathVariable String firstName) {
		return userService.findByFirstName(firstName);
	}
	
	@GetMapping("/mail/{email}")
	public User getUserByEmail(@PathVariable String email) {
		return userService.findUserByEmail(email);
	}

	@PostMapping("/creditAccount")
	public BankResponse creditAcc(@RequestBody CreditDebitRequest request){
		return userService.creditAccount(request);
	}

	@PostMapping("/debitAccount")
	public BankResponse debitAcc(@RequestBody CreditDebitRequest request){
		return userService.debitAccount(request);
	}
	
}
