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
package com.mbcsoft.ticketmaven.ejbImpl;

import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.ejb.TableBean;
import com.mbcsoft.ticketmaven.entity.Layout;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.TMPackage;
import com.mbcsoft.ticketmaven.entity.TMTable;

@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})

public class TableBeanImpl extends BaseEntityFacadeImpl<TMTable> implements TableBean  {

	public TableBeanImpl() {
	}

	
	public TMTable newRecord() {
		return (new TMTable());
	}
	

	public List<TMTable> getAll() {
		return getAll("TMTable");
	}
	
	public double calcDiscount(TMPackage p)
	{

		try{
			
			Collection<Show> c = p.getShowsCollection();
			
			int total = 0;
			for( Show sh : c )
			{	
					total += sh.getPrice();		
			}
			
			if( p.getPrice() > total )
			{
				return 0;
			}
			double percent = (((double)total - (double)p.getPrice()) * 100.0) / (double)total;
			return percent;
		}
		catch( Exception e)
		{
			return 0;
		}
	}


	@SuppressWarnings("unchecked")
	public List<TMTable> getTables(Layout l) {
		Query query = em.createQuery("SELECT e FROM TMTable e WHERE e.layout = :layout");
		query.setParameter("layout", l);
		return (List<TMTable>) query.getResultList();
	}
}
