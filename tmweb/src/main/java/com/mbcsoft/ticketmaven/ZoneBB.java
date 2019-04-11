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
