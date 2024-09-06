package com.mindspark.service;

import com.mindspark.dto.*;
import com.mindspark.model.User;
import com.mindspark.repository.UserRepository;
import com.mindspark.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
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
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
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

    //balance Enquiry, name Enquiry, Credit and debit, transfer

    public List<User> getAllUsers(LocalDate date) {
        return userRepository.findUsersCreatedAt(date);
    }

    @Override
    public User getAccountDetail(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
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
        // Check if the provided account number exists in the database
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            System.out.println("Account does not exist: " + request.getAccountNumber()); // Debugging log
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;

        }
        // If the account exists, proceed
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
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
        //System.out.println(userToCredit);

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
