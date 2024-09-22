package com.mindspark.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

	@NotBlank(message = "First name is required.")
	@Size(max = 50, message = "First name must not exceed 50 characters.")
	private String firstName;

	@NotBlank(message = "Last name is required.")
	@Size(max = 50, message = "Last name must not exceed 50 characters.")
	private String lastName;

	@Size(max = 50, message = "Other name must not exceed 50 characters.")
	private String otherName;

	@NotBlank(message = "Gender is required.")
	@Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other.")
	private String gender;

	@NotBlank(message = "Address is required.")
	@Size(max = 100, message = "Address must not exceed 100 characters.")
	private String address;

	@NotBlank(message = "State of origin is required.")
	@Size(max = 50, message = "State of origin must not exceed 50 characters.")
	private String stateOfOrigin;

	@NotBlank(message = "Email is required.")
	@Email(message = "Email should be valid.")
	private String email;

	@NotBlank(message = "Password is required.")
	@Size(min = 6, message = "Password must be at least 6 characters long.")
	private String password;

	@NotBlank(message = "Phone number is required.")
	@Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits.")
	private String phoneNumber;

	@Pattern(regexp = "^\\d{10,15}$", message = "Alternate phone number must be between 10 and 15 digits.")
	private String alternatePhoneNumber;
}
