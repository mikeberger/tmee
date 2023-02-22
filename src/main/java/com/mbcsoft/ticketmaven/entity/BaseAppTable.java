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

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "Instance")
@XmlAccessorType(XmlAccessType.FIELD)
@MappedSuperclass
public abstract class BaseAppTable implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@XmlTransient
	private int recordId;
	
	@Version
	@XmlTransient
	private int version;
	
	@XmlTransient
	@ManyToOne(optional=false)
	@JoinColumn(name="instance_id")
	private Instance instance;
	
	@XmlTransient
	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public BaseAppTable() {
		super();
	}
	
	public int getRecordId() {
		return this.recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof BaseAppTable)
		{
			BaseAppTable ba = (BaseAppTable) obj;
			if( ba.recordId == this.recordId)
				return true;
		}
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.recordId;
	}
	
	public String toString() {
		return "["  + recordId + "]";
	}
	
	
   
}
