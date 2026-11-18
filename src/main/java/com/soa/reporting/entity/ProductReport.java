package com.soa.reporting.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for the product_reports table.
 */
@Entity
@Table(name = "product_reports")
public class ProductReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_report_id", nullable = true)
    @JsonBackReference
    private OrderReport orderReport;

    @Column(name = "product_id", nullable = false)
    @JsonProperty("product_id")
    private Integer productId;

    @Column(name = "total_sold", nullable = false)
    @JsonProperty("total_sold")
    private Integer totalSold = 0;

    @Column(name = "stock_quantity", nullable = false)
    @JsonProperty("stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "revenue", nullable = false, precision = 15, scale = 2)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "profit", nullable = false, precision = 15, scale = 2)
    private BigDecimal profit = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * Creates an empty product report.
     */
    public ProductReport() {
    }

    /**
     * Creates a product report with stats and money values.
     *
     * @param productId product ID from Product Service
     * @param totalSold total sold quantity
     * @param stockQuantity current stock quantity
     * @param revenue revenue amount
     * @param cost cost amount
     */
    public ProductReport(Integer productId, Integer totalSold, Integer stockQuantity,
            BigDecimal revenue, BigDecimal cost) {
        this.productId = productId;
        this.totalSold = totalSold != null ? totalSold : 0;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.revenue = revenue != null ? revenue : BigDecimal.ZERO;
        this.cost = cost != null ? cost : BigDecimal.ZERO;
        calculateProfit();
    }

    /**
     * Sets creation time and updates profit before saving.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        calculateProfit();
    }

    /**
     * Updates profit before modifying record.
     */
    @PreUpdate
    protected void onUpdate() {
        calculateProfit();
    }

    /**
     * Computes profit as revenue minus cost.
     */
    public void calculateProfit() {
        BigDecimal safeRevenue = this.revenue != null ? this.revenue : BigDecimal.ZERO;
        BigDecimal safeCost = this.cost != null ? this.cost : BigDecimal.ZERO;
        this.revenue = safeRevenue;
        this.cost = safeCost;
        this.profit = safeRevenue.subtract(safeCost);
    }

    /**
     * Gets parent order report ID.
     *
     * @return order report ID or null
     */
    @JsonProperty("order_report_id")
    public Integer getOrderReportId() {
        return orderReport != null ? orderReport.getId() : null;
    }

    /**
     * Gets report ID.
     *
     * @return report ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets report ID.
     *
     * @param id report ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets parent order report.
     *
     * @return order report or null
     */
    public OrderReport getOrderReport() {
        return orderReport;
    }

    /**
     * Sets parent order report.
     *
     * @param orderReport parent order report
     */
    public void setOrderReport(OrderReport orderReport) {
        this.orderReport = orderReport;
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
     * Gets total sold quantity.
     *
     * @return total sold quantity
     */
    public Integer getTotalSold() {
        return totalSold;
    }

    /**
     * Sets total sold quantity.
     *
     * @param totalSold total sold quantity
     */
    public void setTotalSold(Integer totalSold) {
        this.totalSold = totalSold;
    }

    /**
     * Gets current stock quantity.
     *
     * @return stock quantity
     */
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Sets current stock quantity.
     *
     * @param stockQuantity stock quantity
     */
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    /**
     * Gets revenue amount.
     *
     * @return revenue amount
     */
    public BigDecimal getRevenue() {
        return revenue;
    }

    /**
     * Sets revenue amount.
     *
     * @param revenue revenue amount
     */
    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
        calculateProfit();
    }

    /**
     * Gets cost amount.
     *
     * @return cost amount
     */
    public BigDecimal getCost() {
        return cost;
    }

    /**
     * Sets cost amount.
     *
     * @param cost cost amount
     */
    public void setCost(BigDecimal cost) {
        this.cost = cost;
        calculateProfit();
    }

    /**
     * Gets profit amount.
     *
     * @return profit amount
     */
    public BigDecimal getProfit() {
        return profit;
    }

    /**
     * Sets profit amount.
     *
     * @param profit profit amount
     */
    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    /**
     * Gets creation time.
     *
     * @return creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets creation time.
     *
     * @param createdAt creation time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
