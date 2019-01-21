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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(
	    uniqueConstraints={
	    		@UniqueConstraint(columnNames={"INSTANCE_ID", "ROWCOL", "SEAT", "LAYOUT_RECORDID"})
	    		}
	)
public class Seat extends BaseAppTable implements Serializable {

	static public final String NONE = "None";
	static public final String LEFT = "Left";
	static public final String RIGHT = "Right";
	static public final String FRONT = "Front";

	// max number fo weight values
	static public final int MAX_WEIGHT = 30;

	public static final  String rowletters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	@Column(name="rowcol", nullable=false)
	private String row;

	@Column(nullable=false)
	private int seat;

	@Column(nullable=false)
	private int weight;

	@Column(name="ENDCOL")
	private String end;

	private String available;

	@ManyToOne(optional=false)
	private Layout layout;

	@ManyToOne
	@JoinColumn
	private Zone zone;

	private String label;

	@OneToMany(mappedBy="seat")
	@EqualsAndHashCode.Exclude
	private Set<Ticket> ticketsCollection;

	private static final long serialVersionUID = 1L;

	public Seat() {
		super();
	}


}
