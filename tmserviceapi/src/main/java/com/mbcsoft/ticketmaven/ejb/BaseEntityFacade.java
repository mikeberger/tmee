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
