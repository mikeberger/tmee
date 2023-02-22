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

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJBContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.mbcsoft.ticketmaven.entity.BaseAppTable;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;

@RolesAllowed({"tmuser", "tmadmin", "tmsite"})
public abstract class BaseEntityFacadeImpl<T extends BaseAppTable>  {

	static protected Logger logger = Logger
	.getLogger("com.mbcsoft.ticketmaven.ejb");

	@PersistenceContext
	protected EntityManager em;

	@Resource
	protected EJBContext ejbContext;

	public BaseEntityFacadeImpl() {
	}

	public T save(T c) {

		c.setInstance(getInstance());
		c = em.merge(c);
		logger.fine("saving T: " + c.toString() + " instance: " + c.getInstance().getName());
		return c;
	}


	public void delete(T c) {

		if( !em.contains(c))
			c = em.merge(c);
		em.remove(c);

	}

	@SuppressWarnings("unchecked")
	protected List<T> getAll(String type) {

		Query query = em.createQuery("SELECT e FROM " + type + " e WHERE e.instance = :inst");
		query.setParameter("inst", getInstance());
		return (List<T>) query.getResultList();
	}


	
	public Customer getCurrentCustomer() {

		// also set instance from user tbl
		String userid = ejbContext.getCallerPrincipal().getName();
		try {

			Customer c = (Customer) em.createNamedQuery("findCustomerByUserid")
					.setParameter("id", userid).getSingleResult();

			return c;

		} catch (Throwable t) {
			return null;
		}
	}

	public T get(Class<? extends T> c, String id) {
		int iid = Integer.parseInt(id);
		return em.find(c, iid);
	}
	
	public void delete(Class<? extends T> c, String id) {
		T obj = get(c,id);
		if( obj != null)
			em.remove(obj);
	}

	protected Instance getInstance()
	{
		return InstanceBean.getInstance(ejbContext, em);
	}
}
