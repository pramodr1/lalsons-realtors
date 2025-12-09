package com.lalsons.backend.service;

import com.lalsons.backend.entity.Location;
import com.lalsons.backend.entity.MediaAsset;
import com.lalsons.backend.entity.Property;
import com.lalsons.backend.enums.MediaType;
import com.lalsons.backend.repository.LocationRepository;
import com.lalsons.backend.repository.MediaAssetRepository;
import com.lalsons.backend.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {
    
    private final PropertyRepository propertyRepository;
    private final LocationRepository locationRepository;
    private final MediaAssetRepository mediaAssetRepository;
    
    private final Path uploadDir = Paths.get("uploads");

    public PropertyService(PropertyRepository propertyRepository, LocationRepository locationRepository, MediaAssetRepository mediaAssetRepository) {
        this.propertyRepository = propertyRepository;
        this.locationRepository = locationRepository;
        this.mediaAssetRepository = mediaAssetRepository;
        
        try {
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder");
        }
    }

    @Transactional
    public Property createProperty(Property property, Long locationId) {
        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            property.setLocation(location);
        }
        return propertyRepository.save(property);
    }

    @Transactional
    public List<MediaAsset> uploadMedia(Long propertyId, MultipartFile[] files) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id " + propertyId));

        List<MediaAsset> savedAssets = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + (originalName != null ? originalName : "file");
                Path filePath = uploadDir.resolve(fileName);
                
                // Save file to disk
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Create Entity
                MediaAsset asset = new MediaAsset();
                asset.setProperty(property);
                // URL assumes running locally on port 8080
                asset.setUrl("http://localhost:8080/uploads/" + fileName);
                
                String contentType = file.getContentType();
                if (contentType != null && contentType.startsWith("video")) {
                    asset.setMediaType(MediaType.VIDEO);
                } else if (contentType != null && contentType.contains("pdf")) {
                    asset.setMediaType(MediaType.DOCUMENT);
                } else {
                    asset.setMediaType(MediaType.IMAGE);
                }
                
                savedAssets.add(mediaAssetRepository.save(asset));
                
            } catch (IOException e) {
                e.printStackTrace();
                // Continue with other files even if one fails
            }
        }
        return savedAssets;
    }
}