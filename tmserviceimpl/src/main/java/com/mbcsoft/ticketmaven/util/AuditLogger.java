package com.mbcsoft.ticketmaven.util;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;


public class AuditLogger {
	

	static private final Logger logger = Logger.getLogger("audit_logger");


	@Resource
	EJBContext ejbContext;

	@AroundInvoke
	public Object audit(InvocationContext ic) throws Exception {

		Method method = ic.getMethod();
		StringBuilder log = new StringBuilder();
		
		log.append("API:" + method.getDeclaringClass().getSimpleName() + "."
				+ method.getName() + " USER:"
				+ ejbContext.getCallerPrincipal().getName() + " [");
		
		
		for(Object o : ic.getParameters())
		{
			if( o != null )
				log.append("{" + o.toString() + "}");
		}
		
		log.append("]");
		
		logger.info("Is tmadmin: " + ejbContext.isCallerInRole("tmadmin"));
		logger.info("Is tmuser: " + ejbContext.isCallerInRole("tmuser"));
		logger.info("Is tmsite: " + ejbContext.isCallerInRole("tmsite"));

	
		logger.info("ENTER :"+ log.toString());
		long a = System.currentTimeMillis();
		String status = "Success";
		try {
			return  ic.proceed();
		} catch (Exception e) {
			status = "Failure";
			throw e;
			
		} finally {
			long b = System.currentTimeMillis();
			logger.info("EXIT : "+ log.toString() +" : STATUS : "+ status +
					" : Execution Time Taken :" +(b-a)/1000d +" Seconds");
		}
	}


}
