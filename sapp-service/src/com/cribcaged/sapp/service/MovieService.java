package com.cribcaged.sapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cribcaged.sapp.persistence.dao.JpaDaoFactory;
import com.cribcaged.sapp.persistence.dao.MovieDao;
import com.cribcaged.sapp.persistence.dto.MovieFilter;
import com.cribcaged.sapp.persistence.entity.Movie;
import com.cribcaged.sapp.persistence.entity.MovieGenre;
import com.cribcaged.sapp.persistence.entity.enumeration.GenreType;

@Stateless
public class MovieService extends AbstractJpaService {

	private static Logger logger = LoggerFactory.getLogger(MovieService.class);

	private MovieDao movieDao;

	private final String API_URL = "http://www.omdbapi.com/?";

	@PostConstruct
	public void init() {
		movieDao = JpaDaoFactory.createMovieDao(entityManager);
	}

	public List<Movie> getMovies(MovieFilter filter) {
		return movieDao.getMovies(filter);
	}

	public List<Movie> completeMovieTitle(String query) {
		List<Movie> results = new ArrayList<Movie>();
		try {
			String url = API_URL + "type=movie&s=" + URLEncoder.encode(query, "UTF-8");
			JSONObject json = readJsonFromUrl(url);
			boolean found = json.getString("Response") != null ? Boolean.valueOf(json.getString("Response")) : false;
			if (found) {
				JSONArray movies = json.getJSONArray("Search");
				for (int i = 0; i < movies.length(); i++) {
					JSONObject movie = (JSONObject) movies.get(i);
					Movie movieEntity = new Movie();
					movieEntity.setTitle(movie.getString("Title"));
					movieEntity.setYear(movie.getInt("Year"));
					movieEntity.setImdbReference(movie.getString("imdbID"));
					if (!movie.getString("Poster").equals("N/A")) {
						movieEntity.setPosterUrl(movie.getString("Poster"));
					}
					results.add(movieEntity);
				}
			}
		} catch (JSONException | IOException e) {
			logger.error(e.getMessage(), e);
		}
		return results;
	}

	public Movie getMovieFromImdb(String imdbReference) {
		Movie movieEntity = null;
		try {
			String url = API_URL + "i=" + URLEncoder.encode(imdbReference, "UTF-8");
			JSONObject json = readJsonFromUrl(url);
			boolean found = json.getString("Response") != null ? Boolean.valueOf(json.getString("Response")) : false;
			if (found) {
				movieEntity = new Movie();
				movieEntity.setTitle(json.getString("Title"));
				movieEntity.setYear(json.getInt("Year"));
				if (!json.getString("Runtime").equals("N/A")) {
					movieEntity.setRuntime(Integer.parseInt(json.getString("Runtime").substring(0, json.getString("Runtime").indexOf(' '))));
				}
				movieEntity.setDirector(json.getString("Director"));
				movieEntity.setActors(json.getString("Actors"));
				movieEntity.setLanguage(json.getString("Language"));
				
				if (!json.getString("imdbRating").equals("N/A")) {
					movieEntity.setImdbRating(json.getDouble("imdbRating"));
				}
				
				if (!json.getString("imdbVotes").equals("N/A")) {
					movieEntity.setImdbVotes(Integer.parseInt(json.getString("imdbVotes").replaceAll(",", "")));
				}
				
				String[] genres = json.getString("Genre").split(",");
				Set<MovieGenre> movieGenres = new HashSet<MovieGenre>();
				for (String genre : genres) {
					MovieGenre movieGenre = new MovieGenre();
					movieGenre.setMovie(movieEntity);
					movieGenre.setGenre(GenreType.valueOf(genre.trim().replace('-', '_').toUpperCase()));
					movieGenres.add(movieGenre);
				}
				movieEntity.setMovieGenres(movieGenres);
				movieEntity.setCountry(json.getString("Country"));
				
				if (!json.getString("Plot").equals("N/A")) {
					movieEntity.setPlot(json.getString("Plot"));
				}
				
				movieEntity.setImdbReference(json.getString("imdbID"));
				
				if (!json.getString("Poster").equals("N/A")) {
					movieEntity.setPosterUrl(json.getString("Poster"));
				}
			}
		} catch (JSONException | IOException e) {
			logger.error(e.getMessage(), e);
		}
		return movieEntity;
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
}
