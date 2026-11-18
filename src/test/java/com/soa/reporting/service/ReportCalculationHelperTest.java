package com.soa.reporting.service;

import com.soa.reporting.dto.OrderItemDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ReportCalculationHelper.
 */
class ReportCalculationHelperTest {

    /**
     * Tests valid custom ratio and default fallback ratio.
     */
    @Test
    void resolveCostRatio_validAndFallback() {
        assertEquals(new BigDecimal("0.65"),
                ReportCalculationHelper.resolveCostRatio(new BigDecimal("0.65"), new BigDecimal("0.70")));
        assertEquals(new BigDecimal("0.70"),
                ReportCalculationHelper.resolveCostRatio(null, new BigDecimal("0.70")));
    }

    /**
     * Tests that out-of-range cost ratio throws exception.
     */
    @Test
    void resolveCostRatio_invalidRangeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ReportCalculationHelper.resolveCostRatio(new BigDecimal("-0.01"), new BigDecimal("0.70")));
        assertThrows(IllegalArgumentException.class,
                () -> ReportCalculationHelper.resolveCostRatio(new BigDecimal("1.01"), new BigDecimal("0.70")));
    }

    /**
     * Tests order status rules for completed, pending, and cancelled.
     */
    @Test
    void isEligibleOrderStatus_checksAllStatuses() {
        assertTrue(ReportCalculationHelper.isEligibleOrderStatus("completed", false));
        assertTrue(ReportCalculationHelper.isEligibleOrderStatus("COMPLETED", false));
        assertFalse(ReportCalculationHelper.isEligibleOrderStatus("pending", false));
        assertTrue(ReportCalculationHelper.isEligibleOrderStatus("pending", true));
        assertFalse(ReportCalculationHelper.isEligibleOrderStatus("cancelled", true));
    }

    /**
     * Tests revenue extraction from total price or unit price times quantity.
     */
    @Test
    void calculateItemRevenue_usesTotalOrUnitMultiplyQuantity() {
        OrderItemDto withTotal = new OrderItemDto(1, 1, 1, "Keyboard", 2,
                new BigDecimal("100.00"), new BigDecimal("200.00"));
        assertEquals(new BigDecimal("200.00"), ReportCalculationHelper.calculateItemRevenue(withTotal));

        OrderItemDto withoutTotal = new OrderItemDto(2, 1, 1, "Mouse", 3,
                new BigDecimal("50.00"), null);
        assertEquals(new BigDecimal("150.00"), ReportCalculationHelper.calculateItemRevenue(withoutTotal));
    }

    /**
     * Tests cost priority between unit cost and ratio fallback.
     */
    @Test
    void calculateCost_unitCostHasPriorityOverRatio() {
        BigDecimal withUnitCost = ReportCalculationHelper.calculateCost(
                new BigDecimal("4300000.00"), 2, new BigDecimal("1500000.00"), new BigDecimal("0.70"));
        assertEquals(new BigDecimal("3000000.00"), withUnitCost);

        BigDecimal withRatio = ReportCalculationHelper.calculateCost(
                new BigDecimal("4300000.00"), 2, null, new BigDecimal("0.70"));
        assertEquals(new BigDecimal("3010000.00"), withRatio);

        assertThrows(IllegalArgumentException.class,
                () -> ReportCalculationHelper.calculateCost(
                        new BigDecimal("100.00"), 1, new BigDecimal("-1.00"), new BigDecimal("0.70")));
    }
}
