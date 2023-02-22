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

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
import com.mbcsoft.ticketmaven.ejbImpl.RequestBean;
import com.mbcsoft.ticketmaven.ejbImpl.ShowBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.util.Message;

@Named("requestBB")
@SessionScoped
public class RequestBB implements Serializable {

	private static final long serialVersionUID = 1L;

	private Request request;

	private List<Request> list = new ArrayList<Request>();

	@EJB
	private RequestBean rbean;
	@EJB
	private ShowBean showbean;
	@EJB
	private CustomerBean cbean;

	private String selectedShow;
	private String selectedCustomer;

	// filters
	private boolean all = false;

	public void get(ActionEvent evt) {
		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("request_id");
		if (id == null || "".equals(id))
			return;

		try {

			request = rbean.get(Request.class, id);

			// we are going to the editor
			ShowBB.refreshSessionShowList();
			CustomerBB.refreshSessionList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void delete(ActionEvent evt) {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("request_id");
			if (id == null || "".equals(id))
				return;

			Request r = rbean.get(Request.class, id);
			rbean.delete(r);

			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void newRecord(ActionEvent evt) {

		newUserRecord(evt);

		try {
			CustomerBB.refreshSessionList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void newUserRecord(ActionEvent evt) {

		try {

			request = rbean.newRecord();

			request.setPaid(false); 

			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("show_id");
			if (id == null || id.isEmpty()) {
				// we are going to the editor
				ShowBB.refreshSessionShowList();
			} else {
				Show s = showbean.get(Show.class, id);
				request.setShow(s);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void markPaid() {
		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("request_id");
			if (id == null || "".equals(id))
				return;

			Request r = rbean.get(Request.class, id);
			r.setPaid(true);
			rbean.save(r);

			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {

		if (request.getShow() == null) {
			Show s = showbean.get(Show.class, selectedShow);
			request.setShow(s);
		}

		if (selectedCustomer != null) {
			Customer c = cbean.get(Customer.class, selectedCustomer);
			request.setCustomer(c);
		}

		if (request.getCustomer() == null) {
			request.setCustomer(cbean.getCurrentCustomer());
		}

		if (request.getTickets() > request.getCustomer().getAllowedTickets()) {
			Message.validationError(
					"Only " + request.getCustomer().getAllowedTickets() + " tickets are allowed for this customer");
			throw new AbortProcessingException();
		}

		rbean.save(request);

		refreshList();

	}

	public void loadUserList() {
		all = false;
		refreshList();
	}

	public void loadAdminList() {
		all = true;
		refreshList();
	}

	public void refreshList() {
		try {

			if (all) {
				list = rbean.getAll();

			} else {
				// get requests for current logged in customer
				list = rbean.getRequestsForCustomer(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}

	public void setList(List<Request> list) {
		this.list = list;
	}

	public List<Request> getList() {
		return list;
	}

	public String getSelectedShow() {
		return selectedShow;
	}

	public void setSelectedShow(String selectedShow) {
		this.selectedShow = selectedShow;
	}

	public String getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(String selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

}
