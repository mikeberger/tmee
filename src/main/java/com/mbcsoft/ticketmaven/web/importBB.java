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
import java.util.logging.Logger;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;


import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
import com.mbcsoft.ticketmaven.ejbImpl.LayoutBean;
import com.mbcsoft.ticketmaven.ejbImpl.SeatBean;
import com.mbcsoft.ticketmaven.ejbImpl.ShowBean;
import com.mbcsoft.ticketmaven.ejbImpl.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Zone;
import org.primefaces.model.file.UploadedFile;

@Named("importBB")
@RequestScoped
public class importBB implements Serializable {

	private static final long serialVersionUID = 1L;
	static private final Logger logger = Logger.getLogger(importBB.class.getName());


	@EJB
	private CustomerBean cbean;
	@EJB
	private ShowBean sbean;
	@EJB
	private LayoutBean lbean;
	@EJB
	private ZoneBean zbean;
	@EJB
	private SeatBean seatbean;
	
	private UploadedFile uploadedFile;

	public void upload() {
		try {
			JAXBContext jc = JAXBContext.newInstance(XmlContainer.class);
			Unmarshaller u = jc.createUnmarshaller();
			XmlContainer container = (XmlContainer) u.unmarshal(uploadedFile.getInputStream());
			Instance inst = cbean.getCurrentCustomer().getInstance();
			
			for( Customer c : container.customer)
			{
				c.setInstance(inst);
				cbean.save(c);
			}
			
			for( Layout l : container.layout)
			{
				l.setInstance(inst);
				lbean.save(l, false);
			}
			
			for( Zone z : container.zone)
			{
				z.setInstance(inst);
				zbean.save(z);
			}
			
			for( Show s : container.show)
			{
				s.setInstance(inst);
				s.setLayout(lbean.get(s.getLayoutName()));
				logger.info("ts:" + s.getTime());
				sbean.save(s);
			}
			
			for( Seat s : container.seat)
			{
				s.setInstance(inst);
				s.setLayout(lbean.get(s.getLayoutName()));
				s.setZone(zbean.get(s.getZoneName()));
				seatbean.save(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

}
