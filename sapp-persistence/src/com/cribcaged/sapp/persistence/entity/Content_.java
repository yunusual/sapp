package com.cribcaged.sapp.persistence.entity;

import com.cribcaged.sapp.persistence.entity.enumeration.CategoryType;
import com.cribcaged.sapp.persistence.entity.enumeration.ContentType;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-20T04:44:19.490+0200")
@StaticMetamodel(Content.class)
public class Content_ {
	public static volatile SingularAttribute<Content, Integer> id;
	public static volatile SingularAttribute<Content, SystemUser> systemUser;
	public static volatile SingularAttribute<Content, Date> createTimestamp;
	public static volatile SingularAttribute<Content, CategoryType> category;
	public static volatile SingularAttribute<Content, ContentType> type;
	public static volatile SetAttribute<Content, UserComment> userComments;
	public static volatile SetAttribute<Content, UserRating> userRatings;
	public static volatile SingularAttribute<Content, String> title;
	public static volatile SingularAttribute<Content, String> description;
	public static volatile SetAttribute<Content, UserLike> userLikes;
}
