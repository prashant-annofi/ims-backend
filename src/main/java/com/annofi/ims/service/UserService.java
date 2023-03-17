package com.annofi.ims.service;

import com.annofi.ims.dto.UserInformationDTO;
import com.annofi.ims.exception.RestMessageException;
import com.annofi.ims.model.User;
import com.annofi.ims.repository.UserRepository;
import com.annofi.ims.exception.ResourceNotFoundException;
import com.annofi.ims.config.multitenant.DataSourceConfig;
import com.annofi.ims.config.multitenant.WithDataBase;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
	@Autowired
    private PasswordEncoder passwordEncoder;
	
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private DataSourceConfig dataSourceConfig;
    
    private static final String regex = "^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{8,20}$";
    
    public static final int MAX_FAILED_ATTEMPTS = 10;
    
    private static final long LOCK_TIME_DURATION = 60 * 60 * 1000; // 1 hours
    
    @WithDataBase(" ")
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.getUsername());
    }
     
    public void resetFailedAttempts(String username) {
    	userRepository.updateFailedAttempts(0, username);
    }
    
    @WithDataBase(" ")
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
         
        userRepository.save(user);
    }
    
    @WithDataBase(" ")
    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
         
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
             
            userRepository.save(user);
             
            return true;
        }
         
        return false;
    }
    
    @WithDataBase(" ")
    public List<User> findAll()
    {
        return userRepository.findAll();
    }

    @WithDataBase(" ")
    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }
	
    @WithDataBase(" ")
    public User getByUserName(String username) {
    	User users = userRepository.findTop1ByOrderByIdAsc();
		return userRepository.findByUsername(username);
	}
    
    @WithDataBase(" ")
    public void saveUser(User user) {
    	User users = userRepository.findTop1ByOrderByIdAsc();
        userRepository.save(user);
    }
    
    @WithDataBase(" ")
    public Authentication auth(AuthenticationManager authManager, UsernamePasswordAuthenticationToken authenticationToken) {
    	try {
	    	Authentication auth = authManager.authenticate(authenticationToken);
	        return auth;
    	}
    	catch (Exception e) {
			return null;
		}
    }
    
    public void addTenantCRUD() {
    	try {
			dataSourceConfig.resetPrimaryDataSource();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @WithDataBase(" ")
    public UserInformationDTO getUserInformationByUsername(String username)
    {
        //TODO change to single query
        User user = userRepository.findByUsername(username);
        
        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setUsername(user.getUsername());

        return userInformationDTO;
    }
    
    @WithDataBase(" ")
    @Transactional
    public User createInitialUser(User user) throws RestMessageException
    {
    	// Compile the ReGex
    	Pattern p = Pattern.compile(regex);
        if(user.getPassword() != null) {
            if(!("".equals(user.getPassword().replaceAll(" ","")))) {
                User softDeletedUser = userRepository.findByUsernameAndDeletedTrue(user.getUsername());
                if(softDeletedUser != null) {
	                softDeletedUser.setDeleted(false);
	            }
                Matcher m = p.matcher(user.getPassword());
                if(m.matches()) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }else {
                 	throw new RuntimeException("Password must be minimum 8 characters, an upper case alphabet,requires alpha-numeric and special characters.");
                }
                user.setActive(true);
                if(user.getUsername().length() >= 5) {
                	user.setUsername(user.getUsername().trim());
                }else {
                	throw new RuntimeException("Username should be greater than 5 characters");
                }
                return userRepository.save(user);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    @WithDataBase(" ")
    @Transactional
    public User updateUser(User user) throws RestMessageException
    {
    	// Compile the ReGex
    	Pattern p = Pattern.compile(regex);
        if(user.getPassword() != null) {
            if(!("".equals(user.getPassword().replaceAll(" ","")))) {
                User softDeletedUser = userRepository.findByUsernameAndDeletedTrue(user.getUsername());
                if(softDeletedUser != null) {
	                softDeletedUser.setDeleted(false);
	            }
                Matcher m = p.matcher(user.getPassword());
                if(m.matches()) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }else {
                 	throw new RuntimeException("Password must be minimum 8 characters, an upper case alphabet,requires alpha-numeric and special characters.");
                }
                user.setActive(true);
                if(user.getUsername().length() >= 5) {
                	user.setUsername(user.getUsername().trim());
                }else {
                	throw new RuntimeException("Username should be greater than 5 characters");
                }
                return userRepository.save(user);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    @Transactional
    @WithDataBase(" ")
    public User updateUserInformation(Long userId, User userDTO, String deleted)
    {
        return userRepository.findById(userId).map(user ->{
        	if(user.getUsername().length()>=5) {
        		if("deleted".equals(deleted)) {
        			user.setDeleted(false);
        		}
        		user.setUsername(user.getUsername().trim());
        	}else {
            	throw new RuntimeException("Username should be greater than 5 characters");
            }
            user.setActive(user.isActive());
            if(user.getPassword() != null)
            {
                if(!("".equals(user.getPassword().replaceAll(" ","")))) {
                	// Compile the ReGex
                	Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(user.getPassword());
               	 	if(m.matches()) {
               	 		user.setPassword(passwordEncoder.encode(user.getPassword()));
               	 	}else {
               	 		throw new RuntimeException("Password must be minimum 8 characters, an upper case alphabet, requires alpha-numeric and special characters."); 
               	 	}
            	}
            }
            user.setContactNumber(userDTO.getContactNumber());
            user.setEmail(userDTO.getEmail());
            user.setGender(userDTO.getGender());
            user.setRemarks(userDTO.getRemarks());
            user.setRoles(userDTO.getRoles());
            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException(""));
    }
}
