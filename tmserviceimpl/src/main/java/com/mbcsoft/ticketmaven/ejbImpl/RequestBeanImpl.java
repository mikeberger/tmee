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
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.RequestBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.util.AuditLogger;

@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})
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
	@RolesAllowed({"tmadmin", "tmsite"})
	public List<Request> getRequestsForShow(Show s) {

		Query query = em.createQuery("SELECT e FROM Request e WHERE e.show = :show");
		query.setParameter("show", s);
		return (List<Request>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@RolesAllowed({"tmadmin", "tmsite"})
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
