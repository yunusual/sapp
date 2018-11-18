package com.cribcaged.sapp.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.collections4.CollectionUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cribcaged.sapp.persistence.dto.MovieFilter;
import com.cribcaged.sapp.persistence.entity.Content;
import com.cribcaged.sapp.persistence.entity.Movie;
import com.cribcaged.sapp.persistence.entity.MovieGenre;
import com.cribcaged.sapp.persistence.entity.SystemUser;
import com.cribcaged.sapp.persistence.entity.UserComment;
import com.cribcaged.sapp.persistence.entity.UserLike;
import com.cribcaged.sapp.persistence.entity.UserRating;
import com.cribcaged.sapp.persistence.entity.enumeration.ContentType;
import com.cribcaged.sapp.persistence.entity.enumeration.GenreType;
import com.cribcaged.sapp.service.MovieService;
import com.cribcaged.sapp.service.UserService;

@ManagedBean
@ViewScoped
public class MovieBean extends ListBean<Movie> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(MovieBean.class);

	@EJB
	private MovieService movieService;
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
	private Movie selectedMovie;

	private List<SystemUser> users;
	private MovieFilter filter;
	
	private List<GenreType> selectedGenres;
	
	private String contentDescription;
	private String comment;
	private Integer rating;
	private Movie currentMovie;

	@Override
	public void initializeBean() {
		defaultLocale = new Locale("tr", "TR");
		user = userBean.getUser();
		filter = new MovieFilter();
		users = userService.getActiveUsers();
	}

	@Override
	protected void loadList() {
		elements = movieService.getMovies(filter);
	}

	@Override
	protected void sortList() {
	}

	@Override
	public boolean deleteElement(Movie e) {
		return false;
	}

	public List<Movie> completeMovieTitle(String query) {
		return movieService.completeMovieTitle(query);
	}
	
	public List<GenreType> completeGenre(String query) {
		List<GenreType> genres = new ArrayList<GenreType>();
		for (GenreType genre : GenreType.values()) {
			String genreTranslated = messageBean.getMessage("movie.genre." + genre.name()).toLowerCase(new Locale("tr", "TR"));
			if (genreTranslated.contains(query.toLowerCase(new Locale("tr", "TR")))) {
				genres.add(genre);
			}
		}
		return genres;
	}
	
	public String getGenreLabel(GenreType genre) {
		if (genre != null) {
			return messageBean.getMessage("movie.genre." + genre.name());
		}
		return "";
	}
	
	public void onMovieSelect(SelectEvent event) {
		if (selectedMovie != null && CollectionUtils.isNotEmpty(selectedMovie.getMovieGenres())) {
			selectedGenres = new ArrayList<GenreType>();
			for (MovieGenre genre : selectedMovie.getMovieGenres()) {
				selectedGenres.add(genre.getGenre());
			}
		}
	}

	public String share() {
		try {
			Content content = new Content();
			content.setSystemUser(user);
			content.setCategory(null);
			content.setType(ContentType.MOVIE);
			content.setDescription(contentDescription);
			content.setCreateTimestamp(new Date());
			content.setUserLikes(new HashSet<UserLike>());
			
			selectedMovie.setContent(content);
			Set<MovieGenre> movieGenres = new HashSet<MovieGenre>();
			for (GenreType genre : selectedGenres) {
				MovieGenre movieGenre = new MovieGenre();
				movieGenre.setMovie(selectedMovie);
				movieGenre.setGenre(genre);
				movieGenres.add(movieGenre);
			}
			selectedMovie.setMovieGenres(movieGenres);
			movieService.merge(selectedMovie);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "Movie.xhtml?faces-redirect=true";
	}
	
	public String comment(Movie movie) {
		if (movie != null) {
			UserComment newComment = new UserComment();
			newComment.setSystemUser(user);
			newComment.setContent(movie.getContent());
			newComment.setCreateTimestamp(new Date());
			newComment.setComment(comment);
			movie.getContent().getUserComments().add(newComment);
			movieService.merge(newComment);
			setComment(null);
		}
		return null;
	}

	public void rate() {
		if (currentMovie != null && rating > 0) {
			UserRating userRating = new UserRating();
			userRating.setSystemUser(user);
			userRating.setRating((double) rating);
			userRating.setCreateTimestamp(new Date());
			userRating.setContent(currentMovie.getContent());
			currentMovie.getContent().getUserRatings().add(userRating);
			movieService.merge(userRating);
			setRating(null);
		}
	}
	
	public String getWatchers(Movie movie) {
		if (CollectionUtils.isNotEmpty(movie.getContent().getUserRatings())) {
			String watchers = "";
			List<UserRating> ratingList = new ArrayList<UserRating>(movie.getContent().getUserRatings());
			DecimalFormat df = new DecimalFormat("0.00");
			for (int i = 0; i < ratingList.size(); i++) {
				UserRating userRating = ratingList.get(i);
				watchers += userRating.getSystemUser().getFirstname() + " " + userRating.getSystemUser().getLastname() + " " +
						messageBean.getMessage("movie.watcher.watched") + " " + messageBean.getMessage("movie.watcher.rating") + " "
						+ df.format(userRating.getRating()) + " / 10.00";
				if (i + 1 < ratingList.size()) {
					watchers += "<br/>";
				}
			}
			
			return watchers;
		}
		return "";
	}
	
	public String getMovieGenres(Movie movie) {
		String result = "";
		if (movie != null && CollectionUtils.isNotEmpty(movie.getMovieGenres())) {
			Iterator<MovieGenre> iterator = movie.getMovieGenres().iterator();
			while (iterator.hasNext()) {
				result += messageBean.getMessage("movie.genre." + iterator.next().getGenre().name());
				if (iterator.hasNext()) {
					result += ", ";
				}
			}
		}
		return result;
	}

	public String getRatings(Movie movie) {
		if (CollectionUtils.isNotEmpty(movie.getContent().getUserRatings())) {
			double total = 0.00d;
			for (UserRating userRating : movie.getContent().getUserRatings()) {
				total += userRating.getRating();
			}

			double averageRating = total / movie.getContent().getUserRatings().size();
			BigDecimal bd = new BigDecimal(averageRating);
			bd = bd.setScale(2, RoundingMode.HALF_EVEN);
			DecimalFormat df = new DecimalFormat("0.00");

			return messageBean.getMessage("dashboard.rate.label") + df.format(bd.doubleValue()) + " / 10.00 (" + movie.getContent().getUserRatings().size()
					+ ")";
		}
		return "";
	}

	public boolean rateEnabled(Movie movie) {
		if (CollectionUtils.isNotEmpty(movie.getContent().getUserRatings())) {
			for (UserRating userRating : movie.getContent().getUserRatings()) {
				if (userRating.getSystemUser().getId().equals(user.getId())) {
					return true;
				}
			}
		}
		return false;
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
	
	public String getRuntime(Integer runtime) {
		if (runtime != null) {
			return runtime + " " + messageBean.getMessage("movie.newContent.runtime.minute");
		}
		return "";
	}
	
	public String getImdbRating(Double imdbRating, Integer imdbVotes) {
		if (imdbRating != null && imdbVotes != null) {
			return imdbRating + " (" + imdbVotes + " " +  messageBean.getMessage("movie.newContent.runtime.minute") + ")";
		} else if (imdbRating != null) {
			return String.valueOf(imdbRating);
		}
		return "";
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

	public MovieFilter getFilter() {
		return filter;
	}

	public void setFilter(MovieFilter filter) {
		this.filter = filter;
	}

	public Movie getSelectedMovie() {
		return selectedMovie;
	}

	public void setSelectedMovie(Movie selectedMovie) {
		this.selectedMovie = selectedMovie;
	}

	public List<GenreType> getSelectedGenres() {
		return selectedGenres;
	}

	public void setSelectedGenres(List<GenreType> selectedGenres) {
		this.selectedGenres = selectedGenres;
	}

	public String getContentDescription() {
		return contentDescription;
	}

	public void setContentDescription(String contentDescription) {
		this.contentDescription = contentDescription;
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

	public Movie getCurrentMovie() {
		return currentMovie;
	}

	public void setCurrentMovie(Movie currentMovie) {
		this.currentMovie = currentMovie;
	}

}
