package com.mbcsoft.ticketmaven;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Zone;

@XmlRootElement(name = "DBExport")
@SuppressWarnings("unused") class XmlContainer {
	public Instance instance;
	public Collection<Customer> customer;
	public Collection<Layout> layout;
	public Collection<Show> show;
	public Collection<Zone> zone;
	public Collection<Seat> seat;
}