package com.cribcaged.sapp.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import com.cribcaged.sapp.persistence.dao.JpaDaoFactory;
import com.cribcaged.sapp.persistence.dao.UserDao;
import com.cribcaged.sapp.persistence.entity.SystemUser;

@Stateless
public class UserService extends AbstractJpaService {

	private UserDao userDao;

	@PostConstruct
	public void init() {
		userDao = JpaDaoFactory.createUserDao(entityManager);
	}

	public List<SystemUser> findAll() {
		return userDao.findAll();
	}
	
	public SystemUser findUser(String username, String password) {
		return userDao.findUser(username, password);
	}
	
	public SystemUser findUser(String username) {
		return userDao.findUser(username);
	}

	public List<SystemUser> getActiveUsers() {
		return userDao.getActiveUsers();
	}
}
