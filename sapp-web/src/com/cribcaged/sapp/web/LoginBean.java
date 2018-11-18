package com.cribcaged.sapp.web;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cribcaged.sapp.persistence.entity.SystemUser;
import com.cribcaged.sapp.service.UserService;
import com.cribcaged.sapp.web.util.MessageKey;
import com.cribcaged.sapp.web.util.MessageUtil;

@ManagedBean
@SessionScoped
public class LoginBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(LoginBean.class);

	private final String ACTION_FAILED = "actionFailed";

	@EJB
	private UserService userService;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;
	@ManagedProperty(value = "#{messageBean}")
	private MessageBean messageBean;

	private Locale defaultLocale;

	private String username;
	private String password;

	@PostConstruct
	public void init() {
		defaultLocale = new Locale("tr", "TR");
	}

	public String login() {
		boolean actionFailed = true;
		try {
			SystemUser user = userService.findUser(username, password);
			if (user == null) {
				addErrorMessage(MessageUtil.getMessage(MessageKey.LOGIN_ERROR, defaultLocale));
			} else {
				getExternalContext().getSessionMap().put("user", user);
				userBean.setUser(user);
				return "Dashboard";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		getRequestContext().addCallbackParam(ACTION_FAILED, actionFailed);
		return null;
	}

	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "index?faces-redirect=true";
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public MessageBean getMessageBean() {
		return messageBean;
	}

	public void setMessageBean(MessageBean messageBean) {
		this.messageBean = messageBean;
	}
	
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}