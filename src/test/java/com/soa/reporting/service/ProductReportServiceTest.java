package com.soa.reporting.service;

import com.soa.reporting.dto.CreateProductReportRequest;
import com.soa.reporting.dto.OrderDto;
import com.soa.reporting.dto.OrderItemDto;
import com.soa.reporting.dto.ProductDto;
import com.soa.reporting.entity.OrderReport;
import com.soa.reporting.entity.ProductReport;
import com.soa.reporting.repository.OrderReportRepository;
import com.soa.reporting.repository.ProductReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProductReportService.
 */
@ExtendWith(MockitoExtension.class)
class ProductReportServiceTest {

    @Mock
    private ProductReportRepository productReportRepository;

    @Mock
    private OrderReportRepository orderReportRepository;

    @Mock
    private ProductClientService productClientService;

    @Mock
    private OrderClientService orderClientService;

    private ProductReportService productReportService;

    /**
     * Sets up ProductReportService with mocked dependencies.
     */
    @BeforeEach
    void setUp() {
        productReportService = new ProductReportService(
                productReportRepository,
                orderReportRepository,
                productClientService,
                orderClientService,
                new BigDecimal("0.70"));
    }

    /**
     * Tests aggregating sold items only from completed orders by default.
     */
    @Test
    void createProductReport_aggregatesCompletedOrdersOnly() {
        ProductDto productDto = new ProductDto(3, "Monitor", "4K", new BigDecimal("13500000.00"), 10);

        OrderDto completedOrder = new OrderDto(1, "A", "a@test.com", new BigDecimal("108000000.00"), "completed",
                List.of(new OrderItemDto(1, 1, 3, "Monitor", 8,
                        new BigDecimal("13500000.00"), new BigDecimal("108000000.00"))));
        OrderDto pendingOrder = new OrderDto(2, "B", "b@test.com", new BigDecimal("13500000.00"), "pending",
                List.of(new OrderItemDto(2, 2, 3, "Monitor", 1,
                        new BigDecimal("13500000.00"), new BigDecimal("13500000.00"))));
        OrderDto cancelledOrder = new OrderDto(3, "C", "c@test.com", new BigDecimal("13500000.00"), "cancelled",
                List.of(new OrderItemDto(3, 3, 3, "Monitor", 1,
                        new BigDecimal("13500000.00"), new BigDecimal("13500000.00"))));

        when(productClientService.getProductById(3, null)).thenReturn(productDto);
        when(orderClientService.getAllOrders(null))
                .thenReturn(List.of(completedOrder, pendingOrder, cancelledOrder));
        when(productReportRepository.save(any(ProductReport.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateProductReportRequest request = new CreateProductReportRequest();
        request.setProductId(3);
        request.setUnitCost(new BigDecimal("10000000.00"));

        ProductReport report = productReportService.createProductReport(request);

        assertEquals(3, report.getProductId());
        assertEquals(8, report.getTotalSold());
        assertEquals(10, report.getStockQuantity());
        assertEquals(new BigDecimal("108000000.00"), report.getRevenue());
        assertEquals(new BigDecimal("80000000.00"), report.getCost());
        assertEquals(new BigDecimal("28000000.00"), report.getProfit());
    }

    /**
     * Tests deleting a child product report updates parent order report totals.
     */
    @Test
    void deleteProductReport_recalculatesParentOrderTotals() {
        OrderReport parent = new OrderReport(1, BigDecimal.ZERO, BigDecimal.ZERO);
        ProductReport child1 = new ProductReport(1, 2, 25,
                new BigDecimal("400.00"), new BigDecimal("280.00"));
        ProductReport child2 = new ProductReport(2, 1, 40,
                new BigDecimal("100.00"), new BigDecimal("70.00"));
        parent.addProductReport(child1);
        parent.addProductReport(child2);
        parent.recalculateTotals();

        when(productReportRepository.findById(2)).thenReturn(Optional.of(child2));

        boolean deleted = productReportService.deleteProductReport(2);

        assertTrue(deleted);
        assertEquals(1, parent.getProductReports().size());
        assertEquals(new BigDecimal("400.00"), parent.getTotalRevenue());
        assertEquals(new BigDecimal("280.00"), parent.getTotalCost());
        assertEquals(new BigDecimal("120.00"), parent.getTotalProfit());
        verify(orderReportRepository).save(parent);
    }
}
