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
