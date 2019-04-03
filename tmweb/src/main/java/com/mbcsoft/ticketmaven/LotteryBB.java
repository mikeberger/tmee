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
import java.util.Collection;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejb.LotteryManager;
import com.mbcsoft.ticketmaven.ejb.RequestBean;
import com.mbcsoft.ticketmaven.ejb.SeatBean;
import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.ejb.TicketBean;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;

@Named("lotteryBB")
@SessionScoped
public class LotteryBB implements Serializable {


	private static final long serialVersionUID = 1L;
	private String requests;
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void undo() throws AbortProcessingException {

		try {

			Show s = showbean.get(Show.class, showid);

			lottery.undoLottery(s);

			loadInternal();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() throws AbortProcessingException {
			showid = FacesContext.getCurrentInstance().getExternalContext()
			.getRequestParameterMap().get("showid");
			loadInternal();
	}

	private void loadInternal() throws AbortProcessingException {

		try {


			Show s = showbean.get(Show.class, showid);
			
			showName = s.getLabel();

			Collection<Request> reqs = reqbean.getPaidRequestsForShow(s);

			requests = Integer.toString(reqs.size());

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

}
