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

import com.mbcsoft.ticketmaven.ejb.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Zone;

@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})

public class ZoneBeanImpl extends BaseEntityFacadeImpl<Zone> implements ZoneBean {

	public ZoneBeanImpl() {
	}

	
	public Zone newRecord() {
		return (new Zone());
	}
	
	public List<Zone> getAll() {
		return getAll("Zone");
	}
}
