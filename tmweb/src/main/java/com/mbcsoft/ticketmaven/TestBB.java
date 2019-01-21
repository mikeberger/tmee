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
package com.mbcsoft.ticketmaven;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejb.InitBean;

@Named("testBB")
@RequestScoped
public class TestBB {

	@EJB
	private InitBean rbean;

	public void init(ActionEvent evt) {
		rbean.generateTestData();

	}



}
