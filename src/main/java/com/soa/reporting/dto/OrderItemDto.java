package com.soa.reporting.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Data object for order item info from Order Service.
 */
public class OrderItemDto {

    private Integer id;

    @JsonProperty("order_id")
    @JsonAlias("orderId")
    private Integer orderId;

    @JsonProperty("product_id")
    @JsonAlias("productId")
    private Integer productId;

    @JsonProperty("product_name")
    @JsonAlias("productName")
    private String productName;

    private Integer quantity;

    @JsonProperty("unit_price")
    @JsonAlias("unitPrice")
    private BigDecimal unitPrice;

    @JsonProperty("total_price")
    @JsonAlias("totalPrice")
    private BigDecimal totalPrice;

    /**
     * Creates an empty order item data object.
     */
    public OrderItemDto() {
    }

    /**
     * Creates an order item data object with all fields.
     *
     * @param id order item ID
     * @param orderId parent order ID
     * @param productId product ID
     * @param productName product name
     * @param quantity ordered quantity
     * @param unitPrice unit price
     * @param totalPrice line total price
     */
    public OrderItemDto(Integer id, Integer orderId, Integer productId, String productName,
            Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    /**
     * Gets order item ID.
     *
     * @return item ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets order item ID.
     *
     * @param id item ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets order ID.
     *
     * @return order ID
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * Sets order ID.
     *
     * @param orderId order ID
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets product ID.
     *
     * @return product ID
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * Sets product ID.
     *
     * @param productId product ID
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * Gets product name.
     *
     * @return product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets product name.
     *
     * @param productName product name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets ordered quantity.
     *
     * @return ordered quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets ordered quantity.
     *
     * @param quantity ordered quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets unit price.
     *
     * @return unit price
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets unit price.
     *
     * @param unitPrice unit price
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Gets line total price.
     *
     * @return total price
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets line total price.
     *
     * @param totalPrice total price
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
