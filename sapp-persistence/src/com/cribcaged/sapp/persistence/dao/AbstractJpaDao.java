package com.cribcaged.sapp.persistence.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public abstract class AbstractJpaDao implements Serializable {

	private static final long serialVersionUID = 1L;

	protected EntityManager em;

	public AbstractJpaDao(EntityManager em) {
		this.em = em;
	}

	public <T> List<T> findAll(Class<T> entityClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(entityClass);
		query.from(entityClass);

		return em.createQuery(query).getResultList();
	}

	public <T> void delete(Class<T> entityClass, int primaryKey) {
		em.createQuery("DELETE FROM " + entityClass.getSimpleName() + " WHERE id = :id").setParameter("id", primaryKey).executeUpdate();
	}
}
