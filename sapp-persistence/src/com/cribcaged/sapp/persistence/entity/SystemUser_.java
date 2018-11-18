package com.cribcaged.sapp.persistence.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-20T14:56:34.489+0200")
@StaticMetamodel(SystemUser.class)
public class SystemUser_ {
	public static volatile SingularAttribute<SystemUser, Integer> id;
	public static volatile SingularAttribute<SystemUser, String> username;
	public static volatile SingularAttribute<SystemUser, String> password;
	public static volatile SingularAttribute<SystemUser, String> email;
	public static volatile SingularAttribute<SystemUser, String> firstname;
	public static volatile SingularAttribute<SystemUser, String> lastname;
	public static volatile SingularAttribute<SystemUser, Date> createTimestamp;
	public static volatile SetAttribute<SystemUser, UserComment> userComments;
	public static volatile SetAttribute<SystemUser, UserRating> userRatings;
	public static volatile SetAttribute<SystemUser, Content> contents;
	public static volatile SingularAttribute<SystemUser, String> profilePhoto;
	public static volatile SingularAttribute<SystemUser, Boolean> active;
}
