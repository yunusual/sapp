package com.cribcaged.sapp.persistence.dao;

import java.util.List;

import com.cribcaged.sapp.persistence.entity.SystemUser;

public interface UserDao {
	List<SystemUser> findAll();

	SystemUser findUser(String username, String password);

	SystemUser findUser(String username);

	List<SystemUser> getActiveUsers();
}
