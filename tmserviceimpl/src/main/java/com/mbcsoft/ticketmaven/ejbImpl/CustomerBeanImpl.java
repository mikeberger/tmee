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
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.util.AuditLogger;

/**
 * Session Bean implementation class CustomerBeanImpl
 */
@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})
@Interceptors({AuditLogger.class})

//@PermitAll
public class CustomerBeanImpl extends BaseEntityFacadeImpl<Customer> implements CustomerBean {



	public CustomerBeanImpl() {
	}

	public Customer newRecord() {
		return (new Customer());
	}

	@SuppressWarnings("unchecked")
	@RolesAllowed({"tmadmin", "tmsite"})
	public List<Customer> getAll() {

		//return getAll("Customer");
		Query query = em.createQuery("SELECT e FROM Customer e WHERE e.instance = :inst AND e.roles = 'tmuser' order by lastname, firstname");
		query.setParameter("inst", getInstance());
		return (List<Customer>) query.getResultList();

	}
	
	@SuppressWarnings("unchecked")
	@RolesAllowed({ "tmsite"})
	public List<Customer> getAllAdmins() {

		//return getAll("Customer");
		Query query = em.createQuery("SELECT e FROM Customer e WHERE e.roles = 'tmadmin' order by lastname, firstname");
		return (List<Customer>) query.getResultList();

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

	@Override
	public void saveAdmin(Customer cust) {
		cust.setRoles("tmadmin");
		em.merge(cust);
		
	}




}
