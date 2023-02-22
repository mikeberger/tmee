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

/*-
 * #%L
 * tmee
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static jakarta.persistence.CascadeType.REMOVE;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

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
	

	@Column(nullable=false)
	private String name;
	private int centerseat;
	private int numRows;
	private int numSeats;

	@XmlTransient
	@OneToMany(mappedBy="layout")
	@EqualsAndHashCode.Exclude private Set<Show> showsCollection;

	@XmlTransient
	@OneToMany(mappedBy="layout", cascade = REMOVE)
	@EqualsAndHashCode.Exclude private Set<Seat> seatsCollection;

	private static final long serialVersionUID = 1L;

	public Layout() {
		super();
	}

	public String toString() {
		return name;
	}
	
}
