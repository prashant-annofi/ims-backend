package com.annofi.ims.config.multitenant;

import lombok.Data;

@Data
public class DataSourceProperties {
	private String name;
	private String url;
	private String userName;
	private String password;
	private String databaseDriver;
}
