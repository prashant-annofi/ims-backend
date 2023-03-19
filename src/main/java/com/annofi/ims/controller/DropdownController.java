package com.annofi.ims.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annofi.ims.dto.DropDownDTO;
import com.annofi.ims.model.Role;
import com.annofi.ims.model.User;
import com.annofi.ims.repository.RoleRepository;

@RestController
@RequestMapping("/dropdown/")
@CrossOrigin(origins = "*")
public class DropdownController {
	@Autowired
	private RoleRepository roleRepository;
	
	@GetMapping("/roles")
    public List<DropDownDTO> getUsers(){
		List<DropDownDTO> data = new ArrayList<DropDownDTO>();
    	for (Role item : roleRepository.findAll()) {
			DropDownDTO downDTO = new DropDownDTO();
			downDTO.setId((long)item.getId());
			downDTO.setName(item.getName());
			data.add(downDTO);
		}
    	return data;
    }
}
