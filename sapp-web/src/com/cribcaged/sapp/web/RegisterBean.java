package com.cribcaged.sapp.web;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cribcaged.sapp.persistence.entity.SystemUser;
import com.cribcaged.sapp.service.UserService;

@ManagedBean
@ViewScoped
public class RegisterBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(RegisterBean.class);

	private final String ACTION_FAILED = "actionFailed";
	private final double MAX_WIDTH_PROFILE_PHOTO = 300.00;

	@EJB
	private UserService userService;

	@ManagedProperty(value = "#{messageBean}")
	private MessageBean messageBean;
	@ManagedProperty(value = "#{configurationBean}")
	private ConfigurationBean configurationBean;

	private SystemUser newUser;
	private String fileName;

	@PostConstruct
	public void init() {
		newUser = new SystemUser();
	}

	public void register() {
		boolean actionFailed = true;
		try {
			if (fileName == null) {
				addErrorMessage(messageBean.getMessage("register.error.select.profile.photo"));
			}
			else if (userService.findUser(newUser.getUsername()) != null) {
				addErrorMessage(messageBean.getMessage("register.error.user.exists"));
			} else {
				newUser.setCreateTimestamp(new Date());
				newUser.setActive(false);
				newUser.setProfilePhoto(fileName);
				userService.persist(newUser);
				actionFailed = false;
				addInformationMessage(messageBean.getMessage("register.info.successful"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		getRequestContext().addCallbackParam(ACTION_FAILED, actionFailed);
	}

	public void handleFileUpload(FileUploadEvent event) {
		try {
			if (event != null && event.getFile() != null) {
				UploadedFile uploadedFile = event.getFile();
				String path = configurationBean.getConfiguration("uploadPath.profile");
				Path folder = Paths.get(path);
				String filename = String.valueOf(new Date().getTime() + "_");
				String extension = FilenameUtils.getExtension(uploadedFile.getFileName());
				Path file = Files.createTempFile(folder, filename, "." + extension);
				
				BufferedImage img = ImageIO.read(uploadedFile.getInputstream());
				InputStream input = null;
				if (img.getWidth() > MAX_WIDTH_PROFILE_PHOTO) {
					double scale = MAX_WIDTH_PROFILE_PHOTO / (double) img.getWidth();
					int scaleX = (int) (img.getWidth() * scale);
					int scaleY = (int) (img.getHeight() * scale);
					Image newImg = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					BufferedImage bImage = new BufferedImage(newImg.getWidth(null), newImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
					Graphics2D bImageGraphics = bImage.createGraphics();
					bImageGraphics.drawImage(newImg, null, null);
					RenderedImage rImage = (RenderedImage) bImage;
					ImageIO.write(rImage, "jpeg", os);
					input = new ByteArrayInputStream(os.toByteArray());
				} else {
					input = uploadedFile.getInputstream();
				}

				Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
				fileName = file.getFileName().toString();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public MessageBean getMessageBean() {
		return messageBean;
	}

	public void setMessageBean(MessageBean messageBean) {
		this.messageBean = messageBean;
	}

	public ConfigurationBean getConfigurationBean() {
		return configurationBean;
	}

	public void setConfigurationBean(ConfigurationBean configurationBean) {
		this.configurationBean = configurationBean;
	}

	public SystemUser getNewUser() {
		return newUser;
	}

	public void setNewUser(SystemUser newUser) {
		this.newUser = newUser;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
