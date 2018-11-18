package com.cribcaged.sapp.persistence.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.cribcaged.sapp.persistence.dao.AbstractJpaDao;
import com.cribcaged.sapp.persistence.dao.ContentDao;
import com.cribcaged.sapp.persistence.entity.Content;
import com.cribcaged.sapp.persistence.entity.Content_;
import com.cribcaged.sapp.persistence.entity.SystemUser;

public class ContentDaoImpl extends AbstractJpaDao implements ContentDao {

	private static final long serialVersionUID = 1L;

	public ContentDaoImpl(EntityManager em) {
		super(em);
	}

	@Override
	public List<Content> getContents(SystemUser user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Content> query = cb.createQuery(Content.class);
		Root<Content> content = query.from(Content.class);
		
		Predicate restrictions = cb.and(cb.notEqual(content.get(Content_.systemUser), user));
		query.where(restrictions);
		query.orderBy(cb.desc(content.get(Content_.createTimestamp)));
		
		return em.createQuery(query).getResultList();
	}

}
