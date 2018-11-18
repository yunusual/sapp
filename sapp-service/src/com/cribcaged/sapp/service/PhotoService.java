package com.cribcaged.sapp.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cribcaged.sapp.model.PhotoExifModel;
import com.cribcaged.sapp.persistence.dao.JpaDaoFactory;
import com.cribcaged.sapp.persistence.dao.PhotoDao;
import com.cribcaged.sapp.persistence.dto.PhotoFilter;
import com.cribcaged.sapp.persistence.entity.Content;
import com.cribcaged.sapp.persistence.entity.Photo;
import com.cribcaged.sapp.persistence.entity.SystemUser;
import com.cribcaged.sapp.persistence.entity.enumeration.CategoryType;
import com.cribcaged.sapp.persistence.entity.enumeration.ContentType;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

@Stateless
public class PhotoService extends AbstractJpaService {

	private static Logger logger = LoggerFactory.getLogger(PhotoService.class);
	
	private final double MAX_WIDTH_PHOTO = 750.00;

	private PhotoDao photoDao;
	
	@EJB
	private UserService userService;
	@EJB
	private ContentService contentService;

	@PostConstruct
	public void init() {
		photoDao = JpaDaoFactory.createPhotoDao(entityManager);
	}

	public List<Photo> getPhotos(PhotoFilter filter) {
		return photoDao.getPhotos(filter);
	}

