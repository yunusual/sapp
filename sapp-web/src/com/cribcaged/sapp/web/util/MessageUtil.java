package com.cribcaged.sapp.web.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageUtil {
	
	private final static String baseName = "sapp_web";
	private final static Locale defaultLocale = new Locale("tr", "TR");
	
	public static String getMessage(String key) {
		return getMessage(key, defaultLocale);
	}
	
	public static String getMessage(String key, Locale locale) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
			String message = bundle.getString(key);
			return message != null ? message : "";			
		} catch (MissingResourceException e) {
			return "???" + key + "???";
		}
	}
	
	public static String getMessage(String key, Locale locale, Object... args) {
		return MessageFormat.format(getMessage(key, locale), args);
	}
	
}
