package com.annofi.ims.security;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.annofi.ims.dto.LoginViewModel;
import com.annofi.ims.model.User;
import com.annofi.ims.service.UserService;
 
@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
     
    @Autowired
    private UserService userService;
    
    public CustomLoginFailureHandler(UserService userService) {
    	this.userService = userService;
    }
     
    //@Override
    public String onAuthenticationFailure(LoginViewModel request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
    	String message = null;
        String username = request.getUsername();
        User user = userService.getByUserName(username);
        
        if (user != null) {
            if (user.isActive() && user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempts(user);
                } else {
                    userService.lock(user);
                    //exception = new LockedException("Your account has been locked due to 10 failed attempts."
                    //        + " It will be unlocked after 1 hours.");
                    message = "Your account has been locked due to 10 failed attempts, It will be unlocked after an hour.";
                }
            } else if (!user.isAccountNonLocked()) {
                if (userService.unlockWhenTimeExpired(user)) {
                    //exception = new LockedException("Your account has been unlocked. Please try to login again.");
                	message = "Your account has been unlocked.";
                }
                else {
                	message = "Your account has been locked. It will unlock after an hour. Please consult with the admin.";
                }
            }
             
        }
        return message;
    }
 
}
