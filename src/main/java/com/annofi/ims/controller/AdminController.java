package com.annofi.ims.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annofi.ims.model.User;
import com.annofi.ims.service.UserService;

@RestController
@RequestMapping("/admin/")
@CrossOrigin(origins = "*")
public class AdminController {
	@Autowired
	private UserService userService;
	
    @GetMapping("/test")
    public List<User> test(){
    	return userService.findAll();
    }
}
