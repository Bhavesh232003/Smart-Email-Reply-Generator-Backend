package com.email.email.writer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
       @NotBlank
       private String username;
       
       @NotBlank
       private String password;
       
//       @NotBlank
//       private String email;
       
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
       
}
