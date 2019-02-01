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

import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.ShowDTO;

@Local
public interface ShowBean extends BaseEntityFacade<Show>{

	public List<ShowDTO> getFutureShows();
	public Show getFullShow(String id); // eager loading
}
