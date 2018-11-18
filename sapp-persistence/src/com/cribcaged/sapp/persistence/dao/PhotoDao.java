package com.cribcaged.sapp.persistence.dao;

import java.util.List;

import com.cribcaged.sapp.persistence.dto.PhotoFilter;
import com.cribcaged.sapp.persistence.entity.Photo;

public interface PhotoDao {
	List<Photo> getPhotos(PhotoFilter filter);
}
