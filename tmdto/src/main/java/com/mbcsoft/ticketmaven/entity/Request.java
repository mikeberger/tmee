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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class Request extends BaseAppTable implements Serializable {
	

	@Column(nullable=false)
	private int tickets;

	private double discount;
	
	@ManyToOne
	@JoinColumn
	private Customer customer;

	@ManyToOne
	@JoinColumn
	private Show show;
	
	private boolean paid;
	
	@Transient
	private int lotteryPosition;

	private static final long serialVersionUID = 1L;

	public Request() {
		super();
		paid = false;
	}

	
	// convenience for legacy reasons
	public String getCustomerName()
	{
		return this.customer.getFirstName() + " " + this.customer.getLastName();
	}
	
	
	
	public String getSpecialNeeds()
	{
		return this.customer.getSpecialNeeds();
	}


	

}
