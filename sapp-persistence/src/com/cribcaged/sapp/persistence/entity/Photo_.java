package com.cribcaged.sapp.persistence.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-13T20:11:59.195+0200")
@StaticMetamodel(Photo.class)
public class Photo_ {
	public static volatile SingularAttribute<Photo, Integer> id;
	public static volatile SingularAttribute<Photo, Content> content;
	public static volatile SingularAttribute<Photo, Date> dateTaken;
	public static volatile SingularAttribute<Photo, String> fileName;
	public static volatile SingularAttribute<Photo, Double> latitude;
	public static volatile SingularAttribute<Photo, Double> longitude;
}
