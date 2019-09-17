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
package com.mbcsoft.ticketmaven.ejbImpl;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;
import com.mbcsoft.ticketmaven.entity.Zone;

@Stateless
@RolesAllowed({"tmadmin"})
public class LotteryManager  {

	final private static Logger logger = Logger.getLogger("com.mbcsoft.ticketmaven.ejb");

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

	public void runLottery(Show show) {
		Collection<Seat> availableSeats = seatbean.getAvailableSeatsForShow(show);

		// run the lottery with a capacity limit so that a random set of requests get
		// chosen for a show that is sold out
		runLottery(show, availableSeats.size());

		Collection<Request> reqs = requestbean.getRequestsForShow(show);

		// there are still outstanding requests. run another lottery with no capacity
		// limit so that every request will
		// be considered. This may fill in the remaining seats
		if (!reqs.isEmpty()) {
			runLottery(show, -1);
		}
	}

	private void runLottery(Show s, int capacity) {

		logger.info("Running Lottery for show " + s.getRecordId() + " capacity=" + capacity);

		// fetch a copy of show that is managed
		Show show = em.find(Show.class, s.getRecordId());

		// get the list of available seats for the show
		Collection<Seat> availableSeats = seatbean.getAvailableSeatsForShow(show);

		dumpSeats("initial AVAIL", availableSeats);

		// get the list of requests
		Collection<Request> requests = getRequests(show, capacity);

		// sort Requests in the order that they should be honored
		// requests are sorted by past quality plus a random factor for
		// people who have never gotten tickets.
		// We will eventually assign tickets from
		// the best to the worst, so at this point, we are ordering folks in
		// the line at the door.
		// the folks who got the worst seats in the past are put at the
		// front of the line.
		TreeSet<Request> sortedRequests = new TreeSet<Request>(new requestCompare());
		sortedRequests.addAll(requests);

		dumpRequests(sortedRequests);

		// NOTE: once we start assigning tickets, we need to make sure that
		// we do not
		// break up a party across an aisle or in non-contiguous seats. So,
		// if we ever get to the point where
		// there are single empty seats scattered about, the program will
		// not assign request for
		// 2 or more tickets. If the user wants to break up ticket requests
		// into smaller parties, then
		// that must be done manually.

		// assign front row seats
		// people with special needs type of front only are assigned here.
		// if they don't get a front row seat - they get no ticket
		assignSeats(show, getFrontRowSeats(availableSeats), sortedRequests, Customer.FRONT_ONLY);

		// assign all special seats from zone table
		// now we assign all of the special needs people according to the
		// user's custom special needs
		// if a special needs person does not get an available seat that
		// matches their need - they get no ticket
		// it's important to make sure there are enough special needs seats
		// when creating the layout
		// NOTE: all assignment still is servicing people in order of past
		// quality whether it's
		// special needs seating or regular
		Collection<Zone> zones = zonebean.getAll();
		for (Zone z : zones) {
			Collection<Seat> zoneSeats = seatbean.getAvailableSeatsForShow(show, z);
			assignSeats(show, zoneSeats, sortedRequests, z.getName());
		}

		// now we assign aisle seats for people with the built-in Aisle
		// needs type
		availableSeats = seatbean.getAvailableSeatsForShow(show);
		assignAisleSeats(show, availableSeats, sortedRequests);

		// assign front seats - the folks with this special need just get
		// whatever seats a
		// left as close to the front as possible
		availableSeats = seatbean.getAvailableSeatsForShow(show);
		TreeSet<Seat> frontSortedSeats = new TreeSet<Seat>(new frontSeatCompare());
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
		// now we assign anyone without a special need to whatever seats are
		// left...
		// still in order of past quality.
		TreeSet<Seat> regularSortedSeats = new TreeSet<Seat>(new regularSeatCompare());
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
			show_price = (int) ((double) show_price * (1 - (double) req.getDiscount() / 100.0));
		}

		// create ticket records
		int total_quality = 0;
		for (Seat s : seats) {

			Ticket tkt = new Ticket();
			tkt.setCustomer(req.getCustomer());
			tkt.setSeat(s);
			tkt.setShow(show);
			logger.info("Assigning Ticket: " + req.getCustomerName() + ":" + s.getRow() + "/" + s.getSeat());

			// assign price from show with discount from request

			tkt.setTicketPrice(show_price);
			ticketbean.save(tkt);

			total_quality += s.getWeight();
		}

		// update the quality counts for the customer
		Customer cust = req.getCustomer();
		cust.setTotalTickets(req.getTickets() + cust.getTotalTickets());
		cust.setTotalQuality(total_quality + cust.getTotalQuality());
		custbean.save(cust);

