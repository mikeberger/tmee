package com.mbcsoft.ticketmaven.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.github.javafaker.Faker;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Zone;

public class GenTestData {
	final static protected Logger logger = Logger.getLogger("com.mbcsoft.ticketmaven.ejb");

	public void generateTestData(EntityManager em) {

		createInstData(em, "bellaggio");
	}

	private void createInstData(EntityManager em, String is) {

		logger.info("Creating Test Data");

		// create instance admin account first
		Instance inst = new Instance();
		inst.setEnabled(true);
		inst.setName(is);
		em.persist(inst);

		Customer admin = new Customer();
		admin.setFirstName(is);
		admin.setLastName("Admin");
		admin.setUserid(is);
		admin.setPassword("mike");
		admin.setRoles("tmadmin");
		admin.setInstance(inst);
		admin.setResident("Y");
		admin.setSpecialNeeds(Customer.NONE);

		em.persist(admin);

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
		l.setNumRows(20);
		l.setNumSeats(40);
		em.persist(l);

		generateMissingSeats(em, l, inst);

		Show show1 = null;
		for (int i = 1; i <= 40; i++) {
			Show s = new Show();
			Faker faker = new Faker();
			s.setName(faker.funnyName().name());
			s.setCost(i * 1000);
			s.setInstance(inst);
			s.setLayout(l);
			s.setPrice(i * 100);
			s.setTime(new Timestamp(faker.date().future(100, 7, TimeUnit.DAYS).getTime()));
			em.persist(s);
			if (i == 1)
				show1 = s;
		}

		loadCusts(em, inst, l, show1, zonelist);

		logger.info("Initialization Complete for " + is);

	}

	private void loadCusts(EntityManager em, Instance inst, Layout l, Show show1, ArrayList<Zone> zonelist) {

		for (int i = 1; i < 250; i++) {
			Faker faker = new Faker();

			Customer c = new Customer();
			c.setFirstName(faker.name().firstName());
			c.setLastName(faker.name().lastName());
			c.setResident("Y");
			c.setUserid(inst.getName() + "cust" + i);
			c.setPassword(c.getUserid());
			c.setRoles("tmuser");
			c.setInstance(inst);
			c.setSpecialNeeds(Customer.NONE);
			c.setPhone(faker.phoneNumber().phoneNumber());
			c.setAddress(faker.address().fullAddress());
			c.setAllowedTickets(2);

			if (i <= 20)
				c.setSpecialNeeds(zonelist.get(i % zonelist.size()).getName());
			try {
				em.persist(c);
				Request r = new Request();
				r.setCustomer(c);
				r.setInstance(inst);
				r.setShow(show1);
				r.setTickets(2);
				r.setPaid(true);
				em.persist(r);
			} catch (Exception e) {
				logger.warning(e.getMessage());
			}

		

		}

	}

	private Seat getSeat(EntityManager em, String row, int seat, Layout l) {
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

	private void generateMissingSeats(EntityManager em, Layout layout, Instance inst) {

		int rows = layout.getNumRows();
		int seats = layout.getNumSeats();
		// logger.info("Layout id=" + layout.getRecordId());

		for (int r = 0; r < rows; r++) {
			String row = Seat.rowletters.substring(r, r + 1);
			for (int s = 1; s <= seats; s++) {

				Seat seat = getSeat(em, row, s, layout);
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
