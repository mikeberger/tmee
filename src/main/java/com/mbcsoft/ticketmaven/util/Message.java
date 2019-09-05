package com.mbcsoft.ticketmaven.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class Message {

	static public void validationError(String msg) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, message);
	}

}
