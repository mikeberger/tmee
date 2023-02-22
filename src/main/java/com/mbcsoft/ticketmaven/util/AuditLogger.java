/*******************************************************************************
 * Copyright (C) 2019 Mike Berger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.mbcsoft.ticketmaven.util;

/*-
 * #%L
 * tmee
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.lang.reflect.Method;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;


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
		
		logger.fine("Is tmadmin: " + ejbContext.isCallerInRole("tmadmin"));
		logger.fine("Is tmuser: " + ejbContext.isCallerInRole("tmuser"));
		logger.fine("Is tmsite: " + ejbContext.isCallerInRole("tmsite"));

	
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
