package com.soa.reporting.service;

import com.soa.reporting.dto.CreateOrderReportRequest;
import com.soa.reporting.dto.OrderDto;
import com.soa.reporting.dto.OrderItemDto;
import com.soa.reporting.dto.ProductDto;
import com.soa.reporting.entity.OrderReport;
import com.soa.reporting.entity.ProductReport;
import com.soa.reporting.repository.OrderReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for order report business logic.
 */
@Service
@Transactional
public class OrderReportService {

    private final OrderReportRepository orderReportRepository;
    private final OrderClientService orderClientService;
    private final ProductClientService productClientService;
    private final BigDecimal defaultCostRatio;

    /**
     * Creates OrderReportService with required dependencies.
     *
     * @param orderReportRepository order report repository
     * @param orderClientService client for Order Service
     * @param productClientService client for Product Service
     * @param defaultCostRatio default ratio for cost calculation
     */
    public OrderReportService(OrderReportRepository orderReportRepository,
            OrderClientService orderClientService,
            ProductClientService productClientService,
            @Value("${reporting.default.cost-ratio:0.70}") BigDecimal defaultCostRatio) {
        this.orderReportRepository = orderReportRepository;
        this.orderClientService = orderClientService;
        this.productClientService = productClientService;
        this.defaultCostRatio = defaultCostRatio;
    }

    /**
     * Gets all order reports sorted by newest first.
     *
     * @return list of order reports
     */
    @Transactional(readOnly = true)
    public List<OrderReport> getAllOrderReports() {
        return orderReportRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Gets one order report by its ID.
     *
     * @param id order report ID
     * @return matching order report if found
     */
    @Transactional(readOnly = true)
    public Optional<OrderReport> getOrderReportById(Integer id) {
        return orderReportRepository.findById(id);
    }

    /**
     * Creates a new order report from Order Service and Product Service data.
     *
     * @param request order report input data
     * @return saved order report
     */
    public OrderReport createOrderReport(CreateOrderReportRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("ID đơn hàng (order_id) không được để trống");
        }

        if (orderReportRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalArgumentException(
                    "Báo cáo cho đơn hàng ID = " + request.getOrderId() + " đã tồn tại");
        }

        BigDecimal effectiveRatio = ReportCalculationHelper.resolveCostRatio(
                request.getCostRatio(), defaultCostRatio);
        boolean includePending = Boolean.TRUE.equals(request.getIncludePending());

        OrderDto order = orderClientService.getOrderById(request.getOrderId(), null);
        if (!ReportCalculationHelper.isEligibleOrderStatus(order.getStatus(), includePending)) {
            throw new IllegalArgumentException(
                    "Đơn hàng ID = " + order.getId() + " đang ở trạng thái '" + order.getStatus()
                            + "'. Mặc định chỉ lập báo cáo doanh thu cho đơn 'completed' (hoặc truyền include_pending=true)");
        }

        List<OrderItemDto> items = resolveOrderItems(order);
        OrderReport orderReport = new OrderReport(order.getId(), BigDecimal.ZERO, BigDecimal.ZERO);

        if (items.isEmpty()) {
            BigDecimal revenue = order.getTotalAmount() != null
                    ? order.getTotalAmount().setScale(2, RoundingMode.HALF_UP)
                    : ReportCalculationHelper.zeroMoney();
            BigDecimal cost = ReportCalculationHelper.calculateCost(revenue, 0, null, effectiveRatio);
            orderReport.setTotalRevenue(revenue);
            orderReport.setTotalCost(cost);
            orderReport.setTotalProfit(revenue.subtract(cost));
        } else {
            Map<Integer, BigDecimal> itemCosts = request.getItemCosts();
            for (OrderItemDto item : items) {
                ProductReport productReport = buildChildProductReport(item, itemCosts, effectiveRatio);
                orderReport.addProductReport(productReport);
            }
            orderReport.recalculateTotals();
        }

        return orderReportRepository.save(orderReport);
    }

    /**
     * Deletes an order report by ID.
     *
     * @param id order report ID
     * @return true if deleted, false if not found
     */
    public boolean deleteOrderReport(Integer id) {
        if (!orderReportRepository.existsById(id)) {
            return false;
        }
        orderReportRepository.deleteById(id);
        return true;
    }

    private List<OrderItemDto> resolveOrderItems(OrderDto order) {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            return order.getItems();
        }
        return orderClientService.getOrderItems(order.getId(), null);
    }

    private ProductReport buildChildProductReport(OrderItemDto item,
            Map<Integer, BigDecimal> itemCosts,
            BigDecimal effectiveRatio) {
        if (item.getProductId() == null) {
            throw new IllegalArgumentException("Mặt hàng trong đơn thiếu product_id");
        }

        ProductDto product = productClientService.getProductById(item.getProductId(), null);
        int stockQuantity = product.getQuantity() != null ? product.getQuantity() : 0;
        int soldQuantity = item.getQuantity() != null ? item.getQuantity() : 0;

        BigDecimal revenue = ReportCalculationHelper.calculateItemRevenue(item);
        BigDecimal unitCost = itemCosts != null ? itemCosts.get(item.getProductId()) : null;
        BigDecimal cost = ReportCalculationHelper.calculateCost(revenue, soldQuantity, unitCost, effectiveRatio);

        return new ProductReport(item.getProductId(), soldQuantity, stockQuantity, revenue, cost);
    }
}
