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
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.InstanceBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.util.AuditLogger;

@Stateless
@RolesAllowed({ "tmadmin", "tmsite" })
@Interceptors({ AuditLogger.class })
public class InstanceBeanImpl implements InstanceBean {

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
			throw new EJBException("User " + userid + " not found");
		}
	}

	protected Instance getInstance() {
		return InstanceBeanImpl.getInstance(ejbContext, em);
	}

	@SuppressWarnings("unchecked")
	@RolesAllowed({ "tmsite" })
	public List<Instance> getAllInstances() {

		Query query = em.createQuery("SELECT e FROM Instance e ORDER BY name");
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

	@Override
	public Instance newRecord() {
		return (new Instance());
	}

	@Override
	public Instance save(Instance c) {
		c = em.merge(c);
		return c;
	}

	@Override
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
