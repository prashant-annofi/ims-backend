package com.annofi.ims.config.multitenant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annofi.ims.message.RestMessage;
import com.annofi.ims.message.success.RestSuccessMessage;
import com.annofi.ims.service.UserService;

@RestController
@RequestMapping("/multitenant/")
@CrossOrigin(origins = "*")
public class MultitenantController {
	@Value("${upload.path}")
	private String uploadPath;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/append")
	//public RestMessage addTanent(@Valid @RequestBody Clients clients) {
	public RestMessage addTanent() {
		try {
			Files.createDirectories(Paths.get(uploadPath + "/" + "zpt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userService.addTenantCRUD();
		return new RestSuccessMessage("jbhdfjkdsf");
	}
	
	@GetMapping("/listUpdate")
	public void listUpdate() {
		//return addTenantCRUD(multitenantDTO);
		List<MultitenantDTO> tenants = new ArrayList<MultitenantDTO>();
		
		MultitenantDTO multitenantDTO = new MultitenantDTO();
		multitenantDTO.setName("demo");
		multitenantDTO.setDbName("rms_demo");
		multitenantDTO.setUserName("root");
		multitenantDTO.setPassword("password");
		multitenantDTO.setSubdomain("");
		tenants.add(multitenantDTO);
		
		try {
			Connection conn = RoutingDataSource.getMainConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM clients c WHERE c.is_active = 1";
			statement.execute(sql);
			ResultSet rs = statement.executeQuery(sql);
			while(rs.next()) {
				MultitenantDTO item = new MultitenantDTO();
				item.setName(rs.getString("name"));
				item.setDbName(rs.getString("db_name"));
				item.setUserName(rs.getString("user_name"));
				item.setPassword(rs.getString("password"));
				item.setSubdomain(rs.getString("subdomain"));
				tenants.add(item);
			}
			//conn.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println("here: "+ e1.getMessage());
			e1.printStackTrace();
		}
		
		RoutingDataSource.setTenantList(tenants);
	}
}
