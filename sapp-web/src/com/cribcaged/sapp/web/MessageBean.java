package com.cribcaged.sapp.web;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@ApplicationScoped
public class MessageBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LoggerFactory.getLogger(MessageBean.class);

	private ResourceBundle bundle;

	@PostConstruct
	public void init() {
		loadProperties();
	}

	private void loadProperties() {
		bundle = ResourceBundle.getBundle("sapp_web", new Locale("tr", "TR"));
	}
	
	public String getMessage(String key) {
		return getMessage(key, null);
	}
	
	public String getMessage(String key, Object... args) {
		try {
			if (bundle == null) {
				loadProperties();
			}
			
			String message = bundle.getString(key);
			if (message == null) {
				return "???" + key + "???";
			}
			
			if (args != null) {
				message = MessageFormat.format(message, args);
			}
			return message;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "???" + key + "???";
	}
}
