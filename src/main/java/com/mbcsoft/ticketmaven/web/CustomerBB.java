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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
import com.mbcsoft.ticketmaven.ejbImpl.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Zone;
import com.mbcsoft.ticketmaven.util.PasswordUtil;

@Named("custBB")
@SessionScoped
public class CustomerBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Customer cust;
	private String origPassword;
	private String oldpw;
	private String newpw;
	@EJB private CustomerBean rbean;
	@EJB private ZoneBean zbean;

	static private final Logger logger = Logger
			.getLogger(CustomerBB.class.getName());
	
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
			
			origPassword = cust.getPassword();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void refreshList() {
		try {

			setList(rbean.getAll());
			logger.fine("refresh: size=" + list.size());

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
			cust.setRoles("tmuser");
			String pw = "FFFFDDEEAAFFAACCBBFF";
			origPassword = pw;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void chgpw() {
		//logger.info("origpw=" + oldpw + " newpw=" + newpw);
		
		if( !cust.getPassword().equals(PasswordUtil.hexHash(oldpw))) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Old password is incorrect"));
			return;
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "Password has been changed"));
		
		cust.setPassword(PasswordUtil.hexHash(newpw));
		rbean.save(cust);


	}
	
	public void save() {
		try {
			
			//logger.info("origpw=" + origPassword + " custpw=" + cust.getPassword());
			
			if( cust.getPassword() == null || cust.getPassword().isEmpty())
			{
				cust.setPassword(origPassword);
			} else {
				cust.setPassword(PasswordUtil.hexHash(cust.getPassword()));
			}

			//logger.info("newpw=" + cust.getPassword());

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
	
	public List<Customer> getAllCusts() {
		return rbean.getAll();
	}
	
	public void eraseQualityTotals() {
		rbean.eraseQualityTotals();
	}
	
	public void recalculateQualityTotals() {
		rbean.recalculateQualityTotals();
	}

	public String getOldpw() {
		return oldpw;
	}

	public void setOldpw(String oldpw) {
		this.oldpw = oldpw;
	}

	public String getNewpw() {
		return newpw;
	}

	public void setNewpw(String newpw) {
		this.newpw = newpw;
	}

}
