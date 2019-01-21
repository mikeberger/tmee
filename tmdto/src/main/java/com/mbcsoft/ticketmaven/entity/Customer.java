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

import static javax.persistence.CascadeType.REMOVE;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = { "ticketsCollection", "reservationsCollection", "requestsCollection" })
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

	@Column(unique = true, nullable = false)
	private String userid;

	private String password;

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

	@OneToMany(mappedBy = "customer", cascade = REMOVE)
	@EqualsAndHashCode.Exclude
	private Set<Ticket> ticketsCollection;

	@OneToMany(mappedBy = "customer", cascade = REMOVE)
	@EqualsAndHashCode.Exclude
	private Set<Reservation> reservationsCollection;

	@OneToMany(mappedBy = "customer", cascade = REMOVE)
	@EqualsAndHashCode.Exclude
	private Set<Request> requestsCollection;

	private static final long serialVersionUID = 1L;

	public Customer() {
		super();
	}

}