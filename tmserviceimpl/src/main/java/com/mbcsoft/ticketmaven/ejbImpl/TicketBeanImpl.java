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
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.TicketBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})

public class TicketBeanImpl extends BaseEntityFacadeImpl<Ticket> implements TicketBean   {

	public TicketBeanImpl() {
	}

	
	public Ticket newRecord() {
		return (new Ticket());
	}
	
	public List<Ticket> getAll() {
		return getAll("Ticket");
	}

	@SuppressWarnings("unchecked")
	public List<Ticket> getTicketsForCustomer(Customer c) {
		if( c == null )
		{
			// get current customer
			c = getCurrentCustomer();
			if( c == null )
				return null;
		}
		Query query = em.createQuery("SELECT e FROM Ticket e WHERE e.customer = :cust");
		query.setParameter("cust", c);
		return (List<Ticket>) query.getResultList();
	}


	@SuppressWarnings("unchecked")
	public List<Ticket> getTicketsForShow(Show s) {
		Query query = em.createQuery("SELECT e FROM Ticket e WHERE e.show = :show");
		query.setParameter("show", s);
		return (List<Ticket>) query.getResultList();
	}
	
	public void deleteTicketsForShow(Show s) {
		Query query = em.createQuery("delete FROM Ticket e WHERE e.show = :show");
		query.setParameter("show", s);
		query.executeUpdate();
	}
}
