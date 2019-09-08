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
package com.mbcsoft.ticketmaven.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
import com.mbcsoft.ticketmaven.ejbImpl.InstanceBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.util.PasswordUtil;

@Named("adminBB")
@SessionScoped
public class AdminBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Customer cust;
	@EJB private CustomerBean rbean;
	@EJB private InstanceBean ibean;
	
	private String instname;

	private List<Customer> list = new ArrayList<Customer>();
	
	static public void refreshSessionList() {
		// find showBB in session
		FacesContext ctx = FacesContext.getCurrentInstance();
		AdminBB bb = ctx.getApplication().evaluateExpressionGet(ctx, "#{custBB}", AdminBB.class);
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
			
			instname = cust.getInstance().getName();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void refreshList() {
		try {

			setList(rbean.getAllAdmins());

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

			cust.setInstance(ibean.getInstance(instname));
			
			cust.setPassword(PasswordUtil.hexHash(cust.getPassword()));
			
			rbean.saveAdmin(cust);
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
	

	public List<String> getAllInstances() {
		List<String> ret = new ArrayList<String>();
		for( Instance z : ibean.getAllInstances() )
			ret.add(z.getName());
		return ret;
	}
	
	public List<Customer> getAllAdmins() {
		return rbean.getAll();
	}

	public String getInstname() {
		return instname;
	}

	public void setInstname(String instname) {
		this.instname = instname;
	}

}
