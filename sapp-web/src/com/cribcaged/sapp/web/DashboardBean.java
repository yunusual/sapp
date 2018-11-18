package com.cribcaged.sapp.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cribcaged.sapp.model.PhotoExifModel;
import com.cribcaged.sapp.persistence.dto.PhotoFilter;
import com.cribcaged.sapp.persistence.dto.PhotoFilter.OrderBy;
import com.cribcaged.sapp.persistence.entity.Photo;
import com.cribcaged.sapp.persistence.entity.SystemUser;
import com.cribcaged.sapp.persistence.entity.UserComment;
import com.cribcaged.sapp.persistence.entity.UserLike;
import com.cribcaged.sapp.persistence.entity.UserRating;
import com.cribcaged.sapp.persistence.entity.enumeration.CategoryType;
import com.cribcaged.sapp.service.PhotoService;
import com.cribcaged.sapp.service.UserService;

@ManagedBean
@ViewScoped
public class DashboardBean extends ListBean<Photo> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(DashboardBean.class);

	@EJB
	private PhotoService photoService;
	@EJB
	private UserService userService;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;
	@ManagedProperty(value = "#{configurationBean}")
	private ConfigurationBean configurationBean;
	@ManagedProperty(value = "#{imageBean}")
	private ImageBean imageBean;
	@ManagedProperty(value = "#{messageBean}")
	private MessageBean messageBean;

	private Locale defaultLocale;
	private SystemUser user;
	private String contentDescription;
	private CategoryType contentCategory;
	private String comment;
	private Integer rating;
	private UploadedFile selectedFile;
	private Photo currentPhoto;
	private MapModel mapModel;
	private String mapCenter;
	
	private String[] selectedCategories;
	private PhotoFilter filter;
	private List<SystemUser> users;

	@Override
	public void initializeBean() {
		defaultLocale = new Locale("tr", "TR");
		user = userBean.getUser();
		filter = new PhotoFilter();
		users = userService.getActiveUsers();
	}

	@Override
	protected void loadList() {
		elements = photoService.getPhotos(null);
	}

	@Override
	protected void sortList() {
	}

	@Override
	public boolean deleteElement(Photo e) {
		return false;
	}

	public String share() {
		try {
			if (selectedFile == null || StringUtils.isEmpty(selectedFile.getFileName())) {
				addErrorMessage(messageBean.getMessage("dashboard.newContent.fileNotSelected"));
			} else {
				String fileNameBase = String.valueOf(new Date().getTime());
				String directory = configurationBean.getConfiguration("uploadPath");
				Path file = photoService.uploadOriginalPhoto(selectedFile.getInputstream(), selectedFile.getFileName(), fileNameBase, directory);
				PhotoExifModel exifModel = photoService.getPhotoExifModel(file);
				Path rotatedFile = photoService.rotateImage(file, fileNameBase, directory);
				Path resizedFile = photoService.resizePhoto(rotatedFile, fileNameBase, directory);
				if (resizedFile != null) {
					photoService.createNewPhoto(resizedFile, user, contentDescription, contentCategory, exifModel);
					contentDescription = null;
					contentCategory = null;
					loadList();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "Dashboard.xhtml?faces-redirect=true";
	}

	public String comment(Photo photo) {
		if (photo != null) {
			UserComment newComment = new UserComment();
			newComment.setSystemUser(user);
			newComment.setContent(photo.getContent());
			newComment.setCreateTimestamp(new Date());
			newComment.setComment(comment);
			photo.getContent().getUserComments().add(newComment);
			photoService.merge(newComment);
			setComment(null);
		}
		return null;
	}

	public String like(Photo photo) {
		if (photo != null) {
			UserLike like = new UserLike();
			like.setSystemUser(user);
			like.setContent(photo.getContent());
			like.setCreateTimestamp(new Date());
			photo.getContent().getUserLikes().add(like);
			photoService.merge(like);
		}
		return null;
	}

	public void rate() {
		if (currentPhoto != null && rating > 0) {
			UserRating userRating = new UserRating();
			userRating.setSystemUser(user);
			userRating.setRating((double) rating);
			userRating.setCreateTimestamp(new Date());
			userRating.setContent(currentPhoto.getContent());
			currentPhoto.getContent().getUserRatings().add(userRating);
			photoService.merge(userRating);
			setRating(null);
		}
	}
	
	public void filter() {
		if (filter != null) {
			if (selectedCategories != null) {
				List<CategoryType> categories = new ArrayList<CategoryType>();
				for (String cat : selectedCategories) {
					categories.add(CategoryType.fromValue(cat));
				}
				filter.setCategories(categories);
			}
			elements = photoService.getPhotos(filter);
		}
	}
	
	public void reset() {
		if (filter != null) {
			filter.reset();
			elements = photoService.getPhotos(filter);
		}
	}

	public String getTimeAgo(Date date) {
		Date now = new Date();

		if (!now.before(date)) {
			if (now.getTime() - date.getTime() < 1000 * 60) {
				return TimeUnit.MILLISECONDS.toSeconds(now.getTime() - date.getTime()) + " " + messageBean.getMessage("dashboard.time.ago.seconds");
			} else if (now.getTime() - date.getTime() < 1000 * 60 * 60) {
				return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - date.getTime()) + " " + messageBean.getMessage("dashboard.time.ago.minutes");
			} else if (now.getTime() - date.getTime() < 1000 * 60 * 60 * 24) {
				return TimeUnit.MILLISECONDS.toHours(now.getTime() - date.getTime()) + " " + messageBean.getMessage("dashboard.time.ago.hours");
			} else if (now.getTime() - date.getTime() < 1000 * 60 * 60 * 24 * 365) {
				return TimeUnit.MILLISECONDS.toDays(now.getTime() - date.getTime()) + " " + messageBean.getMessage("dashboard.time.ago.days");
			}
		}

		return "";
	}

	public String getTime(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss EEEEEEEEEE", new Locale("tr", "TR"));
			return sdf.format(date);
		}
		return "";
	}

	public String getCategoryImage(CategoryType type) {
		if (type != null) {
			switch (type) {
			case EAT:
				return "images/eat.png";
			case DRINK:
				return "images/drink.png";
			case SHIT:
				return "images/shit.png";
			case WEED:
				return "images/weed.png";
			default:
				break;
			}
		}
		return "";
	}

	public String getLikes(Photo photo) {
		if (CollectionUtils.isNotEmpty(photo.getContent().getUserLikes())) {
			List<UserLike> likes = new ArrayList<UserLike>(photo.getContent().getUserLikes());
			String response = "";
			for (int i = 0; i < likes.size(); i++) {
				if (i < likes.size() - 2) {
					response += likes.get(i).getSystemUser().getFirstname() + " " + likes.get(i).getSystemUser().getLastname() + ", ";
				} else if (i == likes.size() - 2) {
					response += likes.get(i).getSystemUser().getFirstname() + " " + likes.get(i).getSystemUser().getLastname() + " ";
				} else {
					if (likes.size() == 1) {
						response += likes.get(i).getSystemUser().getFirstname() + " " + likes.get(i).getSystemUser().getLastname();
					} else {
						response += messageBean.getMessage("dashboard.like.and") + " " + likes.get(i).getSystemUser().getFirstname() + " "
								+ likes.get(i).getSystemUser().getLastname();
					}
				}
			}
			return response += " " + messageBean.getMessage("dashboard.like");
		}
		return "";
	}

	public boolean likeEnabled(Photo photo) {
		if (CollectionUtils.isNotEmpty(photo.getContent().getUserLikes())) {
			for (UserLike like : photo.getContent().getUserLikes()) {
				if (like.getSystemUser().getId().equals(user.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	public String getRatings(Photo photo) {
		if (CollectionUtils.isNotEmpty(photo.getContent().getUserRatings())) {
			double total = 0.00d;
			for (UserRating userRating : photo.getContent().getUserRatings()) {
				total += userRating.getRating();
			}

			double averageRating = total / photo.getContent().getUserRatings().size();
			BigDecimal bd = new BigDecimal(averageRating);
			bd = bd.setScale(2, RoundingMode.HALF_EVEN);
			DecimalFormat df = new DecimalFormat("0.00");

			return messageBean.getMessage("dashboard.rate.label") + df.format(bd.doubleValue()) + " / 5.00 (" + photo.getContent().getUserRatings().size()
					+ ")";
		}
		return "";
	}

	public boolean rateEnabled(Photo photo) {
		if (CollectionUtils.isNotEmpty(photo.getContent().getUserRatings())) {
			for (UserRating userRating : photo.getContent().getUserRatings()) {
				if (userRating.getSystemUser().getId().equals(user.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	public List<SelectItem> getCategories() {
		List<SelectItem> categories = new ArrayList<SelectItem>();
		SelectItem noSelect = new SelectItem(null, messageBean.getMessage("dashboard.newContent.category.select"));
		noSelect.setNoSelectionOption(true);
		categories.add(noSelect);
		for (CategoryType category : CategoryType.values()) {
			categories.add(new SelectItem(category, messageBean.getMessage("dashboard.newContent.category." + category.name())));
		}
		return categories;
	}

	public void showGeoLocation(Photo photo) {
		if (photo != null && photo.getLatitude() != null && photo.getLongitude() != null) {
			mapCenter = photo.getLatitude() + ", " + photo.getLongitude();
			mapModel = new DefaultMapModel();
			LatLng coordinate = new LatLng(photo.getLatitude(), photo.getLongitude());
			mapModel.addOverlay(new Marker(coordinate));
		}
	}
	
	public List<SelectItem> getUsers() {
		List<SelectItem> userList = new ArrayList<SelectItem>();
		if (CollectionUtils.isNotEmpty(users)) {
			for (SystemUser user : users) {
				userList.add(new SelectItem(user.getUsername(), user.getFirstname() + " " + user.getLastname()));
			}
		}
		return userList;
	}
	
	public List<SelectItem> getOrderByValues() {
		List<SelectItem> values = new ArrayList<SelectItem>();
		for (OrderBy value : OrderBy.values()) {
			values.add(new SelectItem(value, messageBean.getMessage("dashboard.filter.orderBy." + value.value())));
		}
		return values;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public ConfigurationBean getConfigurationBean() {
		return configurationBean;
	}

	public void setConfigurationBean(ConfigurationBean configurationBean) {
		this.configurationBean = configurationBean;
	}

	public ImageBean getImageBean() {
		return imageBean;
	}

	public void setImageBean(ImageBean imageBean) {
		this.imageBean = imageBean;
	}

	public MessageBean getMessageBean() {
		return messageBean;
	}

	public void setMessageBean(MessageBean messageBean) {
		this.messageBean = messageBean;
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public SystemUser getUser() {
		return user;
	}

	public void setUser(SystemUser user) {
		this.user = user;
	}

	public UploadedFile getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(UploadedFile selectedFile) {
		this.selectedFile = selectedFile;
	}

	public String getContentDescription() {
		return contentDescription;
	}

	public void setContentDescription(String contentDescription) {
		this.contentDescription = contentDescription;
	}

	public CategoryType getContentCategory() {
		return contentCategory;
	}

	public void setContentCategory(CategoryType contentCategory) {
		this.contentCategory = contentCategory;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Photo getCurrentPhoto() {
		return currentPhoto;
	}

	public void setCurrentPhoto(Photo currentPhoto) {
		this.currentPhoto = currentPhoto;
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public String getMapCenter() {
		return mapCenter;
	}

	public void setMapCenter(String mapCenter) {
		this.mapCenter = mapCenter;
	}

	public String[] getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(String[] selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public PhotoFilter getFilter() {
		return filter;
	}

	public void setFilter(PhotoFilter filter) {
		this.filter = filter;
	}

}
