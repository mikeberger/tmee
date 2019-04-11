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
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.primefaces.model.UploadedFile;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.ejb.LayoutBean;
import com.mbcsoft.ticketmaven.ejb.SeatBean;
import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.ejb.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Zone;

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
			XmlContainer container = (XmlContainer) u.unmarshal(uploadedFile.getInputstream());
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
