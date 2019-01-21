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

import java.net.HttpURLConnection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.entity.Customer;

@Stateless
@Path("/api")
@Produces({ "application/json;charset=UTF-8" })
@RolesAllowed({"tmuser", "tmadmin"})
public class RestServlet {

	@EJB
	private CustomerBean cb;

	@GET
	@Path("/customer/{id}")
	public Object getBalanceBank(@PathParam("id") final String id) {

		Customer c = cb.getCustomer(id);
		if (c != null)
			return c.toString();

		throw new WebApplicationException(HttpURLConnection.HTTP_NOT_FOUND);

	}

	@GET
	@Path("/customers")
	public Object getCusts() {

		List<Customer> l = cb.getAll();
		if (l != null) {
			JSONObject ret = new JSONObject();
			JSONArray list = new JSONArray();
			for (Customer c : l) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("firstname", c.getFirstName());
				jsonObj.put("lastname", c.getLastName());
				jsonObj.put("recordid", c.getRecordId() );
				jsonObj.put("userid", c.getUserid() );
				list.put(jsonObj);
			}
			ret.append("customers", list);

			return ret.toString();
		}

		throw new WebApplicationException(HttpURLConnection.HTTP_NOT_FOUND);

	}

	@GET
	@Path("/ping")
	public Object ping() {

		return "{\"resp\":\"I am Here!\"}";

	}

}
