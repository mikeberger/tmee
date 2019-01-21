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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(
	    uniqueConstraints={
	    		@UniqueConstraint(columnNames={"INSTANCE_ID", "SEAT_RECORDID", "SHOW_RECORDID"})
	    		}
	)
public class Ticket extends BaseAppTable implements Serializable {

	private int ticketPrice;

	@ManyToOne(optional=false)
	private Customer customer;

	@ManyToOne(optional=false)
	private Seat seat;

	@ManyToOne(optional=false)
	private Show show;


	private static final long serialVersionUID = 1L;

	public Ticket() {
		super();
	}


	// convenience for legacy reasons
	public String getCustomerName()
	{
		return this.customer.getFirstName() + " " + this.customer.getLastName();
	}

	public String getRowAisle() {
		return seat.getRow() + "/" + seat.getSeat();
	}


}
