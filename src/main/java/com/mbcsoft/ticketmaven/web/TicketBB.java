/*******************************************************************************
 * Copyright (C) 2019 Mike Berger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.mbcsoft.ticketmaven.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
import com.mbcsoft.ticketmaven.ejbImpl.SeatBean;
import com.mbcsoft.ticketmaven.ejbImpl.ShowBean;
import com.mbcsoft.ticketmaven.ejbImpl.TicketBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;

@Named("ticketBB")
@SessionScoped
public class TicketBB implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private TicketBean rbean;
	@EJB
	CustomerBean custbean;
	@EJB
	private ShowBean showbean;
	@EJB
	SeatBean seatbean;

	private Ticket ticket;

	private Show selectedShow;
	private String selectedCustomer;
	private String selectedSeat;
	private List<Seat> seatList;

	// filters
	private boolean all = false;

	private List<Ticket> ticketList = new ArrayList<Ticket>();

	public void refreshList() {
		try {

			if (all) {
				ticketList = rbean.getAll();

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

	public Show getSelectedShow() {
		return selectedShow;
	}

	public void setSelectedShow(Show selectedShow) {
		this.selectedShow = selectedShow;
	}

	public String getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(String selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	public void delete(ActionEvent evt) {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("ticket_id");
			if (id == null || "".equals(id))
				return;

			Ticket r = rbean.get(Ticket.class, id);
			custbean.subtractTicketQuality(r);
			rbean.delete(r);

			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getSelectedSeat() {
		return selectedSeat;
	}

	public void setSelectedSeat(String selectedSeat) {
		this.selectedSeat = selectedSeat;
	}

	public List<Seat> getSeatList() {
		return seatList;
	}

	public void setSeatList(List<Seat> seatList) {
		this.seatList = seatList;
	}

	public void save() {

		ticket.setShow(selectedShow);

		Customer c = custbean.get(Customer.class, selectedCustomer);
		ticket.setCustomer(c);
		Seat s = seatbean.get(Seat.class, selectedSeat);
		ticket.setSeat(s);

		rbean.save(ticket);

		refreshList();

	}

	public void newRecord(ActionEvent evt) {

		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("show_id");
		if (id == null || "".equals(id))
			return;

		selectedShow = showbean.get(Show.class, id);

		seatList = seatbean.getAvailableSeatsForShow(selectedShow);

		ticket = rbean.newRecord();

		try {
			CustomerBB.refreshSessionList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
