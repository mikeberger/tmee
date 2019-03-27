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
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejb.TicketBean;
import com.mbcsoft.ticketmaven.entity.Ticket;

@Named("ticketBB")
@SessionScoped
public class TicketBB implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private TicketBean rbean;
	
	private String selectedShow;
	private String selectedCustomer;

	// filters
	private boolean all = false;

	private List<Ticket> ticketList = new ArrayList<Ticket>();

	public void refreshList() {
		try {
			
			if (all) {
				ticketList = rbean.getAll();
				
				if( selectedCustomer != null || selectedShow != null)
				{
					List<Ticket> list2 = new ArrayList<Ticket>();
					for( Ticket r : ticketList) {
						if( selectedCustomer != null && !Integer.toString(r.getCustomer().getRecordId()).equals(selectedCustomer) )
							continue;
						if( selectedShow != null && !Integer.toString(r.getShow().getRecordId()).equals(selectedShow) )
							continue;
						list2.add(r);
					}
					ticketList = list2;
				}
				
			} else {
				ticketList = rbean.getTicketsForCustomer(null);
			}

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

	public void loadUserList() {
		all = false;
		refreshList();
	}

	public void loadAdminList() {
		all = true;
		refreshList();
	}
	
	public String getSelectedShow() {
		return selectedShow;
	}

	public void setSelectedShow(String selectedShow) {
		this.selectedShow = selectedShow;
	}

	public String getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(String selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}


}
