package com.soa.reporting.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request body for creating an order report.
 */
public class CreateOrderReportRequest {

    @JsonProperty("order_id")
    @JsonAlias("orderId")
    private Integer orderId;

    @JsonProperty("cost_ratio")
    @JsonAlias("costRatio")
    private BigDecimal costRatio;

    @JsonProperty("item_costs")
    @JsonAlias("itemCosts")
    private Map<Integer, BigDecimal> itemCosts;

    @JsonProperty("include_pending")
    @JsonAlias("includePending")
    private Boolean includePending = Boolean.FALSE;

    /**
     * Creates an empty order report request.
     */
    public CreateOrderReportRequest() {
    }

    /**
     * Gets target order ID.
     *
     * @return order ID
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * Sets target order ID.
     *
     * @param orderId order ID
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets custom cost ratio.
     *
     * @return cost ratio or null
     */
    public BigDecimal getCostRatio() {
        return costRatio;
    }

    /**
     * Sets custom cost ratio.
     *
     * @param costRatio cost ratio
     */
    public void setCostRatio(BigDecimal costRatio) {
        this.costRatio = costRatio;
    }

    /**
     * Gets unit costs mapped by product ID.
     *
     * @return map of product ID to unit cost
     */
    public Map<Integer, BigDecimal> getItemCosts() {
        return itemCosts;
    }

    /**
     * Sets unit costs mapped by product ID.
     *
     * @param itemCosts map of product ID to unit cost
     */
    public void setItemCosts(Map<Integer, BigDecimal> itemCosts) {
        this.itemCosts = itemCosts;
    }

    /**
     * Gets flag to allow pending orders.
     *
     * @return true if pending orders are included
     */
    public Boolean getIncludePending() {
        return includePending;
    }

    /**
     * Sets flag to allow pending orders.
     *
     * @param includePending include pending flag
     */
    public void setIncludePending(Boolean includePending) {
        this.includePending = includePending;
    }
}
