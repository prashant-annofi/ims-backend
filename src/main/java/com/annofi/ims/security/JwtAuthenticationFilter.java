package com.annofi.ims.security;

import com.annofi.ims.dto.LoginViewModel;
import com.annofi.ims.dto.UserInformationDTO;
import com.annofi.ims.model.User;
import com.annofi.ims.service.UserService;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private UserService userService;

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    /*
     * Trigger when we issue POST request to /login
     * We also need to pass in {"username":"dan", "password":"dan123"} in the
     * request body
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // Grab credentials and map them to login viewmodel
        LoginViewModel credentials = null;
        try {
            credentials = new ObjectMapper().readValue(request.getInputStream(), LoginViewModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create login token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                credentials.getUsername().trim(),
                credentials.getPassword(),
                new ArrayList<>());

        // Authenticate user
        try {
        	Authentication auth = userService.auth(authenticationManager, authenticationToken);
        	if(auth != null) {
        		return auth;
        	}
        	else {
        		try {
					this.unsuccessfulAuthentication(request, response, null, credentials);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		throw new RuntimeException("The user does not exist.");
        	}
        } 
        catch (RuntimeException ex) {
			try {
				this.unsuccessfulAuthentication(request, response, null, credentials);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throw new RuntimeException("The user does not exist.");
        }
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        // Grab principal
        UserPrincipal principal = (UserPrincipal) authResult.getPrincipal();

        User user = userService.findByUsername(principal.getUsername());
        user.setFailedAttempt(0);
        userService.saveUser(user);
        
        if(user != null) {
        	Date expireTime = new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME);

            // Create JWT Token
            String token = JWT.create()
                    .withSubject(principal.getUsername())
                    .withExpiresAt(expireTime)
                    .sign(HMAC512(JwtProperties.SECRET.getBytes()));

            UserInformationDTO userInformationDTO = new UserInformationDTO();
            try {
                userInformationDTO = userService.getUserInformationByUsername(principal.getUsername());
            } catch (Exception ex) {
                // ex.printStackTrace();
                System.out.println("Exception for default users");
            }

            ObjectMapper mapper = new ObjectMapper();

            String userInformationJsonString = mapper.writeValueAsString(userInformationDTO);

            String authorizationJson = "{\"token\" : \"" + token + "\"," + "\"expire\" : \"" + expireTime.getTime() + "\"}";
            StringBuilder finalJson = new StringBuilder();

            finalJson.append("{\"authorization\" :").append(authorizationJson).append(",")
                    .append("\"user\" :").append(principal).append(",")
                    .append("\"user_information\" :").append(userInformationJsonString)
                    .append("}");
            // Add token in response
            response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + token);
            response.setContentType("application/json");

            response.getWriter().print(finalJson);
        }
        else {
        	String message = "The username or password entered is incorrect.";
			this.doFilterInternal(request, response, chain, message);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        FilterChain filterChain = new FilterChain() {

            @Override
            public void doFilter(ServletRequest request, ServletResponse response)
                    throws IOException, ServletException {
                // TODO Auto-generated method stub

            }
        };
        this.doFilterInternal(request, response, filterChain, null);
    }

	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed, LoginViewModel credentials) throws IOException, ServletException {		
		//customLoginFailureHandler.onAuthenticationFailure(request, response, failed);
		
		String message = "";
		FilterChain filterChain = new FilterChain() {

			@Override
			public void doFilter(ServletRequest request, ServletResponse response)
					throws IOException, ServletException {
				// TODO Auto-generated method stub

			}
		};
		
		AuthenticationException authenticationException = new AuthenticationException("Failed attempts exceeded"){};
		try {
			CustomLoginFailureHandler customLoginFailureHandler = new CustomLoginFailureHandler(userService);
			message = customLoginFailureHandler.onAuthenticationFailure(credentials, response, authenticationException);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(message == null) {
			message = "The username or password entered is incorrect.";
		}
		this.doFilterInternal(request, response, filterChain, message);
	}

	private void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain, String message) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("message", message);

		httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

		mapper.writeValue(httpServletResponse.getWriter(), errorDetails);
	}
}
