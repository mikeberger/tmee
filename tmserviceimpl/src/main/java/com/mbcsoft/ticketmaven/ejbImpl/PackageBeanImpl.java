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

import com.mbcsoft.ticketmaven.ejb.PackageBean;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.TMPackage;

@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})

public class PackageBeanImpl extends BaseEntityFacadeImpl<TMPackage> implements PackageBean  {

	public PackageBeanImpl() {
	}

	
	public TMPackage newRecord() {
		return (new TMPackage());
	}
	

	public List<TMPackage> getAll() {
		return getAll("TMPackage");
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
}
