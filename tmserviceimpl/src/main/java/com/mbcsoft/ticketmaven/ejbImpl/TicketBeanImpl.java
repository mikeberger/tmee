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
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})

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
