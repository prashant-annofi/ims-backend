package com.annofi.ims.dto.token;

import lombok.Data;

@Data
public class Token {
	private String token;
	private long expireTime;
}
