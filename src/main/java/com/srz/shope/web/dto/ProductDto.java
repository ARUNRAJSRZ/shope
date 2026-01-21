package com.srz.shope.web.dto;

public class ProductDto {
    private Long id;
    private String code;
    private String name;
    private String category;
    private Integer price;
    private String image;
    private String description;
    private String affiliate;
    private Boolean offer;

    public ProductDto() {}

    public ProductDto(Long id, String code, String name, String category, Integer price, String image, String description, String affiliate, Boolean offer) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.image = image;
        this.description = description;
        this.affiliate = affiliate;
        this.offer = offer;
    }

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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAffiliate() { return affiliate; }
    public void setAffiliate(String affiliate) { this.affiliate = affiliate; }
    public Boolean getOffer() { return offer; }
    public void setOffer(Boolean offer) { this.offer = offer; }
}
