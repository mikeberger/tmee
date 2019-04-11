/*******************************************************************************
 * Copyright (C) 2019 Michael Berger
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
