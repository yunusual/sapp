package com.cribcaged.sapp.persistence.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;

import com.cribcaged.sapp.persistence.dao.AbstractJpaDao;
import com.cribcaged.sapp.persistence.dao.PhotoDao;
import com.cribcaged.sapp.persistence.dto.PhotoFilter;
import com.cribcaged.sapp.persistence.dto.PhotoFilter.OrderBy;
import com.cribcaged.sapp.persistence.entity.Content;
import com.cribcaged.sapp.persistence.entity.Content_;
import com.cribcaged.sapp.persistence.entity.Photo;
import com.cribcaged.sapp.persistence.entity.Photo_;
import com.cribcaged.sapp.persistence.entity.SystemUser_;

public class PhotoDaoImpl extends AbstractJpaDao implements PhotoDao {

	private static final long serialVersionUID = 1L;

	public PhotoDaoImpl(EntityManager em) {
		super(em);
	}

	@Override
	public List<Photo> getPhotos(PhotoFilter filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Photo> query = cb.createQuery(Photo.class);
		Root<Photo> photo = query.from(Photo.class);
		Fetch<Photo, Content> content = photo.fetch(Photo_.content);
		content.fetch(Content_.userComments, JoinType.LEFT);
		content.fetch(Content_.userRatings, JoinType.LEFT);
		content.fetch(Content_.userLikes, JoinType.LEFT);

		Predicate restrictions = cb.isTrue(photo.get(Photo_.content).get(Content_.systemUser).get(SystemUser_.active));

		if (filter != null) {
			restrictions = applyFilterRestrictions(filter, photo, restrictions, cb);
		}

		query.distinct(true);
		query.where(restrictions);
		if (filter != null && filter.getOrderBy() != null) {
			applyOrderBy(query, cb, photo, filter.getOrderBy());
		} else {
			query.orderBy(cb.desc(photo.get(Photo_.content).get(Content_.createTimestamp)));
		}

		return em.createQuery(query).getResultList();
	}

	private Predicate applyFilterRestrictions(PhotoFilter filter, Root<Photo> photo, Predicate restrictions, CriteriaBuilder cb) {
		Predicate predicate = restrictions;
		if (filter.getSharedBy() != null) {
			predicate = cb.and(predicate, cb.equal(photo.get(Photo_.content).get(Content_.systemUser).get(SystemUser_.username), filter.getSharedBy()));
		}

		if (filter.getDateFrom() != null) {
			predicate = cb.and(predicate, cb.greaterThanOrEqualTo(photo.get(Photo_.content).get(Content_.createTimestamp), filter.getDateFrom()));
		}

		if (filter.getDateUntil() != null) {
			predicate = cb.and(predicate, cb.lessThanOrEqualTo(photo.get(Photo_.content).get(Content_.createTimestamp), filter.getDateUntil()));
		}

		if (CollectionUtils.isNotEmpty(filter.getCategories())) {
			predicate = cb.and(predicate, photo.get(Photo_.content).get(Content_.category).in(filter.getCategories()));
		}
		return predicate;
	}

	private void applyOrderBy(CriteriaQuery<Photo> query, CriteriaBuilder cb, Root<Photo> photo, OrderBy orderBy) {
		switch (orderBy) {
		case DATE_CREATED_ASC:
			query.orderBy(cb.asc(photo.get(Photo_.content).get(Content_.createTimestamp)));
			break;
		case DATE_CREATED_DESC:
			query.orderBy(cb.desc(photo.get(Photo_.content).get(Content_.createTimestamp)));
			break;
		case USER_ASC:
			query.orderBy(cb.asc(photo.get(Photo_.content).get(Content_.systemUser).get(SystemUser_.username)));
			break;
		case USER_DESC:
			query.orderBy(cb.desc(photo.get(Photo_.content).get(Content_.systemUser).get(SystemUser_.username)));
			break;
		case CATEGORY_ASC:
			query.orderBy(cb.asc(photo.get(Photo_.content).get(Content_.category)));
			break;
		case CATEGORY_DESC:
			query.orderBy(cb.desc(photo.get(Photo_.content).get(Content_.category)));
			break;
		}
	}

}
