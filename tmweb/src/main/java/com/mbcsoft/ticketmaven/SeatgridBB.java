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
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejb.LayoutBean;
import com.mbcsoft.ticketmaven.ejb.SeatBean;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Seat;

@Named("seatgridBB")
@SessionScoped
public class SeatgridBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Layout layout;
	@EJB
	private LayoutBean lbean;
	@EJB
	private SeatBean sbean;

	private List<Seat> list = new ArrayList<Seat>();

	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("layout_id");

			layout = lbean.get(Layout.class, id);
			list = sbean.getSeats(layout);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void refreshList() {
		try {

			setList(sbean.getSeats(layout));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		// TODO

	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setList(List<Seat> list) {
		this.list = list;
	}

	public List<Seat> getList() {
		return list;
	}

}
