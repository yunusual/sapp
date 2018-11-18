package com.cribcaged.sapp.persistence.entity;

import com.cribcaged.sapp.persistence.entity.enumeration.GenreType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-13T20:11:59.191+0200")
@StaticMetamodel(MovieGenre.class)
public class MovieGenre_ {
	public static volatile SingularAttribute<MovieGenre, Integer> id;
	public static volatile SingularAttribute<MovieGenre, Movie> movie;
	public static volatile SingularAttribute<MovieGenre, GenreType> genre;
}
