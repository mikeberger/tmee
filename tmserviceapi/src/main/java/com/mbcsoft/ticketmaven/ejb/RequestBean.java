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
package com.mbcsoft.ticketmaven.ejb;


import java.util.List;

import javax.ejb.Local;

import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Show;

@Local
public interface RequestBean extends BaseEntityFacade<Request>{

	public List<Request> getRequestsForShow(Show s);
	
	public List<Request> getPaidRequestsForShow(Show s);
	
	public List<Request> getRequestsForCustomer(Customer c);
	
	public int requestPrice(Request r);

}
