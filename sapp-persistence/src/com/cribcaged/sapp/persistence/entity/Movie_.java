package com.cribcaged.sapp.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-13T20:11:59.188+0200")
@StaticMetamodel(Movie.class)
public class Movie_ {
	public static volatile SingularAttribute<Movie, Integer> id;
	public static volatile SingularAttribute<Movie, Content> content;
	public static volatile SingularAttribute<Movie, String> title;
	public static volatile SingularAttribute<Movie, String> posterUrl;
	public static volatile SingularAttribute<Movie, String> imdbReference;
	public static volatile SingularAttribute<Movie, String> imdbRating;
	public static volatile SingularAttribute<Movie, Integer> imdbVotes;
	public static volatile SetAttribute<Movie, MovieGenre> movieGenres;
	public static volatile SingularAttribute<Movie, Integer> year;
	public static volatile SingularAttribute<Movie, Integer> runtime;
	public static volatile SingularAttribute<Movie, String> director;
	public static volatile SingularAttribute<Movie, String> actors;
	public static volatile SingularAttribute<Movie, String> language;
	public static volatile SingularAttribute<Movie, String> plot;
	public static volatile SingularAttribute<Movie, String> country;
}
