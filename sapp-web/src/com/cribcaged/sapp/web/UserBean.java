package com.cribcaged.sapp.web;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.cribcaged.sapp.persistence.entity.SystemUser;
import com.cribcaged.sapp.service.UserService;

@ManagedBean
@SessionScoped
public class UserBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private UserService userService;

	private Locale defaultLocale;
	private SystemUser user;

	@PostConstruct
    public void init() {
		defaultLocale = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
    }
	
	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/Login.xhtml?faces-redirect=true";
	}
	
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public SystemUser getUser() {
		return user;
	}

	public void setUser(SystemUser user) {
		this.user = user;
	}
	
	public String getUsername() {
		return user != null ? user.getUsername() : null;
	}
}
