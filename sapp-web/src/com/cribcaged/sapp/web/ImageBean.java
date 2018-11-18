package com.cribcaged.sapp.web;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cribcaged.sapp.service.PhotoService;

@ManagedBean
@ApplicationScoped
public class ImageBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(ImageBean.class);

	@EJB
	private PhotoService photoService;

	@ManagedProperty(value = "#{configurationBean}")
	private ConfigurationBean configurationBean;

	@PostConstruct
	public void init() {
	}

	public StreamedContent getImage() {
		String fileName = "";
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
				// So, we're rendering the view. Return a stub StreamedContent
				// so that it will generate right URL.
				return new DefaultStreamedContent();
			} else {
				// So, browser is requesting the image. Return a real
				// StreamedContent with the image bytes.
				String path = configurationBean.getConfiguration("uploadPath");
				fileName = context.getExternalContext().getRequestParameterMap().get("fileName");
				Path file = Paths.get(path + fileName);

				InputStream is = new FileInputStream(file.toFile());
				return new DefaultStreamedContent(new ByteArrayInputStream(IOUtils.toByteArray(is)));
			}
		} catch (IOException e) {
			logger.error("Cannot find image file \"{}\".", fileName);
		}
		return new DefaultStreamedContent();
	}

	public StreamedContent getProfileImage() {
		String fileName = "";
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
				// So, we're rendering the view. Return a stub StreamedContent
				// so that it will generate right URL.
				return new DefaultStreamedContent();
			} else {
				// So, browser is requesting the image. Return a real
				// StreamedContent with the image bytes.
				String path = configurationBean.getConfiguration("uploadPath.profile");
				fileName = context.getExternalContext().getRequestParameterMap().get("profilePhotoFileName");
				Path file = Paths.get(path + fileName);
				InputStream is = new FileInputStream(file.toFile());
				DefaultStreamedContent content = new DefaultStreamedContent(new ByteArrayInputStream(IOUtils.toByteArray(is)));
				return content;
			}
		} catch (IOException e) {
			logger.error("Cannot find image file \"{}\".", fileName);
		}
		return new DefaultStreamedContent();
	}

	public ConfigurationBean getConfigurationBean() {
		return configurationBean;
	}

	public void setConfigurationBean(ConfigurationBean configurationBean) {
		this.configurationBean = configurationBean;
	}
}
