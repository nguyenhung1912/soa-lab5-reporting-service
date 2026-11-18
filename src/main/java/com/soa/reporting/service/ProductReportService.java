package com.soa.reporting.service;

import com.soa.reporting.dto.CreateProductReportRequest;
import com.soa.reporting.dto.OrderDto;
import com.soa.reporting.dto.OrderItemDto;
import com.soa.reporting.dto.ProductDto;
import com.soa.reporting.entity.OrderReport;
import com.soa.reporting.entity.ProductReport;
import com.soa.reporting.repository.OrderReportRepository;
import com.soa.reporting.repository.ProductReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for product report business logic.
 */
@Service
@Transactional
public class ProductReportService {

    private final ProductReportRepository productReportRepository;
    private final OrderReportRepository orderReportRepository;
    private final ProductClientService productClientService;
    private final OrderClientService orderClientService;
    private final BigDecimal defaultCostRatio;

    /**
     * Creates ProductReportService with required dependencies.
     *
     * @param productReportRepository product report repository
     * @param orderReportRepository order report repository
     * @param productClientService client for Product Service
     * @param orderClientService client for Order Service
     * @param defaultCostRatio default ratio for cost calculation
     */
    public ProductReportService(ProductReportRepository productReportRepository,
            OrderReportRepository orderReportRepository,
            ProductClientService productClientService,
            OrderClientService orderClientService,
            @Value("${reporting.default.cost-ratio:0.70}") BigDecimal defaultCostRatio) {
        this.productReportRepository = productReportRepository;
        this.orderReportRepository = orderReportRepository;
        this.productClientService = productClientService;
        this.orderClientService = orderClientService;
        this.defaultCostRatio = defaultCostRatio;
    }

    /**
     * Gets all product reports or filters by product ID.
     *
     * @param productId optional product ID filter
     * @return list of product reports
     */
    @Transactional(readOnly = true)
    public List<ProductReport> getAllProductReports(Integer productId) {
        if (productId != null) {
            return productReportRepository.findByProductId(productId);
        }
        return productReportRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Gets one product report by its ID.
     *
     * @param id product report ID
     * @return matching product report if found
     */
    @Transactional(readOnly = true)
    public Optional<ProductReport> getProductReportById(Integer id) {
        return productReportRepository.findById(id);
    }

    /**
     * Creates a product report using Product Service and Order Service data.
     *
     * @param request product report input data
     * @return saved product report
     */
    public ProductReport createProductReport(CreateProductReportRequest request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("ID sản phẩm (product_id) không được để trống");
        }

        ReportCalculationHelper.validateUnitCost(request.getUnitCost());
        BigDecimal effectiveRatio = ReportCalculationHelper.resolveCostRatio(
                request.getCostRatio(), defaultCostRatio);
        boolean includePending = Boolean.TRUE.equals(request.getIncludePending());

        OrderReport parentOrderReport = resolveParentOrderReport(request.getOrderReportId());
        ProductDto product = productClientService.getProductById(request.getProductId(), null);
        int stockQuantity = product.getQuantity() != null ? product.getQuantity() : 0;

        List<OrderDto> orders = orderClientService.getAllOrders(null);
        int totalSold = 0;
        BigDecimal revenue = ReportCalculationHelper.zeroMoney();

        for (OrderDto order : orders) {
            if (!isMatchingOrder(order, parentOrderReport, includePending)) {
                continue;
            }
            if (order.getItems() == null) {
                continue;
            }
            for (OrderItemDto item : order.getItems()) {
                if (request.getProductId().equals(item.getProductId())) {
                    int itemQty = item.getQuantity() != null ? item.getQuantity() : 0;
                    totalSold += itemQty;
                    revenue = revenue.add(ReportCalculationHelper.calculateItemRevenue(item));
                }
            }
        }

        BigDecimal cost = ReportCalculationHelper.calculateCost(
                revenue, totalSold, request.getUnitCost(), effectiveRatio);

        ProductReport report = new ProductReport(
                request.getProductId(),
                totalSold,
                stockQuantity,
                revenue,
                cost);

        if (parentOrderReport != null) {
            parentOrderReport.addProductReport(report);
            parentOrderReport.recalculateTotals();
            ProductReport savedReport = productReportRepository.save(report);
            orderReportRepository.save(parentOrderReport);
            return savedReport;
        }

        return productReportRepository.save(report);
    }

    /**
     * Deletes a product report by ID.
     *
     * @param id product report ID
     * @return true if deleted, false if not found
     */
    public boolean deleteProductReport(Integer id) {
        Optional<ProductReport> reportOpt = productReportRepository.findById(id);
        if (reportOpt.isEmpty()) {
            return false;
        }

        ProductReport report = reportOpt.get();
        OrderReport parent = report.getOrderReport();
        if (parent != null) {
            parent.removeProductReport(report);
            parent.recalculateTotals();
            orderReportRepository.save(parent);
            return true;
        }

        productReportRepository.deleteById(id);
        return true;
    }

    private OrderReport resolveParentOrderReport(Integer orderReportId) {
        if (orderReportId == null) {
            return null;
        }
        return orderReportRepository.findById(orderReportId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy báo cáo đơn hàng với ID = " + orderReportId));
    }

    private boolean isMatchingOrder(OrderDto order, OrderReport parentOrderReport, boolean includePending) {
        if (order == null || !ReportCalculationHelper.isEligibleOrderStatus(order.getStatus(), includePending)) {
            return false;
        }
        if (parentOrderReport != null) {
            return parentOrderReport.getOrderId().equals(order.getId());
        }
        return true;
    }
}
