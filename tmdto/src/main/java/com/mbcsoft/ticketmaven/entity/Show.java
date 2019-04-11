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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "Show")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name="SHOWS")
public class Show extends BaseAppTable implements Serializable {

	@Column(nullable=false)
	private String name;
	@Column(nullable=false)
	private Timestamp time;
	@Column(nullable=false)
	private int price;
	private int cost;
	private String format;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn
	private Layout layout;

	@XmlTransient
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="show", cascade = REMOVE)
	private Set<Ticket> ticketsCollection;

	@XmlTransient
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="show", cascade = REMOVE)
	private Set<Reservation> reservationsCollection;

	@XmlTransient
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="show", cascade = REMOVE)
	private Set<Request> requestsCollection;

	@XmlTransient
	@EqualsAndHashCode.Exclude
	@ManyToMany(mappedBy="showsCollection", cascade = REMOVE)
	private Set<TMPackage> packagesCollection;

	private static final long serialVersionUID = 1L;

	public Show() {
		super();
	}

	public String toString()
	{
		return this.getName() + " " + sdf.format(this.getTime());
	}

	// kludge for JSF layer - for now
	static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm");
	public String getLabel()
	{
		return this.getName() + " " + sdf.format(this.getTime());
	}

}
