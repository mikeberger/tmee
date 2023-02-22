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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.Data;
import org.primefaces.shaded.json.JSONObject;

@XmlRootElement(name = "Instance")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Data
public class Instance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@XmlTransient
	private int recordId;

	@Column(unique = true, nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean enabled;

	private String options;

	@Transient
	private JSONObject optionRoot;

	public String getOption(String name, String def) {
		loadOptions();
		String p = (String) optionRoot.get(name);
		if (p == null)
			return def;
		return p;
	}

	public int getIntOption(String name, int def) {
		loadOptions();
		String v = (String) optionRoot.get(name);
		if (v == null) {
			return def;
		}
		return Integer.parseInt(v);
	}

	public void putOption(String name, String val) {
		if( optionRoot == null)
			loadOptions();
		optionRoot.put(name, val);
		options = optionRoot.toString();
	}

	public void loadOptions() {
		if (optionRoot == null) {
			if (options == null || options.isEmpty())
				optionRoot = new JSONObject();
			else
				optionRoot = new JSONObject(options);
		}

	}

}
