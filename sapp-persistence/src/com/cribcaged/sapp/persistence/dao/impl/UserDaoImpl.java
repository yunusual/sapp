package com.cribcaged.sapp.persistence.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.cribcaged.sapp.persistence.dao.AbstractJpaDao;
import com.cribcaged.sapp.persistence.dao.UserDao;
import com.cribcaged.sapp.persistence.entity.SystemUser;
import com.cribcaged.sapp.persistence.entity.SystemUser_;

public class UserDaoImpl extends AbstractJpaDao implements UserDao {

	private static final long serialVersionUID = 1L;

	public UserDaoImpl(EntityManager em) {
		super(em);
	}

	@Override
	public List<SystemUser> findAll() {
		return super.findAll(SystemUser.class);
	}

	@Override
	public SystemUser findUser(String username, String password) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemUser> query = cb.createQuery(SystemUser.class);
		Root<SystemUser> systemUser = query.from(SystemUser.class);

		Predicate restrictions = cb.and(cb.equal(systemUser.get(SystemUser_.username), username), 
				cb.equal(systemUser.get(SystemUser_.password), password),
				cb.isTrue(systemUser.get(SystemUser_.active)));

		query.where(restrictions);

		try {
			return em.createQuery(query).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public SystemUser findUser(String username) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemUser> query = cb.createQuery(SystemUser.class);
		Root<SystemUser> systemUser = query.from(SystemUser.class);

		Predicate restrictions = cb.equal(systemUser.get(SystemUser_.username), username);

		query.where(restrictions);

		try {
			return em.createQuery(query).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public List<SystemUser> getActiveUsers() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemUser> query = cb.createQuery(SystemUser.class);
		Root<SystemUser> systemUser = query.from(SystemUser.class);
		
		Predicate restrictions = cb.isTrue(systemUser.get(SystemUser_.active));
		
		query.distinct(true);
		query.where(restrictions);

		return em.createQuery(query).getResultList();
	}
}
