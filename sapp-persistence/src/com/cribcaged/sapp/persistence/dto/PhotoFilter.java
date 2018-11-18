package com.cribcaged.sapp.persistence.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.cribcaged.sapp.persistence.entity.enumeration.CategoryType;

public class PhotoFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<CategoryType> categories;
	private String sharedBy;
	private Date dateFrom;
	private Date dateUntil;
	private OrderBy orderBy;

	public List<CategoryType> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryType> categories) {
		this.categories = categories;
	}

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateUntil() {
		return dateUntil;
	}

	public void setDateUntil(Date dateUntil) {
		this.dateUntil = dateUntil;
	}

	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public void reset() {
		setCategories(null);
		setSharedBy(null);
		setDateFrom(null);
		setDateUntil(null);
		setOrderBy(null);
	}

	public enum OrderBy {
		DATE_CREATED_ASC("DA"), DATE_CREATED_DESC("DD"), USER_ASC("UA"), USER_DESC("UD"), CATEGORY_ASC("CA"), CATEGORY_DESC("CD");

		OrderBy(String value) {
			this.value = value;
		}

		private final String value;

		public String value() {
			return value;
		}

		public static OrderBy fromValue(String value) {
			for (OrderBy type : OrderBy.values()) {
				if (type.value().equalsIgnoreCase(value)) {
					return type;
				}
			}
			throw new IllegalArgumentException(value);
		}
	}
}
