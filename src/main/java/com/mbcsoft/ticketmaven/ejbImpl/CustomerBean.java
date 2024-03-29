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

import java.util.List;
import java.util.Set;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.Query;

import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Ticket;
import com.mbcsoft.ticketmaven.util.AuditLogger;

/**
 * Session Bean implementation class CustomerBeanImpl
 */
@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})
@Interceptors({AuditLogger.class})

//@PermitAll
public class CustomerBean extends BaseEntityFacadeImpl<Customer>  {



	public CustomerBean() {
	}

	public Customer newRecord() {
		return (new Customer());
	}

	@SuppressWarnings("unchecked")
	@RolesAllowed({"tmadmin"})
	public List<Customer> getAll() {

		//return getAll("Customer");
		Query query = em.createQuery("SELECT e FROM Customer e WHERE e.instance = :inst AND e.roles = 'tmuser' order by lastName, firstName");
		query.setParameter("inst", getInstance());
		return (List<Customer>) query.getResultList();

	}
	
	@SuppressWarnings("unchecked")
	@RolesAllowed({ "tmsite"})
	public List<Customer> getAllAdmins() {

		//return getAll("Customer");
		Query query = em.createQuery("SELECT e FROM Customer e WHERE e.roles = 'tmadmin' order by lastName, firstName");
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

	@RolesAllowed({ "tmsite"})
	public void saveAdmin(Customer cust) {
		cust.setRoles("tmadmin");
		em.merge(cust);
		
	}

	@RolesAllowed({"tmadmin"})
	public void eraseQualityTotals() {
		
		Query query = em.createQuery("UPDATE Customer SET totalTickets = 0, totalQuality = 0");
		query.executeUpdate();
		
	}
	
	@RolesAllowed({"tmadmin"})
	public void recalculateQualityTotals() {
		
		for( Customer c : getAll()) {
			Set<Ticket> tickets = c.getTicketsCollection();
			if (tickets.size() == c.getTotalTickets())
				continue;

			c.setTotalTickets(tickets.size());
			int q = 0;
			for (Ticket t : tickets) {
				Seat s = t.getSeat();
				q += s.getWeight();
			}
			c.setTotalQuality(q);
			save(c);
		}
		
	}

	public void subtractTicketQuality(Ticket ticket) throws Exception {
		Seat s = ticket.getSeat();
		Customer cust = ticket.getCustomer();
		cust.setTotalTickets(Integer.valueOf(cust.getTotalTickets() - 1));
		cust.setTotalQuality(Integer.valueOf(cust.getTotalQuality() - s.getWeight()));
		if( cust.getTotalTickets() < 0) cust.setTotalTickets(0);
		if( cust.getTotalQuality() < 0) cust.setTotalQuality(0);
		save(cust);
	}



}
