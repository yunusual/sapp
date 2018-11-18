package com.cribcaged.sapp.service;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractJpaService {
	
	@PersistenceContext(unitName = "sapp-persistence")
	protected EntityManager entityManager;
	
	public <E> E getReference(Class<E> entityClass, Serializable id) {
		return entityManager.getReference(entityClass, id);
	}

	public <E> E find(Class<E> entityClass, Serializable id) {
		return entityManager.find(entityClass, id);
	}

	public void persist(Object entity) {
		entityManager.persist(entity);
	}

	public <E> E merge(E entity) {
		return entityManager.merge(entity);
	}

	public void remove(Object entity) {
		entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
	}

	public void flush() {
		entityManager.flush();
	}

	public void refresh(Object entity) {
		entityManager.refresh(entity);
	}

	public <E> void evict(Class<E> cls) {
		entityManager.getEntityManagerFactory().getCache().evict(cls);
	}
	
}