	public PhotoExifModel getPhotoExifModel(Path file) {
		PhotoExifModel model = new PhotoExifModel();
		try {
			ImageMetadata metadata = Imaging.getMetadata(file.toFile());
			if (metadata instanceof JpegImageMetadata) {
				JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
				
				TiffField dateTaken = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
				
				TiffField gpsLatitude = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
				TiffField gpsLatitudeRef = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
				TiffField gpsLongitude = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
				TiffField gpsLongitudeRef = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
				
				if (dateTaken != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
					String dateStr = (String) dateTaken.getValue();
					model.setDateTaken(sdf.parse(dateStr));
				}
				
				if (gpsLatitude != null && gpsLatitudeRef != null && gpsLongitude != null && gpsLongitudeRef != null) {
					String latitudeExif = convertToExifFormat((RationalNumber[]) gpsLatitude.getValue());
					String longitudeExif = convertToExifFormat((RationalNumber[]) gpsLongitude.getValue());

					Float latitude = convertToDegree(latitudeExif);
					Float longitude = convertToDegree(longitudeExif);

					if (!gpsLatitudeRef.getStringValue().equals("N")) {
						latitude = 0 - latitude;
					}

					if (!gpsLongitudeRef.getStringValue().equals("E")) {
						longitude = 0 - longitude;
					}
					
					model.setLatitude((double) latitude);
					model.setLongitude((double) longitude);
				}
			}
		} catch (ImageReadException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return model;

	}

	private String convertToExifFormat(RationalNumber[] numbers) {
		RationalNumber no1 = numbers[0];
		RationalNumber no2 = numbers[1];
		RationalNumber no3 = numbers[2];
		String val = no1.numerator + "/" + no1.divisor + ", " + no2.numerator + "/" + no2.divisor + ", " + no3.numerator + "/" + no3.divisor;
		return val;
	}

	private Float convertToDegree(String stringDMS) {
		Float result = null;
		String[] DMS = stringDMS.split(",", 3);

		String[] stringD = DMS[0].split("/", 2);
		Double D0 = new Double(stringD[0]);
		Double D1 = new Double(stringD[1]);
		Double FloatD = D0 / D1;

		String[] stringM = DMS[1].split("/", 2);
		Double M0 = new Double(stringM[0]);
		Double M1 = new Double(stringM[1]);
		Double FloatM = M0 / M1;

		String[] stringS = DMS[2].split("/", 2);
		Double S0 = new Double(stringS[0]);
		Double S1 = new Double(stringS[1]);
		Double FloatS = S0 / S1;

		result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

		return result;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Photo createNewPhoto(Path file, SystemUser user, String description, CategoryType contentCategory, PhotoExifModel exifModel) {
		Photo photo = null;
		try {
			Content newContent = new Content();
			newContent.setSystemUser(user);
			newContent.setCategory(contentCategory);
			newContent.setType(ContentType.PHOTO);
			newContent.setDescription(description);
			newContent.setCreateTimestamp(new Date());
			
			photo = new Photo();
			photo.setContent(newContent);
			photo.setFileName(file.getFileName().toString());
			if (exifModel != null) {
				photo.setDateTaken(exifModel.getDateTaken());
				photo.setLatitude(exifModel.getLatitude());
				photo.setLongitude(exifModel.getLongitude());
			}
			
			merge(photo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return photo;
	}

	public Path uploadOriginalPhoto(InputStream inputstream, String originalFileName, String fileNameBase, String directory) {
		try {
			Path folder = Paths.get(directory);
			String filename = fileNameBase + "_original_";
			String extension = FilenameUtils.getExtension(originalFileName);
			Path file = Files.createTempFile(folder, filename, "." + extension);
			Files.copy(inputstream, file, StandardCopyOption.REPLACE_EXISTING);
			return file;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public Path rotateImage(Path file, String fileNameBase, String directory) {
		Path newFile = null;
		try {
			BufferedImage originalImage = ImageIO.read(file.toFile());

			Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());
			ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			JpegDirectory jpegDirectory = (JpegDirectory) metadata.getFirstDirectoryOfType(JpegDirectory.class);

			int orientation = 1;
			try {
				orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			int width = jpegDirectory.getImageWidth();
			int height = jpegDirectory.getImageHeight();

			AffineTransform affineTransform = new AffineTransform();

			switch (orientation) {
			case 1:
				break;
			case 2: // Flip X
				affineTransform.scale(-1.0, 1.0);
				affineTransform.translate(-width, 0);
				break;
			case 3: // PI rotation
				affineTransform.translate(width, height);
				affineTransform.rotate(Math.PI);
				break;
			case 4: // Flip Y
				affineTransform.scale(1.0, -1.0);
				affineTransform.translate(0, -height);
				break;
			case 5: // - PI/2 and Flip X
				affineTransform.rotate(-Math.PI / 2);
				affineTransform.scale(-1.0, 1.0);
				break;
			case 6: // -PI/2 and -width
				affineTransform.translate(height, 0);
				affineTransform.rotate(Math.PI / 2);
				break;
			case 7: // PI/2 and Flip
				affineTransform.scale(-1.0, 1.0);
				affineTransform.translate(-height, 0);
				affineTransform.translate(0, width);
				affineTransform.rotate(3 * Math.PI / 2);
				break;
			case 8: // PI / 2
				affineTransform.translate(0, width);
				affineTransform.rotate(3 * Math.PI / 2);
				break;
			default:
				break;
			}

			AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage destinationImage = new BufferedImage(originalImage.getHeight(), originalImage.getWidth(), originalImage.getType());
			destinationImage = affineTransformOp.filter(originalImage, destinationImage);

			Path folder = Paths.get(directory);
			String extension = FilenameUtils.getExtension(file.getFileName().toString());
			newFile = Files.createTempFile(folder, fileNameBase + "_rotated_", "." + extension);

			Graphics2D bImageGraphics = destinationImage.createGraphics();
			bImageGraphics.drawImage(destinationImage, null, null);
			RenderedImage rImage = (RenderedImage) destinationImage;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(rImage, "jpeg", os);
			InputStream input = new ByteArrayInputStream(os.toByteArray());
			Files.copy(input, newFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return newFile;
	}

	public Path resizePhoto(Path file, String fileNameBase, String directory) {
		try {
			BufferedImage img = ImageIO.read(file.toFile());
			if (img.getWidth() > MAX_WIDTH_PHOTO) {
				Path folder = Paths.get(directory);
				String filename = fileNameBase + "_resized_";
				String extension = FilenameUtils.getExtension(file.getFileName().toString());
				Path newFile = Files.createTempFile(folder, filename, "." + extension);

				double scale = MAX_WIDTH_PHOTO / (double) img.getWidth();
				int scaleX = (int) (img.getWidth() * scale);
				int scaleY = (int) (img.getHeight() * scale);
				Image newImg = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				BufferedImage bImage = new BufferedImage(newImg.getWidth(null), newImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
				Graphics2D bImageGraphics = bImage.createGraphics();
				bImageGraphics.drawImage(newImg, null, null);
				RenderedImage rImage = (RenderedImage) bImage;
				ImageIO.write(rImage, "jpeg", os);
				InputStream input = new ByteArrayInputStream(os.toByteArray());
				Files.copy(input, newFile, StandardCopyOption.REPLACE_EXISTING);
				Files.delete(file);
				return newFile;
			} else {
				return file;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return file;
	}
}
