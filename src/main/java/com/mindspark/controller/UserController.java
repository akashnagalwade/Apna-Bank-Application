package com.mindspark.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.mindspark.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindspark.model.User;
import com.mindspark.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Account Management APIs")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(
			summary = "Create New User Account",
			description = "Creating a new User and Assigning a Account ID"
	)
	@ApiResponse(
			responseCode = "200",
			description = "http Status 200 CREATED"
	)

	@PostMapping("/login")
	public BankResponse login(@RequestBody LoginDto loginDto){
		return userService.login(loginDto);

	}


	@PostMapping("/create")
	public BankResponse createAccount(@Valid @RequestBody UserRequest userRequest) {
		return userService.createAccount(userRequest);
	}

	@Operation(
			summary = "Get All The User Details",
			description = "Getting all the users with information"
	)
	@ApiResponse(
			responseCode = "201",
			description = "http Status 201 ACCEPTED"
	)
	@GetMapping("/allUsers")
	public List<User> getAllUsers(){
		return userService.getAllUser();
	}

	@Operation(
			summary = "Get All User With Created Date",
			description = "Getting all user which are created on that date"
	)
	@ApiResponse(
			responseCode = "202",
			description = "http Status 202 ACCEPTED"
	)
	@GetMapping
	public List<User> getAllUserWithCreatedDate(@RequestParam(value = "date") String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
	    LocalDate date1 = LocalDate.parse(date, formatter);
	    return userService.getAllUsersWhichAreCreatedOnDate(date1);
	}

	@Operation(
			summary = "Enquiry About Balance",
			description = "Saving the given request to enquire the Balance"
	)
	@ApiResponse(
			responseCode = "203",
			description = "http Status 203 SUCCESS"
	)
	@PostMapping("/balanceEnquiry")
	public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
		return userService.balanceEnquiry(enquiryRequest);
	}

	@PostMapping("/nameEnquiry")
	public String nameEnquiry(@RequestBody EnquiryRequest request){
		return userService.nameEnquiry(request);
	}

	@GetMapping("/{accountNumber}")
	public User getAccountDetail(@PathVariable String accountNumber) {
		System.out.println("-----");
		return userService.getAccountDetail(accountNumber);
	}
	
	@PutMapping("/update")
	public String updateUser(@RequestBody User user) {
	    userService.updateUser(user);
	    return "Record is updated";
	}
	@GetMapping("user/{id}")
	public User getUser(@PathVariable long id) {
		return userService.findByUserId(id);
	}
	
	@GetMapping("user/name/{firstName}")
	public List<User> getUserByName(@PathVariable String firstName) {
		return userService.findByFirstName(firstName);
	}
	
	@GetMapping("user/mail/{email}")
	public User getUserByEmail(@PathVariable String email) {
		return userService.findUserByEmail(email);
	}

	@PostMapping("admin/creditAccount")
	public BankResponse creditAcc(@RequestBody CreditDebitRequest request){
		return userService.creditAccount(request);
	}

	@PostMapping("admin/debitAccount")
	public BankResponse debitAcc(@RequestBody CreditDebitRequest request){
		return userService.debitAccount(request);
	}

	@PostMapping("admin/transfer")
	public BankResponse transfer(@RequestBody TransferRequest request){
		return userService.transfer(request);
	}

	
}
