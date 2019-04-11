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

import static javax.persistence.CascadeType.REMOVE;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "Layout")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(
	    uniqueConstraints={
	    		@UniqueConstraint(columnNames={"INSTANCE_ID", "NAME"})
	    		}
	)
public class Layout extends BaseAppTable implements Serializable {
	
	static public final String AUDITORIUM = "Auditorium";
	static public final String TABLE = "Table";
	
	@Column(nullable=false)
	private String name;
	private int centerseat;
	private int numRows;
	private int numSeats;
	//private String seating;

	@XmlTransient
	@OneToMany(mappedBy="layout")
	@EqualsAndHashCode.Exclude private Set<Show> showsCollection;

	@XmlTransient
	@OneToMany(mappedBy="layout", cascade = REMOVE)
	@EqualsAndHashCode.Exclude private Set<Seat> seatsCollection;

	@XmlTransient
	@OneToMany(mappedBy="layout", cascade = REMOVE)
	@EqualsAndHashCode.Exclude private Set<TMTable> tmtablesCollection;

	private static final long serialVersionUID = 1L;

	public Layout() {
		super();
	}

	public String toString() {
		return name;
	}
	
}
