package com.annofi.ims.config.multitenant;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MultitenantDTO {
	
	private String name;
	
	private String subdomain;
	
	@JsonProperty("db_name")
	private String dbName;
	
	@JsonProperty("user_name")
	private String userName;
	
	private String password;
	
	@JsonProperty("license_expiry_date")
	private LocalDate licenseExpiryDate;
}
