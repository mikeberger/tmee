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

import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.entity.Show;

@Named("showBB")
@SessionScoped
public class ShowBB implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Show> showList = new ArrayList<Show>();

	private String showid;

	private String selectedShow;

	private Show show;

	public Show getShow() {
		return show;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("show_id");

			show = rbean.get(Show.class, id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void delete() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("show_id");
			if (id == null || "".equals(id))
				return;

			rbean.delete(Show.class,id);

			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@EJB
	private ShowBean rbean;

	static public void refreshSessionShowList() {
		// find showBB in session
		FacesContext ctx = FacesContext.getCurrentInstance();
		ShowBB bb = ctx.getApplication().evaluateExpressionGet(ctx, "#{showBB}", ShowBB.class);
		bb.refreshList();
	}

	public void refreshList() {
		try {

			// get requests for current logged in customer
			showList = rbean.getFutureShows();

		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void setShowList(List<Show> showList) {
		this.showList = showList;
	}

	public List<Show> getShowList() {
		return showList;
	}

	public String getShowid() {
		return showid;
	}

	public void setShowid(String showid) {
		this.showid = showid;
	}

	public void newRecord() {

		try {

			show = rbean.newRecord();
			showid = Integer.toString(show.getRecordId());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getSelectedShow() {
		return selectedShow;
	}

	public void setSelectedShow(String selectedShow) {
		this.selectedShow = selectedShow;
	}

}
