/*******************************************************************************
 * Copyright (C) 2019 Mike Berger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.mbcsoft.ticketmaven.ejb;


import java.util.List;

import javax.ejb.Local;

import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;

@Local
public interface TicketBean extends BaseEntityFacade<Ticket>  {

	public List<Ticket> getTicketsForShow(Show s);
	
	public List<Ticket> getTicketsForCustomer(Customer c);

	public void deleteTicketsForShow(Show show);
	
	

}
