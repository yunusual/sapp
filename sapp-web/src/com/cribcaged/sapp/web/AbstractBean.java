package com.cribcaged.sapp.web;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.facelets.FaceletContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

public abstract class AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}

	public RequestContext getRequestContext() {
		return RequestContext.getCurrentInstance();
	}

	public FaceletContext getFaceletContext() {
		return (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get("javax.faces.FACELET_CONTEXT");
	}

	public HttpSession getSession(boolean create) {
		return (HttpSession) getExternalContext().getSession(create);
	}

	protected ServletContext getServletContext() {
		return (ServletContext) getExternalContext().getContext();
	}

	public HttpServletRequest getServletRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	public HttpServletResponse getServletResponse() {
		return (HttpServletResponse) getExternalContext().getResponse();
	}

	public Map<String, String> getParameterMap() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getExternalContext().getRequestParameterMap();
	}

	public Object getAttribute(ActionEvent event, String name) {
		if (event == null) {
			return null;
		}
		return event.getComponent().getAttributes().get(name);
	}

	public Object getManagedBean(String name) {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		if (externalContext != null) {
			HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
			if (req != null) {
				return req.getSession().getAttribute(name);
			}
		}
		return null;
	}

	public void reset() {
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = context.getViewRoot();
		reset(viewRoot.getChildren());
	}

	private void reset(List<UIComponent> components) {
		for (UIComponent component : components) {
			if (component instanceof UIInput) {
				UIInput input = (UIInput) component;
				input.resetValue();
			}
			reset(component.getChildren());
		}
	}

	protected void addErrorMessage(String string) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, string, string));
	}

	protected void addSuccessfullySavedMessage() {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved.", "Successfully saved."));
	}

	protected void addSuccessfullyDeletedMessage(int count) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully deleted " + count + " records.", "Successfully deleted " + count + " records."));
	}
	
	protected void addInformationMessage(String string) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, string, string));
	}
}
