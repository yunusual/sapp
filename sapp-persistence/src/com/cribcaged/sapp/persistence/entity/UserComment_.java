package com.cribcaged.sapp.persistence.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-20T04:05:50.338+0200")
@StaticMetamodel(UserComment.class)
public class UserComment_ {
	public static volatile SingularAttribute<UserComment, Integer> id;
	public static volatile SingularAttribute<UserComment, SystemUser> systemUser;
	public static volatile SingularAttribute<UserComment, Content> content;
	public static volatile SingularAttribute<UserComment, Date> createTimestamp;
	public static volatile SingularAttribute<UserComment, String> comment;
}
