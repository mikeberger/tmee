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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.LayoutBean;
import com.mbcsoft.ticketmaven.ejb.SeatBean;
import com.mbcsoft.ticketmaven.entity.Layout;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})

public class LayoutBeanImpl extends BaseEntityFacadeImpl<Layout> implements LayoutBean  {
	
	@EJB SeatBean sbean;

	public LayoutBeanImpl() {
	}

	public Layout newRecord() {
		return (new Layout());
	}
	
	public Layout get(String name) {
		Query query = em.createQuery("SELECT e FROM Layout e WHERE e.name = :name");
		query.setParameter("name", name);
		try {
			Layout l = (Layout) (query.getSingleResult());
			return l;
		}
		catch(NoResultException e)
		{
			return null;
		}
	}
	

	public List<Layout> getAll() {
		return getAll("Layout");
	}

	@Override
	public Layout save(Layout c) {
		
		// if layout is being created, add seats
		Layout ex = get(c.getName());
		if( ex == null) {
			Layout l = super.save(c);
			sbean.generateMissingSeats(l);
			return l;
		}
		return super.save(c);
	}
	
	
	
}
