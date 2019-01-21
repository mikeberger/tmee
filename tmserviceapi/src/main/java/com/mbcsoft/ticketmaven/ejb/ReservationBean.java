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
import com.mbcsoft.ticketmaven.entity.Reservation;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.TMTable;

@Local
public interface ReservationBean extends BaseEntityFacade<Reservation>{
	public List<Reservation> getReservations(Show show, TMTable table);
	public List<Reservation> getReservations(Show show);
	public List<Reservation> getReservations(Customer c);
	    
}
