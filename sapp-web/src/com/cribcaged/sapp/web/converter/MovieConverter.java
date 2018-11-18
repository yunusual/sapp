package com.cribcaged.sapp.web.converter;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.cribcaged.sapp.persistence.entity.Movie;
import com.cribcaged.sapp.service.MovieService;

@ManagedBean
@RequestScoped
public class MovieConverter implements Converter {
	
	@EJB
	private MovieService movieService;
	
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                return movieService.getMovieFromImdb(value);
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid movie."));
            }
        }
        else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Movie) object).getImdbReference());
        }
        else {
            return null;
        }
    }   
}
