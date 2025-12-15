package com.lalsons.backend.repository;
import com.lalsons.backend.entity.Property;
import com.lalsons.backend.enums.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:type IS NULL OR p.propertyType = :type) AND " +
           "(:locationName IS NULL OR p.location.name = :locationName) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:bhk IS NULL OR p.bhk = :bhk)")
    List<Property> searchProperties(
        @Param("type") PropertyType type, 
        @Param("locationName") String locationName, 
        @Param("minPrice") BigDecimal minPrice, 
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("bhk") Integer bhk,
        Pageable pageable

    );
}