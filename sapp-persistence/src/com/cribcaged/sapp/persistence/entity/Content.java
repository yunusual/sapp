package com.cribcaged.sapp.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.cribcaged.sapp.persistence.entity.converter.CategoryTypeConverter;
import com.cribcaged.sapp.persistence.entity.converter.ContentTypeConverter;
import com.cribcaged.sapp.persistence.entity.enumeration.CategoryType;
import com.cribcaged.sapp.persistence.entity.enumeration.ContentType;

@Entity
@Table(name = "content")
public class Content implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private SystemUser systemUser;
	private Date createTimestamp;
	private String title;
	private String description;
	private CategoryType category;
	private ContentType type;
	private Set<UserComment> userComments;
	private Set<UserRating> userRatings;
	private Set<UserLike> userLikes;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "system_user_id", nullable = false)
	public SystemUser getSystemUser() {
		return systemUser;
	}

	public void setSystemUser(SystemUser systemUser) {
		this.systemUser = systemUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_timestamp", nullable = false)
	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(nullable = false, length = 2)
	@Convert(converter = CategoryTypeConverter.class)
	public CategoryType getCategory() {
		return category;
	}

	public void setCategory(CategoryType category) {
		this.category = category;
	}

	@Column(nullable = false, length = 2)
	@Convert(converter = ContentTypeConverter.class)
	public ContentType getType() {
		return type;
	}

	public void setType(ContentType type) {
		this.type = type;
	}

	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy("createTimestamp ASC")
	public Set<UserComment> getUserComments() {
		return userComments;
	}

	public void setUserComments(Set<UserComment> userComments) {
		this.userComments = userComments;
	}

	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<UserRating> getUserRatings() {
		return userRatings;
	}

	public void setUserRatings(Set<UserRating> userRatings) {
		this.userRatings = userRatings;
	}
	
	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<UserLike> getUserLikes() {
		return userLikes;
	}

	public void setUserLikes(Set<UserLike> userLikes) {
		this.userLikes = userLikes;
	}
}
