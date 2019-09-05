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

import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
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
