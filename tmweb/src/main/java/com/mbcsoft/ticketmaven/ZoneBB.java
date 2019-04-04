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

import com.mbcsoft.ticketmaven.ejb.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Zone;

@Named("zoneBB")
@SessionScoped
public class ZoneBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Zone zone;
	@EJB
	private ZoneBean lbean;

	private List<Zone> list = new ArrayList<Zone>();

	static public void refreshSessionList() {
		// find showBB in session
		FacesContext ctx = FacesContext.getCurrentInstance();
		ZoneBB bb = ctx.getApplication().evaluateExpressionGet(ctx, "#{zoneBB}", ZoneBB.class);
		bb.refreshList();
	}

	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("zone_id");

			zone = lbean.get(Zone.class, id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void refreshList() {
		try {

			setList(lbean.getAll());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("zone_id");
			if (id == null || "".equals(id))
				return;

			// Zone c = lbean.get(Zone.class, id);
			lbean.delete(Zone.class, id);

			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void newRecord() {

		try {

			zone = lbean.newRecord();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void save() {
		try {

			lbean.save(zone);
			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Zone getZone() {
		return zone;
	}

	public void setList(List<Zone> list) {
		this.list = list;
	}

	public List<Zone> getList() {
		return list;
	}

	

}
