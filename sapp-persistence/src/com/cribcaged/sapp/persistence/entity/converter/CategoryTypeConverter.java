package com.cribcaged.sapp.persistence.entity.converter;

import javax.persistence.AttributeConverter;

import com.cribcaged.sapp.persistence.entity.enumeration.CategoryType;

public class CategoryTypeConverter implements AttributeConverter<CategoryType, String> {

	@Override
	public String convertToDatabaseColumn(CategoryType attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.value();
	}

	@Override
	public CategoryType convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return CategoryType.fromValue(dbData);
	}

}

