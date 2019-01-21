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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejb.TicketBean;
import com.mbcsoft.ticketmaven.entity.Ticket;

@Named("ticketBB")
@SessionScoped
public class TicketBB implements Serializable{


	private static final long serialVersionUID = 1L;

	@EJB private TicketBean rbean;

	private List<Ticket> ticketList = new ArrayList<Ticket>();

	public void refreshList(ActionEvent evt)
	{
		try {
			// get requests for current logged in customer
			ticketList = rbean.getTicketsForCustomer(null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTicketList(List<Ticket> ticketList) {
		this.ticketList = ticketList;
	}

	public List<Ticket> getTicketList() {
		return ticketList;
	}

}
