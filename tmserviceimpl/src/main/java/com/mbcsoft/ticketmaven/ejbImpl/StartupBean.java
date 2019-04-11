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

import java.io.File;
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

@Singleton
@Startup

public class StartupBean {
	final static protected Logger logger = Logger.getLogger("com.mbcsoft.ticketmaven.ejb");

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void createDefaultData() {

		// create site instance if needed
		Instance siteInstance = null;
		try {
			Query query = em.createQuery("SELECT e FROM Instance e WHERE e.name = 'TicketMaven Site'");
			siteInstance = (Instance) query.getSingleResult();
		} catch (Exception e) {
			logger.info("**** Creating Site Instance ****");

			siteInstance = new Instance();
			siteInstance.setEnabled(true);
			siteInstance.setName("TicketMaven Site");
			em.persist(siteInstance);
		}

		// check for tmsite account
		Query query = em.createQuery("SELECT e FROM Customer e WHERE e.roles = 'tmsite'");
		List<Customer> siteAdmins = (List<Customer>) query.getResultList();

		// create default admin if needed
		if (siteAdmins == null || siteAdmins.isEmpty()) {
			logger.info("**** Creating Site Admin ****");

			Customer admin = new Customer();

			admin.setFirstName("Site");
			admin.setLastName("Admin");
			admin.setUserid("admin");
			admin.setPassword("admin"); // TODO - encrypt
			admin.setRoles("tmsite");
			admin.setInstance(siteInstance);
			admin.setResident("N");
			admin.setSpecialNeeds(Customer.NONE);
			
			em.persist(admin);

		}
		
		// load test data
		loadTestData();
	}
	
	public void loadTestData() {
		
		
		String fname = "c:/tmp/bellaggio.xml";
		File in = new File(fname);
		if( in.exists() && in.canRead()) {
			logger.info("Creating Test Data");
			
			// create instance admin account first
			Instance inst = new Instance();
			inst.setEnabled(true);
			inst.setName("bellaggio");
			em.persist(inst);

			Customer admin = new Customer();
			admin.setFirstName("bellaggio");
			admin.setLastName("Admin");
			admin.setUserid("bellaggio");
			admin.setPassword("mike");
			admin.setRoles("tmadmin");
			admin.setInstance(inst);
			admin.setResident("Y");
			admin.setSpecialNeeds(Customer.NONE);

			em.persist(admin);
			
			// do not import the file here - that will be done via the UI
			// only the tmadmin user is created here
			
			
		}
	}
	
	
}
