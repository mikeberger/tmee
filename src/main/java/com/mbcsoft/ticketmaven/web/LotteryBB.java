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

/*-
 * #%L
 * tmee
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.Serializable;
import java.util.Collection;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.LotteryManager;
import com.mbcsoft.ticketmaven.ejbImpl.RequestBean;
import com.mbcsoft.ticketmaven.ejbImpl.SeatBean;
import com.mbcsoft.ticketmaven.ejbImpl.ShowBean;
import com.mbcsoft.ticketmaven.ejbImpl.TicketBean;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;

@Named("lotteryBB")
@SessionScoped
public class LotteryBB implements Serializable {


	private static final long serialVersionUID = 1L;
	private String requests;
	private String unpaid;
	private String seatsRequested;
	private String tickets;
	private String seatsAvailable;
	private String showid;
	private String showName;

	@EJB private LotteryManager lottery;
	@EJB private ShowBean showbean;
	@EJB private TicketBean ticketbean;
	@EJB private RequestBean reqbean;
	@EJB private SeatBean seatbean;

	public void run() throws AbortProcessingException {

		try {

			Show s = showbean.get(Show.class, showid);

			lottery.runLottery(s);

			loadInternal();
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "Lottery Complete"));


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void undo() throws AbortProcessingException {

		try {

			Show s = showbean.get(Show.class, showid);

			lottery.undoLottery(s);

			loadInternal();
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "Lottery Undo Complete"));


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() throws AbortProcessingException {
			showid = FacesContext.getCurrentInstance().getExternalContext()
			.getRequestParameterMap().get("show_id");
			loadInternal();
	}

	private void loadInternal() throws AbortProcessingException {

		try {


			Show s = showbean.get(Show.class, showid);
			
			showName = s.getLabel();

			Collection<Request> reqs = reqbean.getPaidRequestsForShow(s);

			requests = Integer.toString(reqs.size());
			
			unpaid = Long.toString(reqbean.getUnpaidCount(s));

			int num_seats = 0;
			for(Request tr : reqs )
			{
				num_seats += tr.getTickets();
			}
			seatsRequested = Integer.toString(num_seats);

			Collection<Seat> seats = seatbean.getAvailableSeatsForShow(s);

			seatsAvailable = Integer.toString(seats.size());
			Collection<Ticket> tkts = ticketbean.getTicketsForShow(s);
			tickets = Integer.toString(tkts.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the requests
	 */
	public String getRequests() {
		return requests;
	}

	/**
	 * @param requests the requests to set
	 */
	public void setRequests(String requests) {
		this.requests = requests;
	}

	/**
	 * @return the seatsRequested
	 */
	public String getSeatsRequested() {
		return seatsRequested;
	}

	/**
	 * @param seatsRequested the seatsRequested to set
	 */
	public void setSeatsRequested(String seatsRequested) {
		this.seatsRequested = seatsRequested;
	}

	/**
	 * @return the tickets
	 */
	public String getTickets() {
		return tickets;
	}

	/**
	 * @param tickets the tickets to set
	 */
	public void setTickets(String tickets) {
		this.tickets = tickets;
	}

	/**
	 * @return the seatsAvailable
	 */
	public String getSeatsAvailable() {
		return seatsAvailable;
	}

	/**
	 * @param seatsAvailable the seatsAvailable to set
	 */
	public void setSeatsAvailable(String seatsAvailable) {
		this.seatsAvailable = seatsAvailable;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getUnpaid() {
		return unpaid;
	}

	public void setUnpaid(String unpaid) {
		this.unpaid = unpaid;
	}

}
