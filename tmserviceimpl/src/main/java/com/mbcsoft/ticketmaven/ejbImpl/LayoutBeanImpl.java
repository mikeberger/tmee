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

import com.mbcsoft.ticketmaven.ejb.LayoutBean;
import com.mbcsoft.ticketmaven.entity.Layout;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})

public class LayoutBeanImpl extends BaseEntityFacadeImpl<Layout> implements LayoutBean  {

	public LayoutBeanImpl() {
	}

	public Layout newRecord() {
		return (new Layout());
	}
	

	public List<Layout> getAll() {
		return getAll("Layout");
	}
}
