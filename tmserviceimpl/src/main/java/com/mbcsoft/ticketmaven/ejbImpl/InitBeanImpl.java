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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.InitBean;
import com.mbcsoft.ticketmaven.ejb.SeatBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Zone;

/**
 * Session Bean implementation class InitBeanImpl
 */
@Stateless
@DeclareRoles( { "tmadmin", "tmuser" })
public class InitBeanImpl implements InitBean {

	final static protected Logger logger = Logger
			.getLogger("com.mbcsoft.ticketmaven.ejb");

	@PersistenceContext
	private EntityManager em;

	@EJB
	private SeatBean seatbean;

	@Resource
	private EJBContext ejbContext;

	public InitBeanImpl() {
	}


	@PermitAll
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
		em.flush();

		logger.info("Deleted all data!");

		// add user mbb if doesn't exist

		Instance inst = new Instance();
		inst.setEnabled(true);
		inst.setName("mbcsoft");
		em.persist(inst);

		Customer mbb = new Customer();

		mbb.setFirstName("Michael");
		mbb.setLastName("Berger");
		mbb.setUserid("mike");
		//mbb.setPassword("e899877691f405507dcd8c078df3daf3");
		mbb.setInstance(inst);
		mbb.setResident("Y");
		mbb.setSpecialNeeds(Customer.NONE);

		em.persist(mbb);


		Customer uuu = new Customer();

		uuu.setFirstName("Joe");
		uuu.setLastName("User");
		uuu.setUserid("user");
		//mbb.setPassword("e899877691f405507dcd8c078df3daf3");
		uuu.setInstance(inst);
		uuu.setResident("Y");
		uuu.setSpecialNeeds(Customer.NONE);

		em.persist(uuu);

		Zone bzone = new Zone();
		bzone.setExclusive("Y");
		bzone.setInstance(inst);
		bzone.setName("B row reserved");
		em.persist(bzone);

		Zone dzone = new Zone();
		dzone.setExclusive("N");
		dzone.setInstance(inst);
		dzone.setName("D row special");
		em.persist(dzone);

		Layout l = new Layout();
		l.setName("Aud 1");
		l.setInstance(inst);
		l.setNumRows(10);
		l.setNumSeats(20);
		l.setSeating(Layout.AUDITORIUM);
		em.persist(l);

		seatbean.generateMissingSeats(l);

		List<Seat> sts = seatbean.getSeats(l);
		for (Seat st : sts) {
			if (st.getRow().equals("D")) {
				st.setZone(dzone);
				em.merge(st);
			} else if (st.getRow().equals("B")) {
				st.setZone(bzone);
				em.merge(st);
			}
		}

		/*
		 * l = new Layout(); l.setName("Tbl 1"); l.setInstanceName("mbcsoft");
		 * l.setSeating(Layout.TABLE); em.persist(l);
		 */
		Show show1 = null;
		for (int i = 1; i <= 10; i++) {
			Show s = new Show();
			s.setName("Show-" + i);
			s.setCost(i * 1000);
			s.setInstance(inst);
			s.setLayout(l);
			s.setPrice(i * 100);
			s.setTime(new Timestamp(new Date().getTime() + (long) 1000 * 60
					* 60 * 24 * 100));
			em.persist(s);
			if (i == 1)
				show1 = s;
		}

		for (int i = 1; i < 50; i++) {
			Customer c = new Customer();
			c.setFirstName("John");
			c.setLastName("Smith-" + i);
			c.setResident("Y");
			c.setUserid("cust" + i);
			c.setInstance(inst);
			c.setSpecialNeeds(Customer.NONE);
			c.setPhone("908-" + i);

			if (i <= 10)
				c.setSpecialNeeds(dzone.getName());
			em.persist(c);

			Request r = new Request();
			r.setCustomer(c);
			r.setInstance(inst);
			r.setShow(show1);
			r.setTickets(3);
			r.setPaid(true);
			em.persist(r);
		}


		Request r = new Request();
		r.setCustomer(mbb);
		r.setInstance(inst);
		r.setShow(show1);
		r.setTickets(3);
		r.setPaid(true);
		em.persist(r);



	}

	@RolesAllowed( { "tmadmin", "tmuser" })
	public boolean verifyRole(String role) {

		logger.info("Checking if " + ejbContext.getCallerPrincipal().getName()
				+ " is in role " + role);
		return ejbContext.isCallerInRole(role);

	}

}
