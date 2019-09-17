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
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.LayoutBean;
import com.mbcsoft.ticketmaven.ejbImpl.SeatBean;
import com.mbcsoft.ticketmaven.ejbImpl.ZoneBean;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Zone;

@Named("seatgridBB")
@SessionScoped
public class SeatgridBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Layout layout;
	@EJB
	private LayoutBean lbean;
	@EJB
	private SeatBean sbean;
	@EJB
	private ZoneBean zbean;

	private String editParam = "availability";

	private List<String> values = new ArrayList<String>();

	private List<Seat> list = new ArrayList<Seat>();

	private String selectedValue;

	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("layout_id");

			layout = lbean.get(Layout.class, id);
			list = sbean.getSeats(layout);
			setValues();
			setEditLabels();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setValues() {
		values.clear();
		if ("availability".equals(editParam)) {
			values.add("Available");
			values.add("Unavailable");
		} else if ("aisle".equals(editParam)) {
			values.add("None");
			values.add("Left");
			values.add("Right");
		} else if ("quality".equals(editParam)) {
			for (int i = 1; i <= 30; i++) {
				values.add(Integer.toString(i));
			}
		} else if ("zone".equals(editParam)) {
			values.add("None");
			for (Zone z : zbean.getAll()) {
				values.add(z.getName());
			}
		}
	}

	public void refreshList() {
		try {

			setList(sbean.getSeats(layout));
			setValues();
			setEditLabels();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setEditLabels() {
		for (Seat s : list) {

			s.setSelected(false);

			String l = s.getRow() + s.getSeat();
			if ("availability".equals(editParam))
				l += "/" + (s.getAvailable() ? "Y" : "N");
			else if ("quality".equals(editParam))
				l += "/" + s.getWeight();
			else if ("zone".equals(editParam)) {
				if (s.getZone() != null)
					l += "/" + s.getZone().getName();
			} else if ("aisle".equals(editParam) && !s.getEnd().equals("None"))
				l += "/" + s.getEnd().charAt(0);
			s.setEditLabel(l);
		}
	}

	public void save() {
		for (Seat s : list) {
			if (s.isSelected()) {
				if ("availability".equals(editParam)) {
					s.setAvailable(selectedValue.equals("Available"));
				} else if ("quality".equals(editParam)) {
					s.setWeight(Integer.parseInt(selectedValue));
				} else if ("zone".equals(editParam)) {
					s.setZone(zbean.get(selectedValue));
				} else if ("aisle".equals(editParam)) {
					s.setEnd(selectedValue);
				}

				sbean.save(s);
			}
		}
		refreshList();

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

	public String getEditParam() {
		return editParam;
	}

	public void setEditParam(String editParam) {
		this.editParam = editParam;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

}
