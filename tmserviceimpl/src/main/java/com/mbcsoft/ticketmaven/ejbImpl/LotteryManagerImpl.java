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
package com.mbcsoft.ticketmaven.ejbImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.ejb.LotteryManager;
import com.mbcsoft.ticketmaven.ejb.RequestBean;
import com.mbcsoft.ticketmaven.ejb.SeatBean;
import com.mbcsoft.ticketmaven.ejb.TicketBean;
import com.mbcsoft.ticketmaven.ejb.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;
import com.mbcsoft.ticketmaven.entity.Zone;

@Stateless
@RolesAllowed("tmadmin")
public class LotteryManagerImpl implements LotteryManager {

	final private static Logger logger = Logger
			.getLogger("com.mbcsoft.ticketmaven.ejb");


	@EJB
	private SeatBean seatbean;
	@EJB
	private RequestBean requestbean;
	@EJB
	private ZoneBean zonebean;
	@EJB
	private TicketBean ticketbean;
	@EJB
	private CustomerBean custbean;

	@PersistenceContext
	private EntityManager em;

	public void runLottery(Show s)  {

		logger.info("Running Lottery for show " + s.getRecordId());

		// fetch a copy of show that is managed
		Show show = em.find(Show.class, s.getRecordId());

		// get the list of available seats for the show
		Collection<Seat> availableSeats = seatbean
				.getAvailableSeatsForShow(show);

		dumpSeats("initial AVAIL", availableSeats);

		// get the list of requests that win the lottery
		Collection<Request> requests = getRequests(show, availableSeats.size());

		// sort Requests in the order that they should be honored
		TreeSet<Request> sortedRequests = new TreeSet<Request>(
				new requestCompare());
		sortedRequests.addAll(requests);

		dumpRequests(sortedRequests);

		// assign front row seats
		assignSeats(show, getFrontRowSeats(availableSeats), sortedRequests,
				Customer.FRONT_ONLY);

		// assign all special seats from zone table
		Collection<Zone> zones = zonebean.getAll();
		for (Zone z : zones) {
			Collection<Seat> zoneSeats = seatbean.getAvailableSeatsForShow(
					show, z);
			assignSeats(show, zoneSeats, sortedRequests, z.getName());
		}

		availableSeats = seatbean.getAvailableSeatsForShow(show);
		assignAisleSeats(show, availableSeats, sortedRequests);

		// assign front seats
		availableSeats = seatbean.getAvailableSeatsForShow(show);
		TreeSet<Seat> frontSortedSeats = new TreeSet<Seat>(
				new frontSeatCompare());
		dumpSeats("before front sorted", availableSeats);
		frontSortedSeats.addAll(availableSeats);
		dumpSeats("front sorted", frontSortedSeats);
		assignSeats(show, frontSortedSeats, sortedRequests, Customer.FRONT);

		// assign rear seats
		TreeSet<Seat> rearSortedSeats = new TreeSet<Seat>(new rearSeatCompare());
		rearSortedSeats.addAll(frontSortedSeats);
		dumpSeats("rear sorted", rearSortedSeats);
		assignSeats(show, rearSortedSeats, sortedRequests, Customer.REAR);

		// assign the rest
		TreeSet<Seat> regularSortedSeats = new TreeSet<Seat>(
				new regularSeatCompare());
		regularSortedSeats.addAll(rearSortedSeats);
		dumpSeats("regular sorted", regularSortedSeats);
		assignSeats(show, regularSortedSeats, sortedRequests, Customer.NONE);

		logger.info("Lottery Done");

	}

	public static class NotEnoughSeats extends Exception {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
	}

	// assign tickets for a number of contiguous seats starting at a given seat
	private void assignTickets(Show show, Request req, Collection<Seat> seats) {

		// calculate price
		int show_price = show.getPrice();
		if (req.getDiscount() > 0) {
			show_price = (int) ((double) show_price * (1 - (double) req
					.getDiscount() / 100.0));
		}

		// create ticket records
		int total_quality = 0;
		for (Seat s : seats) {

			Ticket tkt = new Ticket();
			tkt.setCustomer(req.getCustomer());
			tkt.setSeat(s);
			tkt.setShow(show);
			logger.info("Assigning Ticket: " + req.getCustomerName() + ":"
					+ s.getRow() + "/" + s.getSeat());

			// assign price from show with discount from request

			tkt.setTicketPrice(show_price);
			ticketbean.save(tkt);

			total_quality += s.getWeight();
		}

		// update the quality counts for the customero
		Customer cust = req.getCustomer();
		cust.setTotalTickets(req.getTickets() + cust.getTotalTickets());
		cust.setTotalQuality(total_quality + cust.getTotalQuality());
		custbean.save(cust);

		// delete request from DB
		requestbean.delete(req);

	}

