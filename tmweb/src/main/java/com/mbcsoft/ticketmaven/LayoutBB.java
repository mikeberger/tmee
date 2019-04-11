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
import com.mbcsoft.ticketmaven.entity.Layout;

@Named("layoutBB")
@SessionScoped
public class LayoutBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Layout layout;
	@EJB
	private LayoutBean lbean;

	private List<Layout> list = new ArrayList<Layout>();

	static public void refreshSessionList() {
		// find showBB in session
		FacesContext ctx = FacesContext.getCurrentInstance();
		LayoutBB bb = ctx.getApplication().evaluateExpressionGet(ctx, "#{layoutBB}", LayoutBB.class);
		bb.refreshList();
	}

	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("layout_id");

			layout = lbean.get(Layout.class, id);

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
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("layout_id");
			if (id == null || "".equals(id))
				return;

			// Layout c = lbean.get(Layout.class, id);
			lbean.delete(Layout.class, id);

			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void newRecord() {

		try {

			layout = lbean.newRecord();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void save() {
		try {

			lbean.save(layout, true);
			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setList(List<Layout> list) {
		this.list = list;
	}

	public List<Layout> getList() {
		return list;
	}

	

}
