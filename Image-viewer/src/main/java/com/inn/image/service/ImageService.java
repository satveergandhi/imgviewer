package com.inn.image.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inn.image.model.Image;
import com.inn.image.repository.ImageRepository;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.RegionConflictException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

@Service
public class ImageService {
	
	@Value("${minio.url}")
	private String MINIO_URL;
	
	@Value("${minio.username}")
	private String MINIO_USERNAME;
	
	@Value("${minio.password}")
	private String MINIO_PASSWORD;
	
	@Value("${minio.bucket.name}")
	private String MINIO_BUCKET_NAME;
	
	@Value("${minio.file.path}")
	private String MINIO_FILE_PATH;

	@Autowired
	private ImageRepository imageRepository;
	
	public Image saveImg(byte[] imgData) {
		Image image = new Image();
		image.setImgPath(MINIO_FILE_PATH);
		try {
			saveFileToMinio(imgData);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (ErrorResponseException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InsufficientDataException e) {
			e.printStackTrace();
		} catch (InternalException e) {
			e.printStackTrace();
		} catch (InvalidBucketNameException e) {
			e.printStackTrace();
		} catch (InvalidResponseException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (RegionConflictException e) {
			e.printStackTrace();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (XmlParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageRepository.save(image);
	}
	
	public void saveFileToMinio(byte[] image) throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException, NoSuchAlgorithmException, RegionConflictException, ServerException, XmlParserException, IOException {		

		MinioClient minio = 
				MinioClient.builder().endpoint(MINIO_URL)
	            .credentials(MINIO_USERNAME, MINIO_PASSWORD)
	            .build();
		boolean found =
                minio.bucketExists(BucketExistsArgs.builder().bucket(MINIO_BUCKET_NAME).build());
            if (!found) {
            	System.out.println("BUCKET NOT EXISTS");
            	minio.makeBucket(MakeBucketArgs.builder().bucket(MINIO_BUCKET_NAME).build());

            }        
            
		minio.putObject(MINIO_BUCKET_NAME, MINIO_FILE_PATH, new ByteArrayInputStream(image), 
				new PutObjectOptions(image.length, -1));

	}
	
	public ResponseEntity<byte[]> getFileFromMinio(String imgPath) throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {		

		MinioClient minio = 
				MinioClient.builder().endpoint(MINIO_URL)
	            .credentials(MINIO_USERNAME, MINIO_PASSWORD)
	            .build();
		InputStream inputStream=null;
		inputStream = minio.getObject(MINIO_BUCKET_NAME, imgPath);
		byte[] data = IOUtils.toByteArray(inputStream);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
	}
	
	public ResponseEntity<byte[]> getImg(Long id) throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
		Image img = imageRepository.getById(id);
		String imgPath = img.getImgPath();
		return getFileFromMinio(imgPath);
	}

}
