package com.soa.reporting.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Data object for order info from Order Service.
 */
public class OrderDto {

    private Integer id;

    @JsonAlias("customer_name")
    private String customerName;

    @JsonAlias("customer_email")
    private String customerEmail;

    @JsonAlias("total_amount")
    private BigDecimal totalAmount;

    private String status;

    private List<OrderItemDto> items = new ArrayList<>();

    /**
     * Creates an empty order data object.
     */
    public OrderDto() {
    }

    /**
     * Creates an order data object with all fields.
     *
     * @param id order ID
     * @param customerName customer name
     * @param customerEmail customer email
     * @param totalAmount order total amount
     * @param status order status
     * @param items list of order items
     */
    public OrderDto(Integer id, String customerName, String customerEmail,
            BigDecimal totalAmount, String status, List<OrderItemDto> items) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items != null ? items : new ArrayList<>();
    }

    /**
     * Gets order ID.
     *
     * @return order ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets order ID.
     *
     * @param id order ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets customer name.
     *
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets customer name.
     *
     * @param customerName customer name
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets customer email.
     *
     * @return customer email
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Sets customer email.
     *
     * @param customerEmail customer email
     */
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
     * Gets total amount.
     *
     * @return total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets total amount.
     *
     * @param totalAmount total amount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets order status.
     *
     * @return order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets order status.
     *
     * @param status order status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets order items.
     *
     * @return list of order items
     */
    public List<OrderItemDto> getItems() {
        return items;
    }

    /**
     * Sets order items.
     *
     * @param items list of order items
     */
    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
