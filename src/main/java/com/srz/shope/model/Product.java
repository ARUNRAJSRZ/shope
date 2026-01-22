package com.srz.shope.model;

import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String category;
    private Integer price;
    private String image;
    @jakarta.persistence.Lob
    @JsonIgnore
    private byte[] imageData;
    private String imageContentType;
    private String description;
    private String affiliate;
    private Boolean offer = false;
    private Integer AvailableQuantity;
    private Double discountPrice;
    private double averageRating;
    private Double discountPercentage;
    private Double averagePurchasePrice;
    


    public Product() {}

    public Product(Long id, String code, String name, String category, Integer price, String image,
                   byte[] imageData, String imageContentType, String description, String affiliate,
                   Boolean offer, Integer availableQuantity, Double discountPrice,
                   double averageRating, Double discountPercentage, Double averagePurchasePrice) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.image = image;
        this.imageData = imageData;
        this.imageContentType = imageContentType;
        this.description = description;
        this.affiliate = affiliate;
        this.offer = offer;
        this.AvailableQuantity = availableQuantity;
        this.discountPrice = discountPrice;
        this.averageRating = averageRating;
        this.discountPercentage = discountPercentage;
        this.averagePurchasePrice = averagePurchasePrice;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
    public String getImageContentType() { return imageContentType; }
    public void setImageContentType(String imageContentType) { this.imageContentType = imageContentType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAffiliate() { return affiliate; }
    public void setAffiliate(String affiliate) { this.affiliate = affiliate; }
    public Boolean getOffer() { return offer; }
    public void setOffer(Boolean offer) { this.offer = offer; }
    public Integer getAvailableQuantity() { return AvailableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { AvailableQuantity = availableQuantity; }
    public Double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }
    public Double getAveragePurchasePrice() { return averagePurchasePrice; }
    public void setAveragePurchasePrice(Double averagePurchasePrice) { this.averagePurchasePrice = averagePurchasePrice; } 
    
    
}
