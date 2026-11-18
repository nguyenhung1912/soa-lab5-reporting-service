package com.soa.reporting.dto;

import java.math.BigDecimal;

/**
 * Data object for product info from Product Service.
 */
public class ProductDto {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;

    /**
     * Creates an empty product data object.
     */
    public ProductDto() {
    }

    /**
     * Creates a product data object with all fields.
     *
     * @param id product ID
     * @param name product name
     * @param description product description
     * @param price selling price
     * @param quantity stock quantity
     */
    public ProductDto(Integer id, String name, String description, BigDecimal price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Gets product ID.
     *
     * @return product ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets product ID.
     *
     * @param id product ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets product name.
     *
     * @return product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets product name.
     *
     * @param name product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets product description.
     *
     * @return product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets product description.
     *
     * @param description product description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets product price.
     *
     * @return product price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets product price.
     *
     * @param price product price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets product stock quantity.
     *
     * @return stock quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets product stock quantity.
     *
     * @param quantity stock quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
