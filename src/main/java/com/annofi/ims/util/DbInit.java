package com.annofi.ims.util;

import com.annofi.ims.model.Operation;
import com.annofi.ims.model.Role;
import com.annofi.ims.model.User;
import com.annofi.ims.repository.OperationRepository;
import com.annofi.ims.repository.RoleRepository;
import com.annofi.ims.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DbInit implements CommandLineRunner {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private OperationRepository operationRepository;
    
    public DbInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if(userRepository.findAll().isEmpty()) {
            // Create users
            User admin = new User("admin", passwordEncoder.encode("admin123"), "ADMIN", "ACCESS_TEST1,ACCESS_TEST2"); 
        	
        	List<Operation> operations = new ArrayList<Operation>();
        	if(operationRepository.findAll().isEmpty()) {
        		Operation operation1 = new Operation("Create User", "user", "To create a new user.");
            	Operation operation2 = new Operation("Edit User", "user", "To update an existing user's information.");
            	Operation operation3 = new Operation("Read User", "user", "To read a user's info.");
            	Operation operation4 = new Operation("Delete User", "user", "To delete a selected user.");
        		
        		/*Operation operation1 = new Operation((short)1, "Create User", "user", "To create a new user.");
            	Operation operation2 = new Operation((short)2, "Edit User", "user", "To update an existing user's information.");
            	Operation operation3 = new Operation((short)3, "Read User", "user", "To read a user's info.");
            	Operation operation4 = new Operation((short)4, "Delete User", "user", "To delete a selected user.");*/
            	
            	operations.add(operation1);
            	operations.add(operation2);
            	operations.add(operation3);
            	operations.add(operation4);
            	
            	//operationRepository.saveAll(operations);
            }
        	
            Set<Role> roleSet = new HashSet<Role>();
            Role role = new Role();
            //role.setId((short)1);
        	role.setName("ADMIN");
        	role.setOperations(new HashSet<Operation>(operations));
        	roleSet.add(role);
        	admin.setRoleSet(roleSet);
        	
            try {
            	userRepository.save(admin);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}           
        }
    }
}
