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

import com.mbcsoft.ticketmaven.entity.Instance;

public interface InstanceBean {

	public List<Instance> getAllInstances();

	public Instance newRecord();

	public Instance save(Instance c);

	public void delete(Instance c);
	
	public Instance getInstance(String instname);
	
	public Instance getById(String id);

}
