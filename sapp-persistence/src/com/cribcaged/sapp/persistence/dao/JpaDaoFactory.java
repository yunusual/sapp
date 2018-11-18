package com.cribcaged.sapp.persistence.dao;

import javax.persistence.EntityManager;

import com.cribcaged.sapp.persistence.dao.impl.ContentDaoImpl;
import com.cribcaged.sapp.persistence.dao.impl.MovieDaoImpl;
import com.cribcaged.sapp.persistence.dao.impl.PhotoDaoImpl;
import com.cribcaged.sapp.persistence.dao.impl.UserDaoImpl;

public class JpaDaoFactory {

	private JpaDaoFactory() {

	}

	public static UserDao createUserDao(EntityManager em) {
		return new UserDaoImpl(em);
	}
	
	public static ContentDao createContentDao(EntityManager em) {
		return new ContentDaoImpl(em);
	}
	
	public static PhotoDao createPhotoDao(EntityManager em) {
		return new PhotoDaoImpl(em);
	}

	public static MovieDao createMovieDao(EntityManager em) {
		return new MovieDaoImpl(em);
	}
}
