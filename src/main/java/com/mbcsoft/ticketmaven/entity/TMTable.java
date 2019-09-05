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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class TMTable extends BaseAppTable implements Serializable {
	
	public static final String RECT_TABLE = "Rect";
	public static final String CIRC_TABLE = "Circ";
	public static final String RECT_FEAT = "RFeat";
	public static final String CIRC_FEAT = "CFeat";

	
	@Column(nullable=false)
	private int seats;

	private int xpos;

	private int ypos;

	private int width;

	private int height;

	private String label;

	private String tbltype;

	@ManyToOne(optional=false)
	@JoinColumn
	private Layout layout;

	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="table")
	private Set<Reservation> reservationsCollection;
	
	private static final long serialVersionUID = 1L;

	public TMTable() {
		super();
	}

	
	
}
