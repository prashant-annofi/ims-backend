package com.annofi.ims.config.multitenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
	private static final ThreadLocal<Stack<String>> ctx = new ThreadLocal<>();
	private static List<MultitenantDTO> map = new ArrayList<MultitenantDTO>();
	private static Map<Object,  Object> datasources = new HashMap<Object, Object>();
	private static Connection mainConnection = null;
	
	public static void setCtx(String dataSourceType) {
		getCtx().push(dataSourceType);
	}
	
	public static void restoreCtx() {
		Stack<String> ctx = getCtx();
		if(!ctx.isEmpty()) {
			ctx.pop();
		}
	}
	
	@Override
	protected Object determineCurrentLookupKey() {
		Stack<String> ctx = getCtx();
		
		if(ctx.isEmpty()) {
			return null;
		}
		//DataSourceType sourceType = ctx.peek();
		String sourceType = ctx.peek();
		
		log.info("choose {}", sourceType);
		return sourceType;	
	
	}
	
	//private static Stack<DataSourceType> getCtx(){
	private static Stack<String> getCtx(){
		if(ctx.get() == null) {
			ctx.set(new Stack<>());
		}
		return ctx.get();
	}
	
	public static void setTenantList(List<MultitenantDTO> map1) {
		map = map1;
	}
	
	public String  setDataSourceListNew(List<MultitenantDTO> map1) {
		MultitenantDTO map = map1.get(0);
		return map.getDbName();
	}
	
	public static List<MultitenantDTO> getTenantList() {
		return map;
	}
	
	public static void addTenant(MultitenantDTO tenant) {
		map.add(tenant);
	}
	
	public static void setTargetDataSourcesInMemory(Map<Object, Object> dataSources1) {
		datasources = dataSources1;
	}
	
	public static Map<Object, Object> getTargetDataSourcesInMemory() {
		return datasources;
	}
	
	public static Connection getMainConnection() {
		return mainConnection;
	}
	
	public static void setMainConnection() {
		try {
			mainConnection = mainDataSource().getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static DataSource mainDataSource() {
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + "client_master2" + "?useSSL=false&autoReconnect=true&sessionVariables=sql_mode=''");
		hikariDataSource.setUsername("root");
		hikariDataSource.setPassword("nirvana");
		hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		hikariDataSource.setAutoCommit(false);
		return new HikariDataSource(hikariDataSource);
	}
}
