package com.mindspark.service;

import com.mindspark.config.JwtTokenProvider;
import com.mindspark.dto.*;
import com.mindspark.exception.*;
import com.mindspark.model.Role;
import com.mindspark.model.User;
import com.mindspark.repository.UserRepository;
import com.mindspark.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService service;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        // Validate required fields
//        if (userRequest.getFirstName() == null || userRequest.getFirstName().isEmpty()) {
//            throw new InvalidUserInputException("First name cannot be null or empty.");
//        }
//        if (userRequest.getPhoneNumber() == null || userRequest.getPhoneNumber().isEmpty()) {
//            throw new InvalidUserInputException("phone number cannot be null or empty.");
//        }
        /*
         * creating an account- saving a new user into the database if the user already
         * has an account then
         */
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null).build();

        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
                .role(Role.valueOf("ROLE_ADMIN"))
                .status("ACTIVE").build();

        User saveUser = userRepository.save(newUser);

        // set email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipent(saveUser.getEmail())
                .subject("Account Creation.")
                .messageBody("Congratulation Your Account has been successfully created.\n Your Account Details: \n"
                        + "Account Name: " + saveUser.getFirstName() + " " + saveUser.getLastName() + " "
                        + saveUser.getOtherName() + "\n Account Number: " + saveUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(saveUser.getAccountBalance())
                        .accountNumber(saveUser.getAccountNumber())
                        .accountName(saveUser.getFirstName() + " " + saveUser.getLastName() + " " + saveUser.getOtherName())
                        .build())
                .build();

    }

    public BankResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = null;
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            EmailDetails loginAlert = EmailDetails.builder()
                    .subject("You're logged in!")
                    .recipent(loginDto.getEmail())
                    .messageBody("You logged into your account. If you did not initiate this request, please contact your bank.")
                    .build();

            emailService.sendEmailAlert(loginAlert);

            return BankResponse.builder()
                    .responseCode("Login Success")
                    .responseMessage(jwtTokenProvider.generateToken(authentication))
                    .build();
        } catch (Exception e) {
            // Handle exception (e.g., logging, returning an error response)
            return BankResponse.builder()
                    .responseCode("Login Failed")
                    .responseMessage(e.getMessage())
                    .build();
        }
    }

    //balance Enquiry, name Enquiry, Credit and debit, transfer

    public List<User> getAllUsersWhichAreCreatedOnDate(LocalDate date) {
        // Check if the provided date is valid (you can customize this validation as needed)
        if (date == null) {
            throw new InvalidDateException("The provided date cannot be null.");
        }
        List<User> usersCreatedAt = userRepository.findUsersCreatedAt(date);

        // Check if the list is null or empty
        if (usersCreatedAt == null || usersCreatedAt.isEmpty()) {
            throw new UsersNotFoundException("No users found for the provided date: " + date);
        }
        return usersCreatedAt;
    }


    @Override
    public User getAccountDetail(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber);

        if (user == null) {
            throw new AccountNotFoundException("The account number '" + accountNumber + "' does not exist.");
        }
        return user;
    }


    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).get();
        if(existingUser == null){
            throw new UserNotFoundException("User not found with this id "+user.getId());
        }
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setOtherName(user.getOtherName());
        existingUser.setGender(user.getGender());
        existingUser.setAddress(user.getAddress());
        existingUser.setCreatedAt(user.getCreatedAt());
        existingUser.setStateOfOrigin(user.getStateOfOrigin());
        existingUser.setAccountBalance(user.getAccountBalance());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(existingUser);
    }

    @Override
    public User findByUserId(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> findByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }


    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        // Check if the provided account number exists in the database
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        if (!isAccountExist) {
            System.out.println("Account does not exist: " + enquiryRequest.getAccountNumber()); // Debugging log
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // If the account exists, proceed
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSEAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .build())
                .build();
    }


    @Override
    public String nameEnquiry(EnquiryRequest request) {
        // Validate the request object
        if (request == null || request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Request or first name must not be null or empty");
        }

        String firstName = request.getFirstName().trim();

        // Find users by first name
        List<User> foundUsers = userRepository.findByFirstName(firstName);

        // Check if the list is empty
        if (foundUsers.isEmpty()) {
            // Log the situation if needed
            System.out.println("Account does not exist: " + firstName); // For debugging
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        // Assuming you want the first user if multiple users are found
        User foundUser = foundUsers.get(0);
        // Return the full name
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }



    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
//		check if account exist
        boolean isAccExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);
        System.out.println(userToCredit);

        //            save transaction
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        service.saveTransaction(transactionDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDIT_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDIT_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())
                .build();
    }

    public BankResponse debitAccount(CreditDebitRequest request) {
//		check if account exist or not
//		check the amount intend to withdraw is not more than the current
//		account balance
        boolean isAccExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();

        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            //            save transaction
            TransactionDetails transactionDetails = TransactionDetails.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();

            service.saveTransaction(transactionDetails);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBIT_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBIT_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {

//        get the account to debit( check if it is exists
//        check if i am debiting is not more than current balance
//        debit the account
//        get the account to debit
//        credit the account

        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getBeneficiaryAccountNumber());

        if (!isDestinationAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUserName = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName();
        userRepository.save(sourceAccountUser);

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipent(sourceAccountUser.getEmail())
                .messageBody("the sum of " + request.getAmount() + " has been deducted from your account ! your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);

        //            save transaction

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        service.saveTransaction(transactionDetails);

        User beneficiaryAccountUser = userRepository.findByAccountNumber(request.getBeneficiaryAccountNumber());
        beneficiaryAccountUser.setAccountBalance(beneficiaryAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(beneficiaryAccountUser);

//        String recepientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName();

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipent(beneficiaryAccountUser.getEmail())
                .messageBody("the sum of " + request.getAmount() + " has been sent to your account from " + sourceUserName + " your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);

        //            save transaction

        TransactionDetails transactionDetail = TransactionDetails.builder()
                .accountNumber(beneficiaryAccountUser.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .build();

        service.saveTransaction(transactionDetail);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFULL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFULL_MESSAGE)
                .accountInfo(null)
                .build();
    }
}