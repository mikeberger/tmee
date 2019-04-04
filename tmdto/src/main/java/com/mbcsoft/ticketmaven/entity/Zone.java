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
	    		@UniqueConstraint(columnNames={"INSTANCE_ID", "ZONENAME"})
	    		}
	)
public class Zone extends BaseAppTable implements Serializable {

	@Column(name="ZONENAME",nullable=false)
	private String name;

	@Column(name="EXCL",nullable=false)
	private Boolean exclusive;

	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "zone")
	private Set<Seat> seatsCollection;
	
	private static final long serialVersionUID = 1L;

	public Zone() {
		super();
	}

	
}
