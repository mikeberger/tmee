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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;
import com.mbcsoft.ticketmaven.entity.Zone;

@Singleton
@Startup

public class StartupBean {
	final static protected Logger logger = Logger.getLogger("com.mbcsoft.ticketmaven.ejb");

	@PersistenceContext
	private EntityManager em;

	@PostConstruct
	public void generateTestData() {

		Query q = em.createQuery("delete from Ticket t");
		q.executeUpdate();
		q = em.createQuery("delete from Reservation t");
		q.executeUpdate();
		q = em.createQuery("delete from Request t");
		q.executeUpdate();
		q = em.createQuery("delete from TMTable t");
		q.executeUpdate();
		q = em.createQuery("delete from TMPackage t");
		q.executeUpdate();
		q = em.createQuery("delete from Show t");
		q.executeUpdate();
		q = em.createQuery("delete from Customer t");
		q.executeUpdate();
		q = em.createQuery("delete from Seat t");
		q.executeUpdate();
		q = em.createQuery("delete from Zone t");
		q.executeUpdate();
		q = em.createQuery("delete from Layout t");
		q.executeUpdate();

		q = em.createQuery("delete from Instance t");
		q.executeUpdate();
		// em.flush();

		logger.info("Deleted all data!");

		createInstData(em, "bellaggio");
		createInstData(em, "valencia");
		
		Instance inst = new Instance();
		inst.setEnabled(true);
		inst.setName("TicketMaven Site");
		em.persist(inst);

		Customer mbb = new Customer();

		mbb.setFirstName("Site");
		mbb.setLastName("Admin");
		mbb.setUserid("admin");
		mbb.setPassword("admin");
		mbb.setRoles("tmsite");
		mbb.setInstance(inst);
		mbb.setResident("Y");
		mbb.setSpecialNeeds(Customer.NONE);

		em.persist(mbb);


	}

	private void createInstData(EntityManager em, String is) {
		// add user mbb if doesn't exist

		Instance inst = new Instance();
		inst.setEnabled(true);
		inst.setName(is);
		em.persist(inst);

		Customer mbb = new Customer();

		mbb.setFirstName(is);
		mbb.setLastName("Admin");
		mbb.setUserid(is);
		mbb.setPassword("mike");
		mbb.setRoles("tmadmin");
		mbb.setInstance(inst);
		mbb.setResident("Y");
		mbb.setSpecialNeeds(Customer.NONE);

		em.persist(mbb);

		ArrayList<Zone> zonelist = new ArrayList<Zone>();

		for (String zone : new String[] { "Mobility Impaired", "Hearing Impaired", "Lavatory", "Vision Impaired",
				"Front Row", "Lip Reader" }) {
			Zone bzone = new Zone();
			bzone.setExclusive(false);
			bzone.setInstance(inst);
			bzone.setName(zone);
			em.persist(bzone);
			zonelist.add(bzone);
		}

		Zone bzone = new Zone();
		bzone.setExclusive(true);
		bzone.setInstance(inst);
		bzone.setName("Wheel Chair");
		em.persist(bzone);
		zonelist.add(bzone);

		Layout l = new Layout();
		l.setName("Aud 1");
		l.setInstance(inst);
		l.setNumRows(10);
		l.setNumSeats(20);
		//l.setSeating(Layout.AUDITORIUM);
		em.persist(l);

		generateMissingSeats(l, inst);

		String[] shownames = { "Capitol Steps", "Little Anthony", "Robert Klein", "Swing Dance", "Pat Cooper",
				"Lenny Rush", "Vampires Ball", "Liberace", "Tommy Tune", "West Side Story" };

		Show show1 = null;
		for (int i = 1; i <= shownames.length; i++) {
			Show s = new Show();
			s.setName(shownames[i - 1]);
			s.setCost(i * 1000);
			s.setInstance(inst);
			s.setLayout(l);
			s.setPrice(i * 100);
			s.setTime(new Timestamp(new Date().getTime() + (long) 1000 * 60 * 60 * 24 * i));
			em.persist(s);
			if (i == 1)
				show1 = s;
		}

		loadCusts(inst, l, show1, zonelist);

		logger.info("Initialization Complete for " + is);

	}

	private void loadCusts(Instance inst, Layout l, Show show1, ArrayList<Zone> zonelist) {

		List<Seat> sts = getSeats(l);
		String fname = "c:/tmp/customers.csv";
		File in = new File(fname);
		if (!in.exists() || !in.canRead()) {
			for (int i = 1; i < 50; i++) {
				Customer c = new Customer();
				c.setFirstName("John");
				c.setLastName("Smith-" + i);
				c.setResident("Y");
				c.setUserid(inst.getName() + "cust" + i);
				c.setPassword(c.getUserid());
				c.setRoles("tmuser");
				c.setInstance(inst);
				c.setSpecialNeeds(Customer.NONE);
				c.setPhone("908-" + i);

				// if (i <= 20)
				// c.setSpecialNeeds(zonelist.get(i%zonelist.size()).getName());
				em.persist(c);

				Request r = new Request();
				r.setCustomer(c);
				r.setInstance(inst);
				r.setShow(show1);
				r.setTickets(3);
				r.setPaid(true);
				em.persist(r);

				Ticket t = new Ticket();
				t.setCustomer(c);
				t.setInstance(inst);
				t.setSeat(sts.get(i));
				t.setShow(show1);
				t.setTicketPrice(2);
				em.persist(t);

				t = new Ticket();
				t.setCustomer(c);
				t.setInstance(inst);
				t.setSeat(sts.get(i + 50));
				t.setShow(show1);
				t.setTicketPrice(2);
				em.persist(t);

			}
		} else {
			String line = null;
			try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {

				int i = 1;
				while ((line = reader.readLine()) != null) {

					// add customer
					String[] fields = line.split(",");
					Customer c = new Customer();
					c.setFirstName(fields[1]);
					c.setLastName(fields[0]);
					c.setResident("Y");
					c.setUserid(inst.getName() + i);
					c.setPassword(c.getUserid());
					c.setPassword("user");
					c.setRoles("tmuser");
					c.setInstance(inst);
					c.setPhone("908-" + i);
					if (i <= 50)
						c.setSpecialNeeds(zonelist.get(i % zonelist.size()).getName());
					else
						c.setSpecialNeeds(Customer.NONE);
					em.persist(c);
					i++;

					em.persist(c);

					Request r = new Request();
					r.setCustomer(c);
					r.setInstance(inst);
					r.setShow(show1);
					r.setTickets(3);
					r.setPaid(true);
					em.persist(r);
					
					if( i > 100 ) break;

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private Seat getSeat(String row, int seat, Layout l) {
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
	private List<Seat> getSeats(Layout l) {
		Query query = em.createQuery("SELECT e FROM Seat e WHERE e.layout = :layout");
		query.setParameter("layout", l);
		return (List<Seat>) query.getResultList();
	}

	private void generateMissingSeats(Layout layout, Instance inst) {

		int rows = layout.getNumRows();
		int seats = layout.getNumSeats();
		// logger.info("Layout id=" + layout.getRecordId());

		for (int r = 0; r < rows; r++) {
			String row = Seat.rowletters.substring(r, r + 1);
			for (int s = 1; s <= seats; s++) {

				Seat seat = getSeat(row, s, layout);
				if (seat != null)
					continue;

				seat = new Seat();
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
				seat.setInstance(inst);
				seat.setLabel(Integer.toString(s));
				em.persist(seat);
			}

		}

	}

}
