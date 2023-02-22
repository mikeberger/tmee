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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.StreamingOutput;

import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Servlet implementation class ReportServlet
 */
@Stateless
@Path("/report")
@Produces({ "application/pdf" })
@RolesAllowed({ "tmsite", "tmadmin" })
public class ReportRestApi {
	private static final long serialVersionUID = 1L;

	@EJB private CustomerBean rbean;

	private void runReport(OutputStream outputStream, String id, String show_id ){
		JasperPrint jasperPrint = null;
		try {

			int instance = rbean.getCurrentCustomer().getInstance().getRecordId();
			String reportname = "na";
			HashMap<String, Object> parms = new HashMap<String, Object>();

			if( "cust".equals(id) )
			{
				reportname = "customer";
				parms.put("instance", Integer.valueOf(instance));
			}
			else if( "show".equals(id) )
			{
				reportname = "seatsForShowByName";
				parms.put("show_id", Integer.valueOf(show_id));
				parms.put("title", "Title TBD");
			}

			InputStream is = ReportRestApi.class.getResourceAsStream("/" + reportname + ".jasper");

			String targetFileName = reportname + ".pdf";
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is);
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/tm/tmdb");

			jasperPrint = JasperFillManager.fillReport(jasperReport, parms, ds.getConnection());

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
			outputStream.write(byteArrayOutputStream.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("/{id}")
	public StreamingOutput report(@PathParam("id") String id) {
		return new StreamingOutput() {
			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				runReport(outputStream,id,null);
			}
		};
	}

	@GET
	@Path("/{id}/{showid}")
	public StreamingOutput reportWithShowId(@PathParam("id") String id, @PathParam("showid") String show_id) {
		return new StreamingOutput() {
			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				runReport(outputStream,id,show_id);
			}
		};
	}




}
