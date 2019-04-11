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

import static javax.persistence.CascadeType.REMOVE;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "Layout")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(
	    uniqueConstraints={
	    		@UniqueConstraint(columnNames={"INSTANCE_ID", "NAME"})
	    		}
	)
public class Layout extends BaseAppTable implements Serializable {
	
	static public final String AUDITORIUM = "Auditorium";
	static public final String TABLE = "Table";
	
	@Column(nullable=false)
	private String name;
	private int centerseat;
	private int numRows;
	private int numSeats;
	//private String seating;

	@XmlTransient
	@OneToMany(mappedBy="layout")
	@EqualsAndHashCode.Exclude private Set<Show> showsCollection;

	@XmlTransient
	@OneToMany(mappedBy="layout", cascade = REMOVE)
	@EqualsAndHashCode.Exclude private Set<Seat> seatsCollection;

	@XmlTransient
	@OneToMany(mappedBy="layout", cascade = REMOVE)
	@EqualsAndHashCode.Exclude private Set<TMTable> tmtablesCollection;

	private static final long serialVersionUID = 1L;

	public Layout() {
		super();
	}

	public String toString() {
		return name;
	}
	
}
