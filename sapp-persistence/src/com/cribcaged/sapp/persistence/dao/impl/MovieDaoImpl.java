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
import com.cribcaged.sapp.persistence.dao.MovieDao;
import com.cribcaged.sapp.persistence.dto.MovieFilter;
import com.cribcaged.sapp.persistence.dto.MovieFilter.OrderBy;
import com.cribcaged.sapp.persistence.entity.Content;
import com.cribcaged.sapp.persistence.entity.Content_;
import com.cribcaged.sapp.persistence.entity.Movie;
import com.cribcaged.sapp.persistence.entity.Movie_;
import com.cribcaged.sapp.persistence.entity.SystemUser_;

public class MovieDaoImpl extends AbstractJpaDao implements MovieDao {

	private static final long serialVersionUID = 1L;

	public MovieDaoImpl(EntityManager em) {
		super(em);
	}

	@Override
	public List<Movie> getMovies(MovieFilter filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Movie> query = cb.createQuery(Movie.class);
		Root<Movie> movie = query.from(Movie.class);
		Fetch<Movie, Content> content = movie.fetch(Movie_.content);
		content.fetch(Content_.userComments, JoinType.LEFT);
		content.fetch(Content_.userRatings, JoinType.LEFT);
		content.fetch(Content_.userLikes, JoinType.LEFT);
		movie.fetch(Movie_.movieGenres, JoinType.LEFT);

		Predicate restrictions = cb.isTrue(movie.get(Movie_.content).get(Content_.systemUser).get(SystemUser_.active));

		if (filter != null) {
			restrictions = applyFilterRestrictions(filter, movie, restrictions, cb);
		}

		query.distinct(true);
		query.where(restrictions);
		if (filter != null && filter.getOrderBy() != null) {
			applyOrderBy(query, cb, movie, filter.getOrderBy());
		} else {
			query.orderBy(cb.desc(movie.get(Movie_.content).get(Content_.createTimestamp)));
		}

		return em.createQuery(query).getResultList();
	}

	private Predicate applyFilterRestrictions(MovieFilter filter, Root<Movie> movie, Predicate restrictions, CriteriaBuilder cb) {
		Predicate predicate = restrictions;
		if (filter.getSharedBy() != null) {
			predicate = cb.and(predicate, cb.equal(movie.get(Movie_.content).get(Content_.systemUser).get(SystemUser_.username), filter.getSharedBy()));
		}

		if (filter.getDateFrom() != null) {
			predicate = cb.and(predicate, cb.greaterThanOrEqualTo(movie.get(Movie_.content).get(Content_.createTimestamp), filter.getDateFrom()));
		}

		if (filter.getDateUntil() != null) {
			predicate = cb.and(predicate, cb.lessThanOrEqualTo(movie.get(Movie_.content).get(Content_.createTimestamp), filter.getDateUntil()));
		}

		if (CollectionUtils.isNotEmpty(filter.getGenres())) {
			predicate = cb.and(predicate, movie.get(Movie_.movieGenres).in(filter.getGenres()));
		}
		return predicate;
	}

	private void applyOrderBy(CriteriaQuery<Movie> query, CriteriaBuilder cb, Root<Movie> movie, OrderBy orderBy) {
		switch (orderBy) {
		case DATE_CREATED_ASC:
			query.orderBy(cb.asc(movie.get(Movie_.content).get(Content_.createTimestamp)));
			break;
		case DATE_CREATED_DESC:
			query.orderBy(cb.desc(movie.get(Movie_.content).get(Content_.createTimestamp)));
			break;
		case USER_ASC:
			query.orderBy(cb.asc(movie.get(Movie_.content).get(Content_.systemUser).get(SystemUser_.username)));
			break;
		case USER_DESC:
			query.orderBy(cb.desc(movie.get(Movie_.content).get(Content_.systemUser).get(SystemUser_.username)));
			break;
		case CATEGORY_ASC:
			query.orderBy(cb.asc(movie.get(Movie_.content).get(Content_.category)));
			break;
		case CATEGORY_DESC:
			query.orderBy(cb.desc(movie.get(Movie_.content).get(Content_.category)));
			break;
		}
	}

}
