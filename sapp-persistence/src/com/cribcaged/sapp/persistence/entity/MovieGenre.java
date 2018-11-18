package com.cribcaged.sapp.persistence.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.cribcaged.sapp.persistence.entity.converter.GenreTypeConverter;
import com.cribcaged.sapp.persistence.entity.enumeration.GenreType;

@Entity
@Table(name = "movie_genre")
public class MovieGenre implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Movie movie;
	private GenreType genre;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "movie_id", nullable = false)
	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	@Column(nullable = false, length = 2)
	@Convert(converter = GenreTypeConverter.class)
	public GenreType getGenre() {
		return genre;
	}

	public void setGenre(GenreType genre) {
		this.genre = genre;
	}
}
