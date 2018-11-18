package com.cribcaged.sapp.persistence.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-20T04:44:19.507+0200")
@StaticMetamodel(UserLike.class)
public class UserLike_ {
	public static volatile SingularAttribute<UserLike, Integer> id;
	public static volatile SingularAttribute<UserLike, SystemUser> systemUser;
	public static volatile SingularAttribute<UserLike, Content> content;
	public static volatile SingularAttribute<UserLike, Date> createTimestamp;
}
