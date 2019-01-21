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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class Reservation extends BaseAppTable implements Serializable {

	@Basic(optional=false)
	@Column(nullable=false)
	private int num;

	@ManyToOne(optional=false)
	@JoinColumn
	private Customer customer;

	@ManyToOne(optional=false)
	@JoinColumn
	private Show show;

	@ManyToOne(optional=false)
	@JoinColumn
	private TMTable table;

	private boolean paid;

	private String amount;

	private String notes;

	private static final long serialVersionUID = 1L;

	public Reservation() {
		super();
		paid = false;
	}



	// convenience for legacy reasons
	public String getCustomerName()
	{
		return this.customer.getFirstName() + " " + this.customer.getLastName();
	}




}
