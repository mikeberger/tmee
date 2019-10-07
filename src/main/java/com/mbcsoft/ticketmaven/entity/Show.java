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

import static javax.persistence.CascadeType.REMOVE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "Show")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name="SHOWS")
public class Show extends BaseAppTable implements Serializable {

	@Column(nullable=false)
	private String name;
	private String description;
	private String image = "defaultShowImage.jpeg";
	
	@XmlJavaTypeAdapter(TimeDateAdapter.class)
	@Column(nullable=false)
	private Timestamp time;
	@Column(nullable=false)
	private int price;
	private int cost;
	private String format;
	
	@Transient
	private String layoutName; // for import/export only

	@XmlTransient
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn
	private Layout layout;

	@XmlTransient
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="show", cascade = REMOVE)
	private Set<Ticket> ticketsCollection;

	@XmlTransient
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy="show", cascade = REMOVE)
	private Set<Request> requestsCollection;

	@XmlTransient
	@EqualsAndHashCode.Exclude
	@ManyToMany(mappedBy="showsCollection", cascade = REMOVE)
	private Set<TMPackage> packagesCollection;

	private static final long serialVersionUID = 1L;

	public Show() {
		super();
	}

	public String toString()
	{
		return this.getName() + " " + sdf.format(this.getTime());
	}

	// kludge for JSF layer - for now
	static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm");
	public String getLabel()
	{
		return this.getName() + " " + sdf.format(this.getTime());
	}

	

}
