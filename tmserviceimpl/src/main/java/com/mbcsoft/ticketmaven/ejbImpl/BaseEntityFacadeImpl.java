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

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
		//logger.warning("saving T: " + c.toString() + " instance: " + c.getInstance().getName());
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
		return InstanceBeanImpl.getInstance(ejbContext, em);
	}
}
