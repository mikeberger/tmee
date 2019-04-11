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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "Seat")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(
	    uniqueConstraints={
	    		@UniqueConstraint(columnNames={"INSTANCE_ID", "ROWCOL", "SEAT", "LAYOUT_RECORDID"})
	    		}
	)
public class Seat extends BaseAppTable implements Serializable {

	static public final String NONE = "None";
	static public final String LEFT = "Left";
	static public final String RIGHT = "Right";
	static public final String FRONT = "Front";

	// max number for weight values
	static public final int MAX_WEIGHT = 30;

	public static final  String rowletters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	@Column(name="rowcol", nullable=false)
	private String row;

	@Column(nullable=false)
	private int seat;

	@Column(nullable=false)
	private int weight;

	@Column(name="ENDCOL")
	private String end;

	private Boolean available;
	
	@Transient
	private String layoutName; // for import/export only
	
	@XmlTransient
	@ManyToOne(optional=false)
	private Layout layout;

	@Transient
	private String zoneName; // for import/export only
	
	@XmlTransient
	@ManyToOne
	@JoinColumn
	private Zone zone;

	private String label;

	@XmlTransient
	@OneToMany(mappedBy="seat")
	@EqualsAndHashCode.Exclude
	private Set<Ticket> ticketsCollection;

	private static final long serialVersionUID = 1L;

	public Seat() {
		super();
	}


}
