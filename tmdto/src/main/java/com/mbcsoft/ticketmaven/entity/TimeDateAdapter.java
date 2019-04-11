/*******************************************************************************
 * Copyright (C) 2019 Michael Berger
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.mbcsoft.ticketmaven.entity;

import java.sql.Timestamp;
import java.util.logging.Logger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimeDateAdapter extends XmlAdapter<String, Timestamp>{

	static private final Logger logger = Logger.getLogger(TimeDateAdapter.class.getName());

    @Override
    public String marshal(Timestamp v) throws Exception {
    	logger.info("marshall-" + v );
        return Long.toString(v.getTime());
    }

    @Override
    public Timestamp unmarshal(String v) throws Exception {
    	logger.info("um" + v + ":" + Long.parseLong(v));
        return new Timestamp(Long.parseLong(v));
    }

}
