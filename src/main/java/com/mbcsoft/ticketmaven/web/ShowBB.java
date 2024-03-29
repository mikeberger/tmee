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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.LayoutBean;
import com.mbcsoft.ticketmaven.ejbImpl.ShowBean;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Show;

@Named("showBB")
@SessionScoped
public class ShowBB implements Serializable {

	private static final long serialVersionUID = 1L;

	static public void refreshSessionShowList() {
		// find showBB in session
		FacesContext ctx = FacesContext.getCurrentInstance();
		ShowBB bb = ctx.getApplication().evaluateExpressionGet(ctx, "#{showBB}", ShowBB.class);
		bb.refreshList();
	}

	@EJB
	private ShowBean rbean;
	@EJB
	private LayoutBean lbean;

	private String selectedShow;

	private Show show;
	
	private Date inputDate;

	private String showid;

	private List<Show> showList = new ArrayList<Show>();
	
	private String layoutName;

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
	
	public void edit() {
		get();
		FacesContext ctx = FacesContext.getCurrentInstance();
		LotteryBB bb = ctx.getApplication().evaluateExpressionGet(ctx, "#{lotteryBB}", LotteryBB.class);
		bb.load();

	}
	
	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("show_id");

			show = rbean.get(Show.class, id);
			inputDate = new Date(show.getTime().getTime());
			layoutName = show.getLayout().getName();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getSelectedShow() {
		return selectedShow;
	}

	public Show getShow() {
		return show;
	}

	public String getShowid() {
		return showid;
	}

	public List<Show> getShowList() {
		return showList;
	}

	public void newRecord() {

		try {

			show = rbean.newRecord();
			showid = Integer.toString(show.getRecordId());
			inputDate = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void save() {
		try {

			show.setTime(new Timestamp(inputDate.getTime()));
			show.setLayout(lbean.get(layoutName));
			rbean.save(show);
			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void refreshList() {
		try {

			// get requests for current logged in customer
			showList = rbean.getAll();

		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void setSelectedShow(String selectedShow) {
		this.selectedShow = selectedShow;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public void setShowid(String showid) {
		this.showid = showid;
	}

	public void setShowList(List<Show> showList) {
		this.showList = showList;
	}

	public List<Show> getAllShows() {
		return rbean.getAll();
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}
	
	public List<Layout> getAllLayouts(){
		return lbean.getAll();
	}

	public String getLayoutName() {
		return layoutName;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}
	
	public void loadOpenShows() {
		try {

			// get requests for current logged in customer
			showList = rbean.getFutureShows();

		} catch (Exception e) {
			e.getMessage();
		}
	}
}
