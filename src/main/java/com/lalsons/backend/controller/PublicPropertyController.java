package com.lalsons.backend.controller;

import com.lalsons.backend.entity.Property;
import com.lalsons.backend.enums.PropertyType;
import com.lalsons.backend.repository.PropertyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/public/properties")
public class PublicPropertyController {
    
    private final PropertyRepository propertyRepository;
    
    @Value("${app.cms.page-size:10}")
    private int defaultPageSize;

    public PublicPropertyController(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @GetMapping
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UP");
    }

    @GetMapping("/search")
    public List<Property> searchProperties(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer bhk,
            @RequestParam(defaultValue = "0") int page

    ) {
        PropertyType pType = null;
        if (type != null && !type.equalsIgnoreCase("All")) {
            try {
                pType = PropertyType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) { /* Ignore invalid type */ }
        }

        String loc = (location != null && !location.equalsIgnoreCase("All")) ? location : null;
        BigDecimal min = minPrice != null ? BigDecimal.valueOf(minPrice) : null;
        BigDecimal max = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;
        Integer bhkVal = (bhk != null) ? bhk : null;
        
        // Pagination logic
        Pageable pageable = PageRequest.of(page, defaultPageSize);


        return propertyRepository.searchProperties(pType, loc, min, max, bhkVal, pageable);
    }
}