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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.util.AuditLogger;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})
@Interceptors({AuditLogger.class})

public class ShowBeanImpl extends BaseEntityFacadeImpl<Show> implements ShowBean {

	static private final Logger logger = Logger.getLogger(ShowBeanImpl.class.getName());

	public ShowBeanImpl() {
	}


	public Show newRecord() {
		return (new Show());
	}

	public List<Show> getAll() {
		return getAll("Show");
	}


	@SuppressWarnings("unchecked")
	public List<Show> getFutureShows() {

		Query query = em.createQuery("SELECT e FROM Show e WHERE e.instance = :inst and e.time > :now order by e.name");
		query.setParameter("inst", getInstance());
		query.setParameter("now", new Timestamp(new Date().getTime()));
		List<Show> l = (List<Show>) query.getResultList();
		return l;

	}


	@Override
	public Show getFullShow(String id) {
		Show s = get(Show.class, id);
		logger.info("full show sizes: " + s.getRequestsCollection().size() + " " + s.getTicketsCollection().size());
		em.detach(s);
		return s;
	}

}
