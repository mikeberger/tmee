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
@RolesAllowed({"tmuser", "tmadmin"})

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
