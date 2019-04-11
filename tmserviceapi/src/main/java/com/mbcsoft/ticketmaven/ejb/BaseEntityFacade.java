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

import com.mbcsoft.ticketmaven.entity.BaseAppTable;
import com.mbcsoft.ticketmaven.entity.Customer;

public interface BaseEntityFacade<T extends BaseAppTable> {
	
	public T newRecord();
    
    public T save(T c);
    
    public void delete(T c);
    public void delete(Class<? extends T> c, String id);
    
    public List<T> getAll();
    
    public T get(Class<? extends T> c, String id);
    
	public Customer getCurrentCustomer();
	
    
}
