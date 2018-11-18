package com.cribcaged.sapp.persistence.dao;

import java.util.List;

import com.cribcaged.sapp.persistence.dto.MovieFilter;
import com.cribcaged.sapp.persistence.entity.Movie;

public interface MovieDao {
	List<Movie> getMovies(MovieFilter filter);
}