	private Collection<Seat> getContiguousSeats(Show show,
			Collection<Seat> availableSeats, Seat startingSeat, int number,
			boolean aisle) throws NotEnoughSeats {

		Collection<Seat> seats = new ArrayList<Seat>();
		seats.add(startingSeat);

		// always assign seats away from the center
		boolean assignToTheRight = true;
		int st = startingSeat.getSeat();
		if (st >= show.getLayout().getCenterseat())
			assignToTheRight = false;

		// if assigning aisle seats - assign away from the aisle
		if (aisle == true) {
			if (startingSeat.getEnd().equals(Seat.LEFT)) {
				assignToTheRight = true;
			} else if (startingSeat.getEnd().equals(Seat.RIGHT)) {
				assignToTheRight = false;
			}
		}

		Seat curseat = startingSeat;
		for (int i = 0; i < number - 1; i++) {
			// check if there is no next seat
			if ((assignToTheRight && curseat.getEnd().equals(Seat.RIGHT))
					|| (!assignToTheRight && curseat.getEnd().equals(Seat.LEFT))) {
				throw new NotEnoughSeats();
			}

			// check if next seat is available
			int nextseat;
			if (assignToTheRight) {
				nextseat = curseat.getSeat() + 1;
			} else {
				nextseat = curseat.getSeat() - 1;
			}

			curseat = null;
			for (Seat ns : availableSeats) {
				if (ns.getSeat() == nextseat
						&& ns.getRow().equals(startingSeat.getRow())) {
					curseat = ns;
					seats.add(curseat);
					break;

				}
			}

			if (curseat == null)
				throw new NotEnoughSeats();
		}

		availableSeats.removeAll(seats);
		return seats;
	}

	private void assignAisleSeats(Show show, Collection<Seat> availableSeats,
			Collection<Request> requests) {

		Collection<Seat> aisleSeats = getAisleSeats(availableSeats);

		dumpSeats("Aisle Seats", aisleSeats);

		Iterator<Request> it = requests.iterator();
		while (it.hasNext()) {

			Request tr = it.next();

			// if not an AISLE request, then stop, AISLE requests should sort
			// first
			if (tr.getSpecialNeeds().equals(Customer.AISLE)) {
				// assign N contiguous seats

				// loop through the aisle seats
				Iterator<Seat> seatit = aisleSeats.iterator();
				while (seatit.hasNext()) {
					try {

						// see if this aisle seat has enough contiguous seats to
						// satisfy the requested number of seats
						Collection<Seat> seats = getContiguousSeats(show,
								availableSeats, seatit.next(), tr.getTickets(),
								true);

						// assign the seats
						assignTickets(show, tr, seats);
						availableSeats.removeAll(seats);

						aisleSeats.removeAll(seats);
						break;

					} catch (NotEnoughSeats nes) {
						// not enough seats, try the next aisle seat
						continue;
					}
				}

				it.remove();

			} else
				continue;
		}
	}

	private void assignSeats(Show show, Collection<Seat> sortedSeats,
			Collection<Request> requests, String special_needs) {

		logger.info("Assigning seats: " + special_needs);

		// for each request
		Iterator<Request> it = requests.iterator();
		while (it.hasNext()) {

			Request tr = it.next();
			if (!tr.getSpecialNeeds().equals(special_needs))
				continue;

			// loop through the aisle seats
			Iterator<Seat> seatit = sortedSeats.iterator();
			while (seatit.hasNext()) {
				try {

					// see if this seat has enough contiguous seats to
					// satisfy the requested number of seats
					Collection<Seat> seats = getContiguousSeats(show, sortedSeats,
							seatit.next(), tr.getTickets(), false);

					// assign the seats
					assignTickets(show, tr, seats);
					sortedSeats.removeAll(seats);
					break;

				} catch (NotEnoughSeats nes) {
					// not enough seats, try the next aisle seat
					continue;
				}
			}

			it.remove();
		}
	}

	private Collection<Seat> getFrontRowSeats(Collection<Seat> availableSeats) {
		Collection<Seat> seats = new TreeSet<Seat>(new regularSeatCompare());
		for (Seat s : availableSeats) {
			if (s.getRow().equals("A") || s.getEnd().equals(Seat.FRONT)) {
				seats.add(s);
			}
		}
		return seats;
	}

	private Collection<Seat> getAisleSeats(Collection<Seat> availableSeats) {
		Collection<Seat> seats = new TreeSet<Seat>(new regularSeatCompare());
		for (Seat s : availableSeats) {
			if (s.getEnd().equals(Seat.LEFT) || s.getEnd().equals(Seat.RIGHT)) {
				seats.add(s);
			}
		}
		return seats;
	}

