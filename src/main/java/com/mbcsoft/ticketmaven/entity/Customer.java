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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "INSTANCE_ID", "FIRSTNAME", "LASTNAME" }) 
		})
@NamedQuery(name = "findCustomerByUserid", query = "SELECT c FROM Customer c WHERE c.userid = :id")
public class Customer extends BaseAppTable implements Serializable {

	static final public String AISLE = "Aisle";
	static final public String FRONT = "Front";
	static final public String FRONT_ONLY = "Front Row Only";
	static final public String REAR = "Rear";
	static final public String NONE = "None";

	@Column(name="USERID", unique = true, nullable = false)
	private String userid;

	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="roles")
	private String roles;

	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	private String email;
	private String phone;
	private String notes;
	@Column(nullable = false)
	private int allowedTickets;
	private String specialNeeds;
	private int totalTickets;
	private int totalQuality;
	private String address;
	private String resident;

	@XmlTransient
	@OneToMany(mappedBy = "customer", cascade = REMOVE)
	@EqualsAndHashCode.Exclude
	private Set<Ticket> ticketsCollection;
	
	@XmlTransient
	@OneToMany(mappedBy = "customer", cascade = REMOVE)
	@EqualsAndHashCode.Exclude
	private Set<Request> requestsCollection;

	private static final long serialVersionUID = 1L;

	public Customer() {
		super();
	}
	
	public double getAverageQuality()
	{
		if( totalTickets == 0) return 0.0;
		return (double) totalQuality/(double) totalTickets;
	}
	
	public String toString()
	{
		return firstName + " " + lastName;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

}