		// delete request from DB
		requestbean.delete(req);

	}

	private Collection<Seat> getContiguousSeats(Show show, Collection<Seat> availableSeats, Seat startingSeat,
			int number, boolean aisle) throws NotEnoughSeats {

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
				if (ns.getSeat() == nextseat && ns.getRow().equals(startingSeat.getRow())) {
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

	private void assignAisleSeats(Show show, Collection<Seat> availableSeats, Collection<Request> requests) {

		Collection<Seat> aisleSeats = getAisleSeats(availableSeats);

		dumpSeats("Aisle Seats", aisleSeats);

		Iterator<Request> it = requests.iterator();
		while (it.hasNext()) {

			Request tr = it.next();

			// if not an AISLE request, then stop, AISLE requests should sort
			// first
			if (tr.getSpecialNeeds() != null && tr.getSpecialNeeds().equals(Customer.AISLE)) {
				// assign N contiguous seats

				// loop through the aisle seats
				Iterator<Seat> seatit = aisleSeats.iterator();
				while (seatit.hasNext()) {
					try {

						// see if this aisle seat has enough contiguous seats to
						// satisfy the requested number of seats
						Collection<Seat> seats = getContiguousSeats(show, availableSeats, seatit.next(),
								tr.getTickets(), true);

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

	private void assignSeats(Show show, Collection<Seat> sortedSeats, Collection<Request> requests,
			String special_needs) {

		logger.info("Assigning seats: " + special_needs);

		// for each request
		Iterator<Request> it = requests.iterator();
		while (it.hasNext()) {

			// skip any requests that are not for the special need that we are
			// assigning
			Request tr = it.next();
			if (tr.getSpecialNeeds() == null || !tr.getSpecialNeeds().equals(special_needs))
				continue;

			// loop through the seats, which are sorted from best to worst
			// we will hunt for seats starting with every seat in the place
			// until we find a bunch of available seats or give up
			// the seats are sorted from best to worst and the requests are
			// sorted
			// with the most deserving on top - so each request will get the best
			// seats available
			// that can satisfy it.
			Iterator<Seat> seatit = sortedSeats.iterator();
			while (seatit.hasNext()) {
				try {

					// see if this seat has enough contiguous seats to
					// satisfy the requested number of seats
					Collection<Seat> seats = getContiguousSeats(show, sortedSeats, seatit.next(), tr.getTickets(),
							false);

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

	private Collection<Request> getRequests(Show show, int capacity) {

		Collection<Request> req = requestbean.getPaidRequestsForShow(show);
		logger.info("total requests for show:" + req.size());
		dumpRequests(req);

		Collection<Request> winners = new ArrayList<Request>();
		Random ran = new Random();
		int winningseats = 0;

		ArrayList<Request> pool = new ArrayList<Request>(req);
		while (pool.size() > 0) {
			int idx = ran.nextInt(pool.size());
			Request tr = pool.remove(idx);

			winners.add(tr);
			tr.setLotteryPosition(winners.size());
			winningseats += tr.getTickets();
			
			if (capacity != -1 && winningseats > capacity) {
				logger.info("Sold-out picked winners...");
				break;
			}

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

			if (r1.getCustomer().getTotalTickets() != 0 && r2.getCustomer().getTotalTickets() != 0) {
				int quality1 = (100000 * r1.getCustomer().getTotalQuality()) / r1.getCustomer().getTotalTickets();

				int quality2 = (100000 * r2.getCustomer().getTotalQuality()) / r2.getCustomer().getTotalTickets();

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
		if (center == 0)
			center = s1.getLayout().getNumSeats() / 2;
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

				sb.append(s.getCustomerName() + " " + s.getSpecialNeeds() + " " + s.getCustomer().getTotalTickets()
						+ " " + s.getCustomer().getTotalQuality() + "\n");
			}
			logger.info(sb.toString());

		}
	}

	/**
	 * Undo a lottery. This method will convert all assgined tickets for a show back
	 * inot the original requests. It also undoes the updates to a customer's past
	 * ticket quality. It has to group tickets together to build up requests for
	 * multiple tickets.
	 *
	 * @throws Exception the exception
	 */
	public void undoLottery(Show show) {

		// hashmap to hold requests
		HashMap<Integer, Request> rmap = new HashMap<Integer, Request>();

		// hashmap to hold weights
		HashMap<Integer, Integer> wmap = new HashMap<Integer, Integer>();

		// collect up all tickets
		Collection<Ticket> tkts = ticketbean.getTicketsForShow(show);

		for (Ticket t : tkts) {
			Integer custid = t.getCustomer().getRecordId();
			Request req = rmap.get(custid);
			if (req == null) {
				// add a new request for this customer
				req = new Request();
				req.setCustomer(t.getCustomer());
				req.setShow(t.getShow());
				req.setTickets(Integer.valueOf(1));
				req.setPaid(true);
				// calculate the discount
				double d = 100.0 * (1.0 - Integer.valueOf(t.getTicketPrice()).doubleValue()
						/ Integer.valueOf(show.getPrice()).doubleValue());
				req.setDiscount(Double.valueOf(d));
				rmap.put(custid, req);

				// add an entry for the seat weight being deleted in this
				// ticket
				wmap.put(custid, t.getSeat().getWeight());
			} else {
				// found a request - just bump the number of tickets
				req.setTickets(Integer.valueOf(req.getTickets() + 1));

				// add to the weight total so we can subtract it from the
				// customer later
				Integer w = wmap.get(custid);
				wmap.put(custid, Integer.valueOf(t.getSeat().getWeight() + w.intValue()));
			}

		}

		// save requests
		for (Request r : rmap.values()) {
			try {
				requestbean.save(r);
			} catch (Exception e) {
				// got here if request already exists - only happens if they
				// added a second request after the lottery for the same
				// customer
				// and show - so we need to add to that request
				List<Request> currentRequests = requestbean.getRequestsForCustomer(r.getCustomer());
				for (Request current : currentRequests) {
					if (current.getShow().getRecordId() == r.getShow().getRecordId()) {
						current.setTickets(current.getTickets() + r.getTickets());
						requestbean.save(current);
						break;
					}
				}

			}

			// adjust customer's quality
			Customer c = r.getCustomer();
			c.setTotalTickets(Integer.valueOf(c.getTotalTickets() - r.getTickets()));
			int w = wmap.get(r.getCustomer().getRecordId()).intValue();
			c.setTotalQuality(Integer.valueOf(c.getTotalQuality() - w));
			if (c.getTotalTickets() < 0)
				c.setTotalTickets(0);
			if (c.getTotalQuality() < 0)
				c.setTotalQuality(0);
			custbean.save(c);
		}

		// delete all tickets for show
		ticketbean.deleteTicketsForShow(show);

	}
}
