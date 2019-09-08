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
package com.mbcsoft.ticketmaven.ejbImpl;

import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.TMPackage;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})

public class PackageBean extends BaseEntityFacadeImpl<TMPackage>  {

	public PackageBean() {
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
