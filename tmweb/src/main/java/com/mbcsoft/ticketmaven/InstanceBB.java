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

import com.mbcsoft.ticketmaven.ejb.InstanceBean;
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
