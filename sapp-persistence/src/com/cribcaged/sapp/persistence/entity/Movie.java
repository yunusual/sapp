package com.cribcaged.sapp.persistence.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "movie")
public class Movie implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Content content;
	private String title;
	private Integer year;
	private Integer runtime;
	private String director;
	private String actors;
	private String language;
	private String plot;
	private String posterUrl;
	private String country;
	private String imdbReference;
	private Double imdbRating;
	private Integer imdbVotes;
	private Set<MovieGenre> movieGenres;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id", nullable = false)
	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	@Column(nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getRuntime() {
		return runtime;
	}

	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	@Column(name = "poster_url")
	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "imdb_reference")
	public String getImdbReference() {
		return imdbReference;
	}

	public void setImdbReference(String imdbReference) {
		this.imdbReference = imdbReference;
	}

	@Column(name = "imdb_rating")
	public Double getImdbRating() {
		return imdbRating;
	}

	public void setImdbRating(Double imdbRating) {
		this.imdbRating = imdbRating;
	}

	@Column(name = "imdb_votes")
	public Integer getImdbVotes() {
		return imdbVotes;
	}

	public void setImdbVotes(Integer imdbVotes) {
		this.imdbVotes = imdbVotes;
	}

	@OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<MovieGenre> getMovieGenres() {
		return movieGenres;
	}

	public void setMovieGenres(Set<MovieGenre> movieGenres) {
		this.movieGenres = movieGenres;
	}
}
