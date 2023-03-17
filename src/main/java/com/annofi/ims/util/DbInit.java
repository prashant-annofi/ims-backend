package com.annofi.ims.util;

import com.annofi.ims.model.User;
import com.annofi.ims.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DbInit implements CommandLineRunner {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    
    public DbInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if(userRepository.findAll().isEmpty()) {
            // Create users
            User admin = new User("admin", passwordEncoder.encode("admin123"), "ADMIN", "ACCESS_TEST1,ACCESS_TEST2");        
           
            try {
            	userRepository.save(admin);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}           
        }
    }
}
