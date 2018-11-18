package com.cribcaged.sapp.persistence.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-13T20:11:59.203+0200")
@StaticMetamodel(UserRating.class)
public class UserRating_ {
	public static volatile SingularAttribute<UserRating, Integer> id;
	public static volatile SingularAttribute<UserRating, SystemUser> systemUser;
	public static volatile SingularAttribute<UserRating, Content> content;
	public static volatile SingularAttribute<UserRating, Date> createTimestamp;
	public static volatile SingularAttribute<UserRating, Double> rating;
}
