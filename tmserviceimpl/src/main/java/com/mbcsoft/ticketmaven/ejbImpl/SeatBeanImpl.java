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
@RolesAllowed( { "tmuser", "tmadmin", "tmsite" })
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
				.createQuery("SELECT e FROM Seat e WHERE e.layout = :layout ORDER BY e.row, e.seat");
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

				seat.setAvailable(true);
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

			if (!seat.getAvailable())
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
