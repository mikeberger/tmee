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
package com.mbcsoft.ticketmaven.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class TMTable extends BaseAppTable implements Serializable {
	
	public static final String RECT_TABLE = "Rect";
	public static final String CIRC_TABLE = "Circ";
	public static final String RECT_FEAT = "RFeat";
	public static final String CIRC_FEAT = "CFeat";

	
	@Column(nullable=false)
	private int seats;

	private int xpos;

	private int ypos;

	private int width;

	private int height;

	private String label;

	private String tbltype;

	@ManyToOne(optional=false)
	@JoinColumn
	private Layout layout;

	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="table")
	private Set<Reservation> reservationsCollection;
	
	private static final long serialVersionUID = 1L;

	public TMTable() {
		super();
	}

	
	
}
