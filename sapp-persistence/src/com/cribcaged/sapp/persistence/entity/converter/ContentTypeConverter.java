package com.cribcaged.sapp.persistence.entity.converter;

import javax.persistence.AttributeConverter;

import com.cribcaged.sapp.persistence.entity.enumeration.ContentType;

public class ContentTypeConverter implements AttributeConverter<ContentType, String> {

	@Override
	public String convertToDatabaseColumn(ContentType attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.value();
	}

	@Override
	public ContentType convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return ContentType.fromValue(dbData);
	}

}

