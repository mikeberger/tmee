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
package com.mbcsoft.ticketmaven.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.mbcsoft.ticketmaven.ejbImpl.CustomerBean;
import com.mbcsoft.ticketmaven.entity.Instance;

@SessionScoped
@Named("login")
public class Login implements Serializable {

	private static final long serialVersionUID = -5876465825563098570L;
	static private final Logger logger = Logger.getLogger("audit_logger");
	
	@EJB private CustomerBean custbean;

	private String username;
	private String password;
	private String community;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getCommunity() {
		return this.community;
	}

	public void login() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			logger.info("Trying login: " + this.username);
			request.login(this.username, this.password);
			logger.info("login success");
			logger.info("is tmadmin: " + request.isUserInRole("tmadmin"));
			logger.info("is tmuser: " + request.isUserInRole("tmuser"));
			
			Instance instance = custbean.getCurrentCustomer().getInstance();
			
			if( !instance.isEnabled())
			{
				logger.log(Level.WARNING, "Login failed. Community " + instance.getName() + " is not enabled");
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "","Login failed. Community " + instance.getName() + " is not enabled"));
				request.logout();
				return;
			}
			
			community = custbean.getCurrentCustomer().getInstance().getName();
			logger.info("community: " + community);

			context.getExternalContext().getSessionMap().put("user", this.username);
			context.getExternalContext().redirect("index.xhtml");

		} catch (ServletException e) {
			logger.log(Level.INFO, "login failed", e.getMessage());
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "","Login failed."));

		} catch (IOException e2) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "","redirect failed."));
		}

	}

	public void logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			request.logout();
			context.getExternalContext().getSessionMap().remove("user");
			context.getExternalContext().redirect("login.xhtml");

		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","Logout failed."));

		} catch (IOException e2) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","redirect failed."));
		}
	}
}
