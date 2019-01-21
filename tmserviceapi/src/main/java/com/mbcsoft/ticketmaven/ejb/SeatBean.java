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

import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Zone;

@Local
public interface SeatBean extends BaseEntityFacade<Seat>{

	public Seat getSeat(String row, int seat, Layout l);
	public List<Seat> getSeats(Layout l);
	public List<Seat> getAvailableSeatsForShow(Show s);
	public List<Seat> getAvailableSeatsForShow(Show s, Zone z);
	public void generateMissingSeats(Layout layout);

}
