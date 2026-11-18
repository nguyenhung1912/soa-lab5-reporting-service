package com.soa.reporting.service;

import com.soa.reporting.dto.OrderItemDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Shared math and rule helper for report services.
 */
final class ReportCalculationHelper {

    private static final BigDecimal ONE = BigDecimal.ONE;

    private ReportCalculationHelper() {
    }

    /**
     * Checks and returns a valid cost ratio between 0 and 1.
     *
     * @param customRatio optional ratio from request
     * @param defaultRatio fallback ratio from config
     * @return valid cost ratio
     */
    static BigDecimal resolveCostRatio(BigDecimal customRatio, BigDecimal defaultRatio) {
        BigDecimal ratio = customRatio != null ? customRatio : defaultRatio;
        if (ratio == null) {
            throw new IllegalArgumentException("Tỷ lệ chi phí không được để trống");
        }
        if (ratio.compareTo(BigDecimal.ZERO) < 0 || ratio.compareTo(ONE) > 0) {
            throw new IllegalArgumentException("Tỷ lệ chi phí (cost_ratio) phải nằm trong khoảng từ 0.00 đến 1.00");
        }
        return ratio;
    }

    /**
     * Checks that unit cost is not negative.
     *
     * @param unitCost optional unit cost
     */
    static void validateUnitCost(BigDecimal unitCost) {
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá vốn đơn vị (unit_cost) không được nhỏ hơn 0");
        }
    }

    /**
     * Checks if an order status can be counted in reports.
     *
     * @param status order status text
     * @param includePending true to allow pending orders
     * @return true if order is eligible
     */
    static boolean isEligibleOrderStatus(String status, boolean includePending) {
        String normalized = status != null ? status.trim().toLowerCase() : "";
        if ("completed".equals(normalized)) {
            return true;
        }
        return includePending && "pending".equals(normalized);
    }

    /**
     * Gets revenue amount from one order item.
     *
     * @param item order item data
     * @return scaled revenue amount
     */
    static BigDecimal calculateItemRevenue(OrderItemDto item) {
        if (item == null) {
            return zeroMoney();
        }
        if (item.getTotalPrice() != null) {
            return item.getTotalPrice().setScale(2, RoundingMode.HALF_UP);
        }
        if (item.getUnitPrice() != null && item.getQuantity() != null) {
            return item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return zeroMoney();
    }

    /**
     * Computes cost using unit cost first, or cost ratio as fallback.
     *
     * @param revenue total revenue amount
     * @param quantity sold item count
     * @param unitCost optional cost per item
     * @param effectiveCostRatio cost ratio between 0 and 1
     * @return scaled cost amount
     */
    static BigDecimal calculateCost(BigDecimal revenue,
            int quantity,
            BigDecimal unitCost,
            BigDecimal effectiveCostRatio) {
        validateUnitCost(unitCost);
        if (unitCost != null) {
            return unitCost.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal safeRevenue = revenue != null ? revenue : BigDecimal.ZERO;
        return safeRevenue.multiply(effectiveCostRatio).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns zero money with two decimal places.
     *
     * @return zero BigDecimal scaled to 2
     */
    static BigDecimal zeroMoney() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
