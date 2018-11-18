package com.cribcaged.sapp.persistence.entity.enumeration;


public enum ContentType {
	PHOTO("P"), 
	MOVIE("M");
	
	ContentType (String value) {
		this.value = value;
	}
	
	private final String value;
	
	public String value() {
		return value;
	}
	
	public static ContentType fromValue (String value) {
		for (ContentType type : ContentType.values()) {
			if (type.value().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException(value);
	}
}
