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

import com.mbcsoft.ticketmaven.entity.Customer;


public interface CustomerBean extends BaseEntityFacade<Customer> {

	
	public Customer getCurrentCustomer();
	public Customer getCustomer(String id);
}
