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
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.SeatBean;
import com.mbcsoft.ticketmaven.ejb.TicketBean;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;
import com.mbcsoft.ticketmaven.entity.Zone;

@Stateless
@RolesAllowed( { "tmuser", "tmadmin" })
public class SeatBeanImpl extends BaseEntityFacadeImpl<Seat> implements
		SeatBean {

	@EJB
	TicketBean tbean;

	public SeatBeanImpl() {
	}

	public Seat newRecord() {
		return (new Seat());
	}

	public List<Seat> getAll() {
		return getAll("Seat");
	}

	public Seat getSeat(String row, int seat, Layout l) {
		Query query = em
				.createQuery("SELECT e FROM Seat e WHERE e.row = :row AND e.seat = :seat AND e.layout = :layout");
		query.setParameter("row", row);
		query.setParameter("seat", seat);
		query.setParameter("layout", l);
		Seat s = null;
		try {
			s = (Seat) query.getSingleResult();
		} catch (Exception e) {

		}
		return s;
	}

	@SuppressWarnings("unchecked")
	public List<Seat> getSeats(Layout l) {
		Query query = em
				.createQuery("SELECT e FROM Seat e WHERE e.layout = :layout");
		query.setParameter("layout", l);
		return (List<Seat>) query.getResultList();
	}

	public void generateMissingSeats(Layout layout) {

		int rows = layout.getNumRows();
		int seats = layout.getNumSeats();
		//logger.info("Layout id=" + layout.getRecordId());

		for (int r = 0; r < rows; r++) {
			String row = Seat.rowletters.substring(r, r + 1);
			for (int s = 1; s <= seats; s++) {

				Seat seat = getSeat(row, s, layout);
				if (seat != null)
					continue;

				seat = newRecord();
				seat.setRow(row);
				seat.setSeat(s);
				if (s == 1)
					seat.setEnd(Seat.LEFT);
				else if (s == seats)
					seat.setEnd(Seat.RIGHT);
				else
					seat.setEnd(Seat.NONE);

				int q = Seat.MAX_WEIGHT - r;
				if (q < 1)
					q = 1;
				seat.setWeight(q);

				seat.setAvailable("Y");
				seat.setLayout(layout);
				save(seat);
			}

		}

	}

	public List<Seat> getAvailableSeatsForShow(Show s) {
		return getAvailableSeatsForShow(s, null);
	}

	public List<Seat> getAvailableSeatsForShow(Show s, Zone z) {

		List<Seat> seats = getSeats(s.getLayout());
		List<Ticket> tickets = tbean.getTicketsForShow(s);

		List<Seat> avail = new ArrayList<Seat>();

		for (Seat seat : seats) {
			if (seat.getZone() != null
					&& seat.getZone().getExclusive())
				continue;

			if (z != null) {
				if (seat.getZone() == null
						|| seat.getZone().getRecordId() != z.getRecordId())
					continue;
			}

			if ("N".equals(seat.getAvailable()))
				continue;

			avail.add(seat);
		}

		for (Ticket t : tickets) {
			if (avail.contains(t.getSeat()))
				avail.remove(t.getSeat());
		}

		return avail;

	}
}
