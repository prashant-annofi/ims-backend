package com.annofi.ims.config.multitenant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DatabaseRouterAspect {
	@Autowired
	HttpServletRequest httpServletRequest;
	
	@Autowired
	MultitenantController multitenantController;
	
    @Around("@annotation(withDatabase)")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint, WithDataBase withDatabase) throws Throwable {
    	try {
			String origin = httpServletRequest.getHeader("origin");
			List<MultitenantDTO> tenants = new ArrayList<MultitenantDTO>();
	       	tenants = RoutingDataSource.getTenantList();	
	       	
			Map<String, String> map = new HashMap<String, String>();
	       	for (MultitenantDTO item : tenants) {
				map.put(item.getSubdomain(), item.getDbName());
			}
	       	if(origin == null) {
	       		RoutingDataSource.setCtx("tenant1");
	       	}
	       	else {
		       	String a = map.get(origin);
		       	if(a != null)
		       	{
		       		RoutingDataSource.setCtx(a);
		       	}
		       	else {
		       		Throwable t = new Throwable("wrong tenant selected.");
		       		return t;
		       	}
			}
	        return proceedingJoinPoint.proceed();
        }
    	catch (Exception e) {
			// TODO: handle exception
    		throw e;
		}
    }
}
