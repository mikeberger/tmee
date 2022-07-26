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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.StreamingOutput;

import com.mbcsoft.ticketmaven.ejbImpl.ShowBean;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.ticketprint.TicketPrinter;


@Stateless
@Path("/ticket")
@Produces({ "application/pdf" })
@RolesAllowed({ "tmsite", "tmadmin" })
public class TicketRestApi  {

	@EJB private ShowBean rbean;
	static private final Logger logger = Logger.getLogger(TicketRestApi.class.getName());

	@GET
	@Path("/{showid}")
	public StreamingOutput report(@PathParam("showid") String id) {
		return new StreamingOutput() {
			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				try {
					logger.info("Ticket Print show = " + id);
					Show s = rbean.getFullShow(id);
					if( s == null ) return;
					int instance = rbean.getCurrentCustomer().getInstance().getRecordId();
					if( s.getInstance().getRecordId() != instance) {
						return;
					}

					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					new TicketPrinter().printShow(s, byteArrayOutputStream);
					outputStream.write(byteArrayOutputStream.toByteArray());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}



}
