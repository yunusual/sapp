package com.cribcaged.sapp.persistence.entity.enumeration;


public enum GenreType {
	ACTION("AC"),
	ADVENTURE("AD"),
	ANIMATION("AN"),
	BIOGRAPHY("BI"),
	COMEDY("CO"),
	CRIME("CR"),
	DOCUMENTARY("DO"),
	DRAMA("DR"),
	FAMILY("FM"),
	FANTASY("FN"),
	FILM_NOIR("FN"),
	HISTORY("HI"),
	HORROR("HO"),
	MUSIC("MU"),
	MUSICAL("ML"),
	MYSTERY("MY"),
	ROMANCE("RO"),
	SCI_FI("SF"),
	SPORT("SP"),
	THRILLER("TH"),
	WAR("WA"),
	WESTERN("WE");
	
	GenreType (String value) {
		this.value = value;
	}
	
	private final String value;
	
	public String value() {
		return value;
	}
	
	public static GenreType fromValue (String value) {
		for (GenreType type : GenreType.values()) {
			if (type.value().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException(value);
	}
}
