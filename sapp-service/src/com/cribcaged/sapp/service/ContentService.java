package com.cribcaged.sapp.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import com.cribcaged.sapp.persistence.dao.ContentDao;
import com.cribcaged.sapp.persistence.dao.JpaDaoFactory;
import com.cribcaged.sapp.persistence.entity.Content;
import com.cribcaged.sapp.persistence.entity.SystemUser;

@Stateless
public class ContentService extends AbstractJpaService {

	private ContentDao contentDao;

	@PostConstruct
	public void init() {
		contentDao = JpaDaoFactory.createContentDao(entityManager);
	}
	
	public List<Content> getContents(SystemUser user) {
		return contentDao.getContents(user);
	}
}
