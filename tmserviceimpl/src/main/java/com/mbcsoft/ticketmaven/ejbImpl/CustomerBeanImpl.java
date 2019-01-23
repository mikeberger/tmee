/*******************************************************************************
 * MBCSoft License
 * 
 * Copyright (C) 2019, Michael Berger
 * 
 * All Rights Reserved
 * 
 * PROPRIETARY
 * 
 * This Software shall not be in the possession of, distributed to, or routed to anyone except with written permission of Michael Berger.
 ******************************************************************************/
package com.mbcsoft.ticketmaven.ejbImpl;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.util.AuditLogger;

/**
 * Session Bean implementation class CustomerBeanImpl
 */
@Stateless
@RolesAllowed({"tmuser", "tmadmin"})
@Interceptors({AuditLogger.class})

//@PermitAll
public class CustomerBeanImpl extends BaseEntityFacadeImpl<Customer> implements CustomerBean {



	public CustomerBeanImpl() {
	}

	public Customer newRecord() {
		return (new Customer());
	}

	public List<Customer> getAll() {

		return getAll("Customer");

	}

	public Customer getCustomer(String id) {
		
		try {

			Customer c = (Customer) em.createNamedQuery("findCustomerByUserid")
					.setParameter("id", id).getSingleResult();

			return c;

		} catch (Throwable t) {
			return null;
		}
	}




}
