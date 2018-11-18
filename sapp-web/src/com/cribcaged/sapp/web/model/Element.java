package com.cribcaged.sapp.web.model;

import java.io.Serializable;

public class  Element<E> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected E model;

	public Element(E model) {
		this.model = model;
	}

	public E getModel() {
		return model;
	}

	public void setModel(E model) {
		this.model = model;
	}
}
