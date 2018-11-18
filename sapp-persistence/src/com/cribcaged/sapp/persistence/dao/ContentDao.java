package com.cribcaged.sapp.persistence.dao;

import java.util.List;

import com.cribcaged.sapp.persistence.entity.Content;
import com.cribcaged.sapp.persistence.entity.SystemUser;

public interface ContentDao {
	List<Content> getContents(SystemUser user);
}
