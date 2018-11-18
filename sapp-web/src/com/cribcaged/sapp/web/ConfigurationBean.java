package com.cribcaged.sapp.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@ApplicationScoped
public class ConfigurationBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(ConfigurationBean.class);

	private Properties properties;

	@PostConstruct
	public void init() {
		loadProperties();
	}

	private void loadProperties() {
		try {
			properties = new Properties();
			InputStream propInput = getClass().getClassLoader().getResourceAsStream("sapp_config.properties");
			properties.load(propInput);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getConfiguration(String key) {
		if (properties == null) {
			loadProperties();
		}
		
		if (properties != null) {
			return properties.getProperty(key);
		}
		return null;
	}
}
