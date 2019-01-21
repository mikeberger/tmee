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
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.ejb.RequestBean;
import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.entity.Request;
import com.mbcsoft.ticketmaven.entity.Show;

@Named("requestBB")
@SessionScoped
public class RequestBB implements Serializable {


	private static final long serialVersionUID = 1L;

	private Request request;

	private List<Request> list = new ArrayList<Request>();

	@EJB private RequestBean rbean;
	@EJB private ShowBean showbean;
	@EJB private CustomerBean cbean;
	
	@Inject ShowBB showbb;


	public void get(ActionEvent evt) {
		String id = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("request_id");
		if (id == null || "".equals(id))
			return;

		try {

			request = rbean.get(Request.class, id);

			// we are going to the editor
			ShowBB.refreshSessionShowList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void delete(ActionEvent evt) {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("request_id");
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

		try {

			request = rbean.newRecord();

			request.setPaid(true); // testing

			// we are going to the editor
			ShowBB.refreshSessionShowList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void save() {
		
		Show s = showbean.get(Show.class, showbb.getSelectedShow());
		request.setShow(s);

		if (request.getCustomer() == null) {
			request.setCustomer(cbean.getCurrentCustomer());
		}

		rbean.save(request);

		refreshList();


	}


	public void refreshList() {
		try {

			// get requests for current logged in customer
			list = rbean.getRequestsForCustomer(null);

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

}
