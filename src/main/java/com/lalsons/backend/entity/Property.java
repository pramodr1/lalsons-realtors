package com.lalsons.backend.entity;

import com.lalsons.backend.enums.PropertyType;
import com.lalsons.backend.enums.AreaUnit;
import com.lalsons.backend.enums.HouseAge;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Double getAreaValue() {
		return areaValue;
	}

	public void setAreaValue(Double areaValue) {
		this.areaValue = areaValue;
	}

	public AreaUnit getAreaUnit() {
		return areaUnit;
	}

	public void setAreaUnit(AreaUnit areaUnit) {
		this.areaUnit = areaUnit;
	}

	public Integer getBhk() {
		return bhk;
	}

	public void setBhk(Integer bhk) {
		this.bhk = bhk;
	}

	public HouseAge getHouseAge() {
		return houseAge;
	}

	public void setHouseAge(HouseAge houseAge) {
		this.houseAge = houseAge;
	}

	public List<MediaAsset> getMediaAssets() {
		return mediaAssets;
	}

	public void setMediaAssets(List<MediaAsset> mediaAssets) {
		this.mediaAssets = mediaAssets;
	}

	public Boolean getIsFeatured() {
		return isFeatured;
	}

	public void setIsFeatured(Boolean isFeatured) {
		this.isFeatured = isFeatured;
	}

	@Enumerated(EnumType.STRING)
    @Column(name = "property_type")
    private PropertyType propertyType;
    
    @Column(name = "sub_type")
    private String subType;
    
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private BigDecimal price;
    
    @Column(name = "area_value")
    private Double areaValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "area_unit")
    private AreaUnit areaUnit;
    
    private Integer bhk;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "house_age")
    private HouseAge houseAge; // NEW, MEDIUM, OLD

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<MediaAsset> mediaAssets;
    
    @Column(name = "is_featured")
    private Boolean isFeatured;

	public void setStatus(String string) {
		// TODO Auto-generated method stub
		
	}
}