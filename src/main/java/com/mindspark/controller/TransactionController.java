package com.mindspark.controller;

import com.itextpdf.text.DocumentException;
import com.mindspark.model.Transaction;
import com.mindspark.service.BankStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionController {

    @Autowired
    private BankStatement bankStatement;

    @PostMapping()
    public List<Transaction> createBankStatement(@RequestParam String accountNumber,
                                                 @RequestParam String startDate,
                                                 @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }

}
