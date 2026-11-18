package com.soa.reporting.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity for the orders_reports table.
 */
@Entity
@Table(name = "orders_reports")
public class OrderReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_id", nullable = false, unique = true)
    @JsonProperty("order_id")
    private Integer orderId;

    @Column(name = "total_revenue", nullable = false, precision = 15, scale = 2)
    @JsonProperty("total_revenue")
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "total_cost", nullable = false, precision = 15, scale = 2)
    @JsonProperty("total_cost")
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "total_profit", nullable = false, precision = 15, scale = 2)
    @JsonProperty("total_profit")
    private BigDecimal totalProfit = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "orderReport", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonProperty("product_reports")
    private List<ProductReport> productReports = new ArrayList<>();

    /**
     * Creates an empty order report.
     */
    public OrderReport() {
    }

    /**
     * Creates an order report with money values.
     *
     * @param orderId order ID from Order Service
     * @param totalRevenue total revenue amount
     * @param totalCost total cost amount
     */
    public OrderReport(Integer orderId, BigDecimal totalRevenue, BigDecimal totalCost) {
        this.orderId = orderId;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.totalProfit = this.totalRevenue.subtract(this.totalCost);
    }

    /**
     * Sets default values before saving.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalRevenue == null) {
            this.totalRevenue = BigDecimal.ZERO;
        }
        if (this.totalCost == null) {
            this.totalCost = BigDecimal.ZERO;
        }
        this.totalProfit = this.totalRevenue.subtract(this.totalCost);
    }

    /**
     * Adds one product report to this order report.
     *
     * @param productReport child product report
     */
    public void addProductReport(ProductReport productReport) {
        productReports.add(productReport);
        productReport.setOrderReport(this);
    }

    /**
     * Removes one product report from this order report.
     *
     * @param productReport child product report
     */
    public void removeProductReport(ProductReport productReport) {
        productReports.remove(productReport);
        productReport.setOrderReport(null);
    }

    /**
     * Updates total revenue, cost, and profit from child items.
     */
    public void recalculateTotals() {
        BigDecimal revenueSum = BigDecimal.ZERO;
        BigDecimal costSum = BigDecimal.ZERO;
        for (ProductReport item : productReports) {
            if (item.getRevenue() != null) {
                revenueSum = revenueSum.add(item.getRevenue());
            }
            if (item.getCost() != null) {
                costSum = costSum.add(item.getCost());
            }
        }
        this.totalRevenue = revenueSum;
        this.totalCost = costSum;
        this.totalProfit = revenueSum.subtract(costSum);
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
     * Gets total revenue.
     *
     * @return total revenue
     */
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Sets total revenue.
     *
     * @param totalRevenue total revenue
     */
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    /**
     * Gets total cost.
     *
     * @return total cost
     */
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Sets total cost.
     *
     * @param totalCost total cost
     */
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Gets total profit.
     *
     * @return total profit
     */
    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    /**
     * Sets total profit.
     *
     * @param totalProfit total profit
     */
    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
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

    /**
     * Gets child product reports.
     *
     * @return list of product reports
     */
    public List<ProductReport> getProductReports() {
        return productReports;
    }

    /**
     * Sets child product reports.
     *
     * @param productReports list of product reports
     */
    public void setProductReports(List<ProductReport> productReports) {
        this.productReports = productReports;
    }
}
