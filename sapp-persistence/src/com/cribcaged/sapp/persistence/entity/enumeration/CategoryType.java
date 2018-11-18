package com.cribcaged.sapp.persistence.entity.enumeration;


public enum CategoryType {
	EAT("E"), 
	DRINK("D"), 
	SHIT("S"),
	WEED("W");
	
	CategoryType (String value) {
		this.value = value;
	}
	
	private final String value;
	
	public String value() {
		return value;
	}
	
	public static CategoryType fromValue (String value) {
		for (CategoryType type : CategoryType.values()) {
			if (type.value().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException(value);
	}
}
