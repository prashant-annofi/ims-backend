package com.annofi.ims.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annofi.ims.model.User;
import com.annofi.ims.service.UserService;
import com.annofi.ims.exception.RestMessageException;
import com.annofi.ims.message.RestMessage;
import com.annofi.ims.message.error.RestErrorMessage;
import com.annofi.ims.message.success.RestSuccessMessage;

@RestController
@RequestMapping("/master/")
@CrossOrigin(origins = "*")
public class UserController {
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private UserService userService;
	
    @GetMapping("/view")
    public List<User> getUsers(){
    	return userService.findAll();
    }
    
    @PostMapping("/users")
    public RestMessage createInitialUser(@Valid @RequestBody User user, Locale locale) throws RestMessageException
    {
    	try {
	    	user = userService.createInitialUser(user);
	    	String successMessage =  messageSource.getMessage("User Successfully added", null, locale);
	    	return new RestSuccessMessage(successMessage);
    	}
    	catch (Exception ex) {
			if(ex.getMessage().contains("users.UK")) {
				return new RestErrorMessage("The user-name has already been used.");
			}
			/*else if(ex.getMessage().contains("userprofiles.UK")) {
				return new RestErrorMessage("Employee email must be unique");
			}*/
			else
	        {
	            return new RestErrorMessage(ex.getMessage());
	        }
		}
    }
    
    @PutMapping("/users/{userId}")
    public RestMessage updateUserInformation(@PathVariable Long userId, @Valid @RequestBody User userDTO, Locale locale)
    {
    	try {
	    	if(userService.updateUserInformation(userId, userDTO, null) != null)
	        {
	            String successMessage =  messageSource.getMessage("user.update.success", null, locale);
	            return new RestSuccessMessage(successMessage);
	        }
	        else
	        {
	            return new RestErrorMessage("Internal Server Error");
	        }
    	}
    	catch (Exception ex) {
    		if(ex.getMessage().contains("username")) {
				return new RestErrorMessage(messageSource.getMessage("user.unique.error", null, locale));
			}
    		else
	        {
	            return new RestErrorMessage("Internal Server Error");
	        }
		}
    }
}
