package com.mindspark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EmailDetails {

	private String recipent;
	private String messageBody;
	private String subject;
	private String attachment;

}
