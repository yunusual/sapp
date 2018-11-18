package com.cribcaged.sapp.persistence.entity.converter;

import javax.persistence.AttributeConverter;

import com.cribcaged.sapp.persistence.entity.enumeration.GenreType;

public class GenreTypeConverter implements AttributeConverter<GenreType, String> {

	@Override
	public String convertToDatabaseColumn(GenreType attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.value();
	}

	@Override
	public GenreType convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}	
		return GenreType.fromValue(dbData);
	}

}

