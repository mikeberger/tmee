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

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.entity.Zone;

@Stateless
@RolesAllowed({"tmuser", "tmadmin", "tmsite"})

public class ZoneBean extends BaseEntityFacadeImpl<Zone> {

	public ZoneBean() {
	}

	public Zone get(String name) {
		Query query = em.createQuery("SELECT e FROM Zone e WHERE e.name = :name and e.instance = :inst");
		query.setParameter("name", name);
		query.setParameter("inst", getInstance());

		try {
			Zone l = (Zone) (query.getSingleResult());
			return l;
		}
		catch(NoResultException e)
		{
			return null;
		}
	}
	
	public Zone newRecord() {
		return (new Zone());
	}
	
	public List<Zone> getAll() {
		return getAll("Zone");
	}
}
