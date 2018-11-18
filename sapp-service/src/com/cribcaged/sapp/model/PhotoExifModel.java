package com.cribcaged.sapp.model;

import java.io.Serializable;
import java.util.Date;

public class PhotoExifModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double latitude;
	private Double longitude;
	private Date dateTaken;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Date getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}
}
