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

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


import com.github.javafaker.Faker;
import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
import com.mbcsoft.ticketmaven.ejbImpl.InstanceBean;
import com.mbcsoft.ticketmaven.ejbImpl.LayoutBean;
import com.mbcsoft.ticketmaven.ejbImpl.LotteryManager;
import com.mbcsoft.ticketmaven.ejbImpl.RequestBean;
import com.mbcsoft.ticketmaven.ejbImpl.SeatBean;
import com.mbcsoft.ticketmaven.ejbImpl.ShowBean;
import com.mbcsoft.ticketmaven.ejbImpl.ZoneBean;
import com.mbcsoft.ticketmaven.entity.*;
import com.mbcsoft.ticketmaven.util.PasswordUtil;

@Stateless
@Path("/admin")
@Produces({ "application/json;charset=UTF-8" })
@RolesAllowed({ "tmsite", "tmadmin" })
public class AdminRestApi {

	static private final Logger logger = Logger.getLogger(AdminRestApi.class.getName());

	@EJB
	private CustomerBean cb;

	@EJB
	private InstanceBean ib;
	@EJB
	private ZoneBean zoneb;
	@EJB
	private SeatBean seatb;
	@EJB
	private ShowBean showb;
	@EJB
	private LayoutBean layoutb;
	@EJB
	private RequestBean requestb;
	@EJB
	private LotteryManager lotteryb;

	@GET
	@Path("/export")
	@Produces("application/zip")
	public StreamingOutput export() {

		return new StreamingOutput() {
			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				try {
					Instance inst = cb.getCurrentCustomer().getInstance();
					logger.info("Export for community = " + inst.getName());

					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ZipOutputStream out = new ZipOutputStream(byteArrayOutputStream);
					Writer fw = new OutputStreamWriter(out, "UTF8");

					out.putNextEntry(new ZipEntry("export.xml"));
					JAXBContext jc = JAXBContext.newInstance(XmlContainer.class);
					Marshaller marshaller = jc.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					XmlContainer cc = new XmlContainer();
					cc.instance = inst;
					cc.customer = cb.getAll();
					cc.show = showb.getAll();
					cc.layout = layoutb.getAll();
					cc.zone = zoneb.getAll();
					cc.seat = seatb.getAll();

					for (Show s : cc.show) {
						s.setLayoutName(s.getLayout().getName());
					}

					for (Seat s : cc.seat) {
						s.setLayoutName(s.getLayout().getName());
						if (s.getZone() != null)
							s.setZoneName(s.getZone().getName());
					}

					marshaller.marshal(cc, out);
					fw.flush();
					out.closeEntry();
					out.close();

					outputStream.write(byteArrayOutputStream.toByteArray());


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	}

/*
	@GET
	@Path("/customer/{id}")
	public Object getCustomer(@PathParam("id") final String id) {

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
				jsonObj.put("recordid", c.getRecordId());
				jsonObj.put("userid", c.getUserid());
				list.put(jsonObj);
			}
			ret.append("customers", list);

			return ret.toString();
		}

		throw new WebApplicationException(HttpURLConnection.HTTP_NOT_FOUND);

	}

	*/

	@GET
	@Path("/genTestInstance/{instance}")
	@RolesAllowed({ "tmsite" })
	public Object genTestInstance(@PathParam("instance") String is) {

		// create instance admin account first
		Instance inst = new Instance();
		inst.setEnabled(true);
		inst.setName(is);
		inst.putOption("opt1", "value1");
		inst.putOption("opt2", "value2");
		inst = ib.save(inst);
				
		System.out.println(inst.getOptions());

		Customer admin = new Customer();
		admin.setFirstName(is);
		admin.setLastName("Admin");
		admin.setUserid(is);
		admin.setPassword(PasswordUtil.hexHash(is));
		admin.setRoles("tmadmin");
		admin.setInstance(inst);
		admin.setResident("Y");
		admin.setSpecialNeeds(Customer.NONE);

		cb.saveAdmin(admin);

		return "Test Instance Generated";
	}

	@GET
	@Path("/genTestData")
	@RolesAllowed({ "tmadmin" })
	public Object genTestData() {

		createInstanceData();

		return "Test Data Generated";
	}

	private void createInstanceData() {


		ArrayList<Zone> zonelist = new ArrayList<Zone>();

		for (String zone : new String[] { "Mobility Impaired", "Hearing Impaired", "Lavatory", "Vision Impaired",
				"Front Row", "Lip Reader" }) {
			Zone bzone = new Zone();
			bzone.setExclusive(false);
			bzone.setName(zone);
			bzone = zoneb.save(bzone);
			zonelist.add(bzone);
		}

		Zone bzone = new Zone();
		bzone.setExclusive(true);
		bzone.setName("Wheel Chair");
		bzone = zoneb.save(bzone);
		zonelist.add(bzone);

		Layout l = new Layout();
		l.setName("Full Auditorium");
		l.setNumRows(15);
		l.setNumSeats(32);
		l = layoutb.save(l,true);

		Show show1 = null;
		Show show2 = null;
		for (int i = 1; i <= 10; i++) {
			Show s = new Show();
			Faker faker = new Faker();
			s.setName(faker.funnyName().name());
			s.setCost(i * 1000);
			s.setLayout(l);
			s.setDescription("This is a really great show!!! A must see where stuff happens and there is this long line of description and more ******************************************************************************************");
			s.setPrice(i * 100);
			s.setTime(new Timestamp(faker.date().future(100, 7, TimeUnit.DAYS).getTime()));
			s = showb.save(s);
			if (i == 1)
				show1 = s;
			else if (i == 2)
				show2 = s;
		}

		loadCusts( zonelist);
		createRequests( show1, true);
		createRequests( show2, false);

		lotteryb.runLottery(show1);

	}

	private void createRequests( Show show, boolean paid) {

		for (Customer c : cb.getAll()) {
			Request r = new Request();
			r.setCustomer(c);
			r.setShow(show);
			r.setTickets(2);
			r.setPaid(paid);
			requestb.save(r);

		}
	}

	private void loadCusts( ArrayList<Zone> zonelist) {

		for (int i = 1; i <= 50; i++) {
			Faker faker = new Faker();

			Customer c = new Customer();
			c.setFirstName(faker.name().firstName());
			c.setLastName(faker.name().lastName());
			c.setResident("Y");
			c.setUserid( "cust" + i);
			c.setPassword(PasswordUtil.hexHash(c.getUserid()));
			c.setRoles("tmuser");
			c.setSpecialNeeds(Customer.NONE);
			c.setPhone(faker.phoneNumber().phoneNumber());
			c.setAddress(faker.address().fullAddress());
			c.setAllowedTickets(2);

			if (i <= 5)
				c.setSpecialNeeds(zonelist.get(i % zonelist.size()).getName());

			try {
				cb.save(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	
	

}
