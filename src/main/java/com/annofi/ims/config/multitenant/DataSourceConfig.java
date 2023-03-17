package com.annofi.ims.config.multitenant;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
	private String defaultDB = "";
	public static RoutingDataSource routingDataSource = new RoutingDataSource();
	
	@Bean
	public DataSource getPrimaryDataSource() throws IOException, ParseException {
		routingDataSource.setMainConnection();
		Map<Object, Object> dataSources = this.buildDataSources();
		routingDataSource.setTargetDataSources(dataSources);
		
		routingDataSource.setDefaultTargetDataSource(dataSources.get("ims"));
		return routingDataSource;
	}
	
	public DataSource resetPrimaryDataSource() throws IOException, ParseException {
		routingDataSource = new RoutingDataSource();
		Map<Object, Object> dataSources = this.buildDataSources();
		routingDataSource.setTargetDataSources(dataSources);
		
		routingDataSource.setDefaultTargetDataSource(dataSources.get(defaultDB));
		return routingDataSource;
	}
	
	private Map<Object, Object> buildDataSources() throws IOException, ParseException{
		List<MultitenantDTO> tenants = new ArrayList<MultitenantDTO>();
		
		MultitenantDTO multitenantDTO2 = new MultitenantDTO();
		multitenantDTO2.setName("demo");
		multitenantDTO2.setDbName("rms_demo");
		multitenantDTO2.setUserName("root");
		multitenantDTO2.setPassword("nirvana");
		multitenantDTO2.setSubdomain("");
		tenants.add(multitenantDTO2);
		
		try {
			Connection conn = routingDataSource.getMainConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM clients c WHERE c.is_active = TRUE";
			//String sql = "create database mew2";
			createDemoDataBase(conn);
			statement.execute(sql);
			ResultSet rs = statement.executeQuery(sql);
			while(rs.next()) {
				MultitenantDTO multitenantDTO = new MultitenantDTO();
				multitenantDTO.setName(rs.getString("name"));
				multitenantDTO.setDbName(rs.getString("db_name"));
				multitenantDTO.setUserName(rs.getString("user_name"));
				multitenantDTO.setPassword(rs.getString("password"));
				multitenantDTO.setSubdomain(rs.getString("subdomain"));
				tenants.add(multitenantDTO);
			}
			conn.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println("here: "+ e1.getMessage());
			e1.printStackTrace();
		}
		
		routingDataSource.setTenantList(tenants);

		final Map<Object, Object> result = new HashMap<>();
		for (MultitenantDTO sourceType : tenants) {
			DataSource ds = this.buildDataSource(sourceType);
			result.put(sourceType.getDbName(), ds);
		}
		
		setDefaultDB(tenants);
		
		return result;
	}
	
	private void setDefaultDB(List<MultitenantDTO> tenants) {
		for (MultitenantDTO sourceType : tenants) {
			DataSource ds = this.buildDataSource(sourceType);
			if(getNumberOfTables(ds, sourceType.getDbName()) < 10) {
				if("rms_demo".equals(defaultDB)) {
					defaultDB = sourceType.getDbName();
					break;     //that's why same loop called twice
				}
			}
			else {
				defaultDB = "rms_demo"; 
			}
		}
	}
	
	public DataSource buildDataSource(MultitenantDTO sourceType) {
		final HikariConfig config = new HikariConfig();
		
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setName(sourceType.getDbName());
		dataSourceProperties.setUrl("jdbc:mysql://localhost:3306/" + dataSourceProperties.getName() + "?useSSL=false&autoReconnect=true&sessionVariables=sql_mode=''");
		dataSourceProperties.setUserName(sourceType.getUserName());
		dataSourceProperties.setPassword(sourceType.getPassword());
		dataSourceProperties.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
		
		
		config.setJdbcUrl(dataSourceProperties.getUrl());
		config.setUsername(dataSourceProperties.getUserName());
		config.setPassword(dataSourceProperties.getPassword());
		config.setDriverClassName(dataSourceProperties.getDatabaseDriver());
		config.setAutoCommit(false);
		
		return new HikariDataSource(config);	
	}
	
	private Long getNumberOfTables(DataSource dataSource, String dbName) {
		Long a = 0L;
		Connection conn;
		try {
			conn = dataSource.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT count(*) AS TOTALNUMBEROFTABLES "
					+ "FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = " + "'" + dbName + "'";
			statement.execute(sql);
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				 a = Long.valueOf(rs.getInt(1));
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}
	
	private void createDemoDataBase(Connection con) {
		try {
			Statement statement = con.createStatement();
			String sql = "create database rms_demo";
			statement.execute(sql);
			con.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println("here: "+ e1.getMessage());
			e1.printStackTrace();
		}
	}
}
