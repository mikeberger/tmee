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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name="package")
public class TMPackage extends BaseAppTable implements Serializable {
	
	@Column(nullable=false)
	private String name;

	@Column(nullable=false)
	private int price;

	@EqualsAndHashCode.Exclude
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="PKGSHOWS",
		joinColumns=@JoinColumn(name="PKG_ID"),
		inverseJoinColumns=@JoinColumn(name="SHOW_ID"))
	private Set<Show> showsCollection;
	

	private static final long serialVersionUID = 1L;

	public TMPackage() {
		super();
	}


	
}
