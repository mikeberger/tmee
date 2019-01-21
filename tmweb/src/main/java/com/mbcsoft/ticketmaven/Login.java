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
package com.mbcsoft.ticketmaven;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@SessionScoped
@Named("login")
public class Login implements Serializable {

	private static final long serialVersionUID = -5876465825563098570L;
	private String username;
	private String password;

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

	public void login() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			// System.out.println(username + ":" + password );
			request.login(this.username, this.password);
			context.getExternalContext().getSessionMap().put("user", this.username);
			context.getExternalContext().redirect("index.xhtml");

		} catch (ServletException e) {
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