	private Collection<Request> getRequests(Show show,int n) {
		//Collection<Request> req = show.getRequestsCollection();
		Collection<Request> req = requestbean.getPaidRequestsForShow(show);
		logger.info("total requests for show:" + req.size());
		dumpRequests(req);

		Collection<Request> winners = new ArrayList<Request>();
		Random ran = new Random();

		ArrayList<Request> pool = new ArrayList<Request>(req);
		while (pool.size() > 0) {
			int idx = ran.nextInt(pool.size());
			Request tr = pool.remove(idx);

			winners.add(tr);
			tr.setLotteryPosition(winners.size());
		}

		logger.info("winners:" + winners.size());
		dumpRequests(winners);
		return winners;
	}

	private static class requestCompare implements Comparator<Request> {

		public int compare(Request r1, Request r2) {

			// compare avg quality. multiply by 100000 so we can use integer
			// math and not fractions

			// only compare quality for prior customers. New customers
			// will be random

			if (r1.getCustomer().getTotalTickets() != 0
					&& r2.getCustomer().getTotalTickets() != 0) {
				int quality1 = (100000 * r1.getCustomer().getTotalQuality())
						/ r1.getCustomer().getTotalTickets();

				int quality2 = (100000 * r2.getCustomer().getTotalQuality())
						/ r2.getCustomer().getTotalTickets();

				if (quality2 != quality1)
					return quality1 - quality2;
			}

			// never return 0, new customers depend on random placement
			return (r1.getLotteryPosition() - r2.getLotteryPosition());
		}

	}

	private static class frontSeatCompare implements Comparator<Seat> {

		public int compare(Seat s1, Seat s2) {

			String r1 = s1.getRow();
			String r2 = s2.getRow();

			// ROW
			if (r1.length() != r2.length())
				return (r1.length() - r2.length());
			int c = r1.compareTo(r2);
			if (c != 0)
				return c;

			// QUAL
			int q = qualityCompare(s1, s2);
			if (q != 0)
				return q;

			// DISTANCE FROM CENTER
			int sc = seatNumberCompare(s1, s2);
			if (sc != 0)
				return sc;

			// never return 0
			return (s1.getRecordId() - s2.getRecordId());

		}

	}

	private static int seatNumberCompare(Seat s1, Seat s2) {
		// int center = Prefs.getIntPref(PrefName.NUMSEATS) / 2;
		int center = s1.getLayout().getCenterseat();
		if( center == 0 ) center = s1.getLayout().getNumSeats()/2;
		int seat1dist = Math.abs(center - s1.getSeat());
		int seat2dist = Math.abs(center - s2.getSeat());
		return (seat1dist - seat2dist);
	}

	private static int qualityCompare(Seat s1, Seat s2) {
		return (s2.getWeight() - s1.getWeight());
	}

	private static class rearSeatCompare implements Comparator<Seat> {

		public int compare(Seat s1, Seat s2) {

			String r1 = s1.getRow();
			String r2 = s2.getRow();

			// ROW
			if (r2.length() != r1.length())
				return (r2.length() - r1.length());
			int c = r2.compareTo(r1);
			if (c != 0)
				return c;

			// QUAL
			int q = qualityCompare(s1, s2);
			if (q != 0)
				return q;

			// DISTANCE FROM CENTER
			int sc = seatNumberCompare(s1, s2);
			if (sc != 0)
				return sc;

			// never return 0
			return (s1.getRecordId() - s2.getRecordId());

		}

	}

	private static class regularSeatCompare implements Comparator<Seat> {

		public int compare(Seat s1, Seat s2) {

			// QUAL
			int q = qualityCompare(s1, s2);
			if (q != 0)
				return q;

			String r1 = s1.getRow();
			String r2 = s2.getRow();

			// ROW
			if (r2.length() != r1.length())
				return (r1.length() - r2.length());
			int c = r1.compareTo(r2);
			if (c != 0)
				return c;

			// DISTANCE FROM CENTER
			int sc = seatNumberCompare(s1, s2);
			if (sc != 0)
				return sc;

			// never return 0
			return (s1.getRecordId() - s2.getRecordId());

		}

	}

	private static void dumpSeats(String title, Collection<Seat> seats) {
		if (logger.isLoggable(Level.INFO)) {
			StringBuffer sb = new StringBuffer();
			sb.append(title + ":\n");
			for (Seat s : seats) {
				sb.append(s.getRow() + "/" + s.getSeat() + " ");
			}
			logger.info(seats.size() + " seats");
			logger.info(sb.toString());

		}
	}

	private static void dumpRequests(Collection<Request> reqs) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info(reqs.size() + " requests");
			StringBuffer sb = new StringBuffer();
			for (Request s : reqs) {

				sb.append(s.getCustomerName() + " " + s.getSpecialNeeds() + " "
						+ s.getCustomer().getTotalTickets() + " "
						+ s.getCustomer().getTotalQuality() + "\n");
			}
			logger.info(sb.toString());

		}
	}
}
