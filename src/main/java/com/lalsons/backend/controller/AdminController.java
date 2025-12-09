package com.lalsons.backend.controller;

import com.lalsons.backend.entity.MediaAsset;
import com.lalsons.backend.entity.Property;
import com.lalsons.backend.service.PropertyService;
import com.lalsons.backend.service.ExcelParserService;
import com.lalsons.backend.repository.PropertyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/properties")
public class AdminController {

    private final PropertyService propertyService;
    private final ExcelParserService excelParserService;
    private final PropertyRepository propertyRepository;

    public AdminController(PropertyService propertyService, ExcelParserService excelParserService, PropertyRepository propertyRepository) {
        this.propertyService = propertyService;
        this.excelParserService = excelParserService;
        this.propertyRepository = propertyRepository;
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property, @RequestParam Long locationId) {
        Property created = propertyService.createProperty(property, locationId);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Property>> uploadBulkProperties(@RequestParam("file") MultipartFile file) {
        try {
            List<Property> properties = excelParserService.parseExcel(file.getInputStream());
            List<Property> saved = propertyRepository.saveAll(properties);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/media")
    public ResponseEntity<List<MediaAsset>> uploadPropertyMedia(
            @PathVariable Long id, 
            @RequestParam("files") MultipartFile[] files) {
        List<MediaAsset> assets = propertyService.uploadMedia(id, files);
        return ResponseEntity.ok(assets);
    }
}