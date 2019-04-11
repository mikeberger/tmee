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

import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.ReservationBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Reservation;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.TMPackage;
import com.mbcsoft.ticketmaven.entity.TMTable;

@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})

public class ReservationBeanImpl extends BaseEntityFacadeImpl<Reservation> implements ReservationBean  {

	public ReservationBeanImpl() {
	}

	
	public Reservation newRecord() {
		return (new Reservation());
	}
	

	public List<Reservation> getAll() {
		return getAll("Reservation");
	}
	
	public double calcDiscount(TMPackage p)
	{
		
		try{
			
			Collection<Show> c = p.getShowsCollection();
			
			int total = 0;
			for( Show sh : c )
			{	
					total += sh.getPrice();		
			}
			
			if( p.getPrice() > total )
			{
				return 0;
			}
			double percent = (((double)total - (double)p.getPrice()) * 100.0) / (double)total;
			return percent;
		}
		catch( Exception e)
		{
			return 0;
		}
	}


	@SuppressWarnings("unchecked")
	public List<Reservation> getReservations(Show show,
			TMTable table) {
		Query query = em.createQuery("SELECT e FROM Reservation e WHERE e.show = :show AND e.table = :table");
		query.setParameter("show", show);
		query.setParameter("table", table);
		return (List<Reservation>) query.getResultList();
	}


	@SuppressWarnings("unchecked")
	public List<Reservation> getReservations(Show show) {
		Query query = em.createQuery("SELECT e FROM Reservation e WHERE e.show = :show");
		query.setParameter("show", show);
		return (List<Reservation>) query.getResultList();
	}


	@SuppressWarnings("unchecked")
	public List<Reservation> getReservations(Customer c) {
		Query query = em.createQuery("SELECT e FROM Reservation e WHERE e.customer = :cust");
		query.setParameter("cust", c);
		return (List<Reservation>) query.getResultList();
	}
}
