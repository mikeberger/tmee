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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import lombok.Data;

@Data
public class ShowDTO  implements Serializable {

	private int recordId;
	private String name;
	private Timestamp time;
	private int price;
	private int cost;
	private String layout;


	private static final long serialVersionUID = 1L;

	public ShowDTO() {
		super();
	}


	// kludge for JSF layer - for now
	static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm");
	public String toString()
	{
		return this.getName() + " " + sdf.format(this.getTime());
	}

}
