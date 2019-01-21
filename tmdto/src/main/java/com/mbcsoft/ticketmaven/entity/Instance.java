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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Instance implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int recordId;
	
	@Column(unique = true, nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean enabled;



}