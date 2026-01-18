package com.samjay.rehire.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.samjay.rehire.exception.ApplicationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
public class CloudinaryFileUpload {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.name}")
    String cloudinaryName;

    @Value("${cloudinary.api_key}")
    String cloudinaryApiKey;

    @Value("${cloudinary.secret}")
    String cloudinaryApiSecret;

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryFileUpload.class);


    public CloudinaryFileUpload(
            @Value("${cloudinary.name}") String cloudinaryName,
            @Value("${cloudinary.api_key}") String cloudinaryApiKey,
            @Value("${cloudinary.secret}") String cloudinaryApiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinaryName,
                "api_key", cloudinaryApiKey,
                "api_secret", cloudinaryApiSecret
        ));
    }

    @PostConstruct
    public void warmUpCloudinaryConnection() {

        try {

            cloudinary.api().ping(ObjectUtils.emptyMap());

        } catch (Exception e) {

            logger.error("Failed to initiate cloudinary {} ", e.getMessage());

        }
    }

    public String saveFile(MultipartFile file) {

        try {

            var uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "raw",
                                    "folder", "cvs",
                                    "public_id", UUID.randomUUID().toString()
                            )
                    );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {

            throw new ApplicationException("Failed to save file", HttpStatus.BAD_REQUEST);

        }
    }
}