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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.ShowDTO;
import com.mbcsoft.ticketmaven.util.AuditLogger;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})
@Interceptors({AuditLogger.class})

public class ShowBeanImpl extends BaseEntityFacadeImpl<Show> implements ShowBean {

	public ShowBeanImpl() {
	}


	public Show newRecord() {
		return (new Show());
	}

	public List<Show> getAll() {
		return getAll("Show");
	}


	@SuppressWarnings("unchecked")
	public List<ShowDTO> getFutureShows() {

		Query query = em.createQuery("SELECT e FROM Show e WHERE e.instance = :inst and e.time > :now");
		query.setParameter("inst", getInstance());
		query.setParameter("now", new Timestamp(new Date().getTime()));
		List<Show> l = (List<Show>) query.getResultList();
		List<ShowDTO> al = new ArrayList<ShowDTO>();
		for( Show s : l)
		{
			ShowDTO sd = new ShowDTO();
			sd.setName(s.getName());
			sd.setPrice(s.getPrice());
			sd.setRecordId(s.getRecordId());
			sd.setTime(s.getTime());
			sd.setLayout(s.getLayout().getName());

			al.add(sd);
		}
		return al;

	}

}
