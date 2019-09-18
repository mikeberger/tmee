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

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.util.AuditLogger;

@Stateless
@RolesAllowed({ "tmadmin", "tmsite" })
@Interceptors({ AuditLogger.class })
public class InstanceBean  {

	static protected Logger logger = Logger.getLogger("com.mbcsoft.ticketmaven.ejb");

	@PersistenceContext
	protected EntityManager em;

	@Resource
	protected EJBContext ejbContext;


	static public Instance getInstance(EJBContext context, EntityManager em) {

		// also set instance from user tbl
		String userid = context.getCallerPrincipal().getName();
		try {

			Customer c = (Customer) em.createNamedQuery("findCustomerByUserid").setParameter("id", userid)
					.getSingleResult();

			return c.getInstance();

		} catch (Throwable t) {
			t.printStackTrace();
			throw new EJBException("User " + userid + " not found");
		}
	}

	public Instance getInstance() {
		return InstanceBean.getInstance(ejbContext, em);
	}

	@SuppressWarnings("unchecked")
	@RolesAllowed({ "tmsite" })
	public List<Instance> getAllInstances() {

		// don't show site instance - it is not to be edited or deleted
		Query query = em.createQuery("SELECT e FROM Instance e WHERE e.name <> 'TicketMaven Site' ORDER BY name");
		return (List<Instance>) query.getResultList();

	}

	public Instance getInstance(String instname) {
		Query query = em.createQuery("SELECT e FROM Instance e WHERE e.name = :name");
		query.setParameter("name", instname);
		try {
			Instance l = (Instance) (query.getSingleResult());
			return l;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@RolesAllowed({ "tmsite" })
	public Instance newRecord() {
		return (new Instance());
	}

	@RolesAllowed({ "tmsite" })
	public Instance save(Instance c) {
		c = em.merge(c);
		return c;
	}

	@RolesAllowed({ "tmsite" })
	public void delete(Instance c) {
		if (!em.contains(c))
			c = em.merge(c);
		em.remove(c);

	}

	public Instance getById( String id) {
		int iid = Integer.parseInt(id);
		return em.find(Instance.class, iid);
	}

}
