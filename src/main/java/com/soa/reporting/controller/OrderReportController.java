package com.soa.reporting.controller;

import com.soa.reporting.dto.ApiResponse;
import com.soa.reporting.dto.CreateOrderReportRequest;
import com.soa.reporting.entity.OrderReport;
import com.soa.reporting.service.OrderReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for order report APIs.
 */
@RestController
@RequestMapping("/reports/orders")
@Tag(name = "Order Report API", description = "Các RESTful API báo cáo doanh thu, chi phí, lợi nhuận theo đơn hàng")
public class OrderReportController {

    private final OrderReportService orderReportService;

    /**
     * Creates OrderReportController with service bean.
     *
     * @param orderReportService service for order reports
     */
    public OrderReportController(OrderReportService orderReportService) {
        this.orderReportService = orderReportService;
    }

    /**
     * Gets all order reports.
     *
     * @return list of order reports
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả các báo cáo theo đơn hàng", description = "Trả về toàn bộ danh sách báo cáo đơn hàng")
    public ResponseEntity<List<OrderReport>> getAllOrderReports() {
        return ResponseEntity.ok(orderReportService.getAllOrderReports());
    }

    /**
     * Gets one order report by ID.
     *
     * @param id order report ID
     * @return order report or 404 message
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết báo cáo cho một đơn hàng", description = "Truy vấn thông tin chi tiết báo cáo đơn hàng theo ID")
    public ResponseEntity<?> getOrderReportById(@PathVariable("id") Integer id) {
        Optional<OrderReport> reportOpt = orderReportService.getOrderReportById(id);
        if (reportOpt.isPresent()) {
            return ResponseEntity.ok(reportOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "Không tìm thấy báo cáo đơn hàng với ID = " + id));
    }

    /**
     * Creates a new order report from Order Service data.
     *
     * @param request input data with order ID and optional costs
     * @return created order report
     */
    @PostMapping
    @Operation(summary = "Tạo báo cáo đơn hàng mới", description = "Tạo báo cáo đơn hàng mới dựa trên dữ liệu từ dịch vụ quản lý đơn hàng và tồn kho từ dịch vụ sản phẩm")
    public ResponseEntity<OrderReport> createOrderReport(@RequestBody CreateOrderReportRequest request) {
        OrderReport created = orderReportService.createOrderReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Deletes an order report by ID.
     *
     * @param id order report ID
     * @return delete result message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa báo cáo đơn hàng", description = "Xóa báo cáo đơn hàng và các chi tiết sản phẩm liên quan theo ID")
    public ResponseEntity<ApiResponse<Void>> deleteOrderReport(@PathVariable("id") Integer id) {
        boolean deleted = orderReportService.deleteOrderReport(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.ok("Đã xóa thành công báo cáo đơn hàng có ID = " + id, null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "Không tìm thấy báo cáo đơn hàng với ID = " + id + " để xóa"));
    }
}
