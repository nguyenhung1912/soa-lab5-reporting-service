package com.soa.reporting.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Request body for creating a product report.
 */
public class CreateProductReportRequest {

    @JsonProperty("product_id")
    @JsonAlias("productId")
    private Integer productId;

    @JsonProperty("order_report_id")
    @JsonAlias("orderReportId")
    private Integer orderReportId;

    @JsonProperty("unit_cost")
    @JsonAlias("unitCost")
    private BigDecimal unitCost;

    @JsonProperty("cost_ratio")
    @JsonAlias("costRatio")
    private BigDecimal costRatio;

    @JsonProperty("include_pending")
    @JsonAlias("includePending")
    private Boolean includePending = Boolean.FALSE;

    /**
     * Creates an empty product report request.
     */
    public CreateProductReportRequest() {
    }

    /**
     * Gets target product ID.
     *
     * @return product ID
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * Sets target product ID.
     *
     * @param productId product ID
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * Gets optional parent order report ID.
     *
     * @return order report ID or null
     */
    public Integer getOrderReportId() {
        return orderReportId;
    }

    /**
     * Sets optional parent order report ID.
     *
     * @param orderReportId order report ID
     */
    public void setOrderReportId(Integer orderReportId) {
        this.orderReportId = orderReportId;
    }

    /**
     * Gets unit cost value.
     *
     * @return unit cost or null
     */
    public BigDecimal getUnitCost() {
        return unitCost;
    }

    /**
     * Sets unit cost value.
     *
     * @param unitCost unit cost
     */
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
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
     * Gets flag to include pending orders.
     *
     * @return true if pending orders are included
     */
    public Boolean getIncludePending() {
        return includePending;
    }

    /**
     * Sets flag to include pending orders.
     *
     * @param includePending include pending flag
     */
    public void setIncludePending(Boolean includePending) {
        this.includePending = includePending;
    }
}
