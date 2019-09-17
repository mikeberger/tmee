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

/*-
 * #%L
 * tmee
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.util.AuditLogger;

@Stateless
@RolesAllowed({"tmuser", "tmadmin"})
@Interceptors({AuditLogger.class})

public class ShowBean extends BaseEntityFacadeImpl<Show>  {

	static private final Logger logger = Logger.getLogger(ShowBean.class.getName());

	public ShowBean() {
	}


	public Show newRecord() {
		return (new Show());
	}

	public List<Show> getAll() {
		return getAll("Show");
	}


	@SuppressWarnings("unchecked")
	public List<Show> getFutureShows() {

		Query query = em.createQuery("SELECT e FROM Show e WHERE e.instance = :inst and e.time > :now order by e.name");
		query.setParameter("inst", getInstance());
		query.setParameter("now", new Timestamp(new Date().getTime()));
		List<Show> l = (List<Show>) query.getResultList();
		return l;

	}


	public Show getFullShow(String id) {
		Show s = get(Show.class, id);
		logger.info("full show sizes: " + s.getRequestsCollection().size() + " " + s.getTicketsCollection().size());
		em.detach(s);
		return s;
	}

}
