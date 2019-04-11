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


import javax.ejb.Local;

import com.mbcsoft.ticketmaven.entity.Zone;

@Local
public interface ZoneBean extends BaseEntityFacade<Zone>{
	
	public Zone get(String name);


}
