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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.mbcsoft.ticketmaven.ejbImpl.InstanceBean;
import com.mbcsoft.ticketmaven.entity.Instance;

@Named("instBB")
@SessionScoped
public class InstanceBB implements Serializable {

	private static final long serialVersionUID = 1L;
	private Instance inst;
	@EJB
	private InstanceBean rbean;

	private List<Instance> list = new ArrayList<Instance>();

	public void get() {

		try {
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("inst_id");
			inst = rbean.getById(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void refreshList() {
		try {

			setList(rbean.getAllInstances());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {

		try {
			
			rbean.delete(inst);
			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void newRecord() {

		try {

			inst = rbean.newRecord();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void save() {
		try {

			rbean.save(inst);
			refreshList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setInstance(Instance inst) {
		this.inst = inst;
	}

	public Instance getInstance() {
		return inst;
	}

	public void setList(List<Instance> list) {
		this.list = list;
	}

	public List<Instance> getList() {
		return list;
	}



}
