package com.soa.reporting.service;

import com.soa.reporting.dto.CreateOrderReportRequest;
import com.soa.reporting.dto.OrderDto;
import com.soa.reporting.dto.OrderItemDto;
import com.soa.reporting.dto.ProductDto;
import com.soa.reporting.entity.OrderReport;
import com.soa.reporting.repository.OrderReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrderReportService.
 */
@ExtendWith(MockitoExtension.class)
class OrderReportServiceTest {

    @Mock
    private OrderReportRepository orderReportRepository;

    @Mock
    private OrderClientService orderClientService;

    @Mock
    private ProductClientService productClientService;

    private OrderReportService orderReportService;

    /**
     * Sets up OrderReportService with mocked dependencies.
     */
    @BeforeEach
    void setUp() {
        orderReportService = new OrderReportService(
                orderReportRepository,
                orderClientService,
                productClientService,
                new BigDecimal("0.70"));
    }

    /**
     * Tests creating an order report with mixed custom item cost and default ratio.
     */
    @Test
    void createOrderReport_completedOrderComputesRevenueCostProfitAndStock() {
        OrderItemDto item1 = new OrderItemDto(1, 10, 1, "Keyboard", 2,
                new BigDecimal("2150000.00"), new BigDecimal("4300000.00"));
        OrderItemDto item2 = new OrderItemDto(2, 10, 2, "Mouse", 1,
                new BigDecimal("2490000.00"), new BigDecimal("2490000.00"));
        OrderDto orderDto = new OrderDto(10, "An", "an@example.com",
                new BigDecimal("6790000.00"), "completed", List.of(item1, item2));

        when(orderReportRepository.existsByOrderId(10)).thenReturn(false);
        when(orderClientService.getOrderById(10, null)).thenReturn(orderDto);
        when(productClientService.getProductById(1, null))
                .thenReturn(new ProductDto(1, "Keyboard", "Desc", new BigDecimal("2150000.00"), 25));
        when(productClientService.getProductById(2, null))
                .thenReturn(new ProductDto(2, "Mouse", "Desc", new BigDecimal("2490000.00"), 40));
        when(orderReportRepository.save(any(OrderReport.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderReportRequest request = new CreateOrderReportRequest();
        request.setOrderId(10);
        request.setItemCosts(Map.of(1, new BigDecimal("1500000.00")));

        OrderReport result = orderReportService.createOrderReport(request);

        assertEquals(10, result.getOrderId());
        assertEquals(2, result.getProductReports().size());
        assertEquals(new BigDecimal("6790000.00"), result.getTotalRevenue());
        assertEquals(new BigDecimal("4743000.00"), result.getTotalCost());
        assertEquals(new BigDecimal("2047000.00"), result.getTotalProfit());
        assertEquals(25, result.getProductReports().get(0).getStockQuantity());
    }

    /**
     * Tests that pending order is rejected unless includePending is true.
     */
    @Test
    void createOrderReport_pendingOrderRejectedByDefaultAndAllowedWhenFlagTrue() {
        OrderItemDto item = new OrderItemDto(3, 11, 3, "Monitor", 1,
                new BigDecimal("13500000.00"), new BigDecimal("13500000.00"));
        OrderDto pendingOrder = new OrderDto(11, "Bich", "bich@example.com",
                new BigDecimal("13500000.00"), "pending", List.of(item));

        when(orderReportRepository.existsByOrderId(11)).thenReturn(false);
        when(orderClientService.getOrderById(11, null)).thenReturn(pendingOrder);

        CreateOrderReportRequest rejectedReq = new CreateOrderReportRequest();
        rejectedReq.setOrderId(11);
        assertThrows(IllegalArgumentException.class, () -> orderReportService.createOrderReport(rejectedReq));

        when(productClientService.getProductById(3, null))
                .thenReturn(new ProductDto(3, "Monitor", "Desc", new BigDecimal("13500000.00"), 10));
        when(orderReportRepository.save(any(OrderReport.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderReportRequest allowedReq = new CreateOrderReportRequest();
        allowedReq.setOrderId(11);
        allowedReq.setIncludePending(true);
        OrderReport saved = orderReportService.createOrderReport(allowedReq);
        assertEquals(new BigDecimal("13500000.00"), saved.getTotalRevenue());
        assertEquals(new BigDecimal("9450000.00"), saved.getTotalCost());
        assertEquals(new BigDecimal("4050000.00"), saved.getTotalProfit());
    }

    /**
     * Tests duplicate order ID and delete results.
     */
    @Test
    void createDuplicateThrowsAndDeleteReturnsStatus() {
        when(orderReportRepository.existsByOrderId(1)).thenReturn(true);
        CreateOrderReportRequest request = new CreateOrderReportRequest();
        request.setOrderId(1);
        assertThrows(IllegalArgumentException.class, () -> orderReportService.createOrderReport(request));

        when(orderReportRepository.existsById(99)).thenReturn(false);
        assertFalse(orderReportService.deleteOrderReport(99));

        when(orderReportRepository.existsById(1)).thenReturn(true);
        assertTrue(orderReportService.deleteOrderReport(1));
    }
}
