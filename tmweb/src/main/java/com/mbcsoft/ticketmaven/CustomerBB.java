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
package com.mbcsoft.ticketmaven;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.ejb.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Zone;

@Named("custBB")
@SessionScoped
public class CustomerBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Customer cust;
	@EJB private CustomerBean rbean;
	@EJB private ZoneBean zbean;

	private List<Customer> list = new ArrayList<Customer>();
	
	static public void refreshSessionList() {
		// find showBB in session
		FacesContext ctx = FacesContext.getCurrentInstance();
		CustomerBB bb = ctx.getApplication().evaluateExpressionGet(ctx, "#{custBB}", CustomerBB.class);
		bb.refreshList();
	}

	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("cust_id");

			if (id == null || "".equals(id)) {
				cust = rbean.getCurrentCustomer();
			} else {
				cust = rbean.get(Customer.class, id);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void refreshList() {
		try {

			// get requests for current logged in customer
			setList(rbean.getAll());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void delete() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("cust_id");
			if (id == null || "".equals(id))
				return;

			//Customer c = rbean.get(Customer.class, id);
			rbean.delete(Customer.class,id);

			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void newRecord() {

		try {

			cust = rbean.newRecord();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void save() {
		try {

			rbean.save(cust);
			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setCust(Customer cust) {
		this.cust = cust;
	}

	public Customer getCust() {
		return cust;
	}

	public void setList(List<Customer> list) {
		this.list = list;
	}

	public List<Customer> getList() {
		return list;
	}
	
	public List<String> getAllneeds() {
		List<String> ret = new ArrayList<String>();
		for( Zone z : zbean.getAll() )
			ret.add(z.getName());
		return ret;
	}

}
