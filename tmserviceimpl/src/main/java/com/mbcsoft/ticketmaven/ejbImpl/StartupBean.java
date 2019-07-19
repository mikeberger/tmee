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
import com.mbcsoft.ticketmaven.util.GenTestData;

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
		new GenTestData().generateTestData(em);
	}

	
}
