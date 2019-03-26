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

import com.mbcsoft.ticketmaven.ejb.RequestBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.util.AuditLogger;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})
@Interceptors({AuditLogger.class})
public class RequestBeanImpl extends BaseEntityFacadeImpl<Request> implements RequestBean  {

	
	public RequestBeanImpl() {
	}

	
	public Request newRecord() {
		return (new Request());
	}
	
	public List<Request> getAll() {
		return getAll("Request");
	}
	
	@SuppressWarnings("unchecked")
	public List<Request> getRequestsForCustomer(Customer c) {
		if( c == null )
		{
			// get current customer
			c = getCurrentCustomer();
			if( c == null )
				return null;
		}
		Query query = em.createQuery("SELECT e FROM Request e WHERE e.customer = :cust");
		query.setParameter("cust", c);
		return (List<Request>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@RolesAllowed({"tmadmin"})
	public List<Request> getRequestsForShow(Show s) {

		Query query = em.createQuery("SELECT e FROM Request e WHERE e.show = :show");
		query.setParameter("show", s);
		return (List<Request>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@RolesAllowed({"tmadmin"})
	public List<Request> getPaidRequestsForShow(Show s) {

		Query query = em.createQuery("SELECT e FROM Request e WHERE e.show = :show AND e.paid = true");
		query.setParameter("show", s);
		return (List<Request>) query.getResultList();
	}
	
	public int requestPrice(Request r)
	{

		int price = 0;
		
		try {
			Show sh = r.getShow();
			double discount = 0.0;
			discount = r.getDiscount();
			double p = sh.getPrice() * r.getTickets() * (100.0 - discount) / 100.0;
			price = (int)p;
			return price;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}			
	}
}
