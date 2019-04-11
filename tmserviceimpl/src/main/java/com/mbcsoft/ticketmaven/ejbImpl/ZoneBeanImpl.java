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
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Zone;

@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})

public class ZoneBeanImpl extends BaseEntityFacadeImpl<Zone> implements ZoneBean {

	public ZoneBeanImpl() {
	}

	public Zone get(String name) {
		Query query = em.createQuery("SELECT e FROM Zone e WHERE e.name = :name and e.instance = :inst");
		query.setParameter("name", name);
		query.setParameter("inst", getInstance());

		try {
			Zone l = (Zone) (query.getSingleResult());
			return l;
		}
		catch(NoResultException e)
		{
			return null;
		}
	}
	
	public Zone newRecord() {
		return (new Zone());
	}
	
	public List<Zone> getAll() {
		return getAll("Zone");
	}
}
