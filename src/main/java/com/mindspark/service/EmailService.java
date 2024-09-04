package com.mindspark.service;

import com.mindspark.dto.EmailDetails;

public interface EmailService {

	void sendEmailAlert(EmailDetails emailDetails);
}
