package com.annofi.ims.security;

public class JwtProperties {
    public static final String SECRET = "@nnofiTechn0logies";
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 day 
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
