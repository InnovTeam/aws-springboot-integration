package com.example.aws.awss3rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@RestController
@RequestMapping("/documents")
public class DocumentController {
	private AmazonS3 awsS3Client;
	@Value("${amazonProperties.bucketName}")
	private String bucketName;

	@Autowired
	DocumentController(AmazonS3 awsS3Client) {
		this.awsS3Client = awsS3Client;
	}

	@GetMapping("/")
	public List<String> uploadFile() {
		List<String> files = new ArrayList<String>();
		ObjectListing objectListing = this.awsS3Client.listObjects(bucketName);
		for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
			files.add(os.getKey());
		}
		return files;
	}
	
	@GetMapping(path="/{documentName}",produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<InputStreamResource> getSingleFile(@PathVariable String documentName) throws IOException {
		
		S3Object s3object = this.awsS3Client.getObject(bucketName, documentName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		
		return ResponseEntity.ok().contentType(org.springframework.http.MediaType.TEXT_PLAIN).cacheControl(CacheControl.noCache())
	            .header("Content-Disposition", "attachment; filename=" + documentName)
	            .body(new InputStreamResource(inputStream));
		
		
	}

}
