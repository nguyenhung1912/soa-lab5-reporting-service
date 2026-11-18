package com.soa.reporting.controller;

import com.soa.reporting.dto.ApiResponse;
import com.soa.reporting.dto.CreateProductReportRequest;
import com.soa.reporting.entity.ProductReport;
import com.soa.reporting.service.ProductReportService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for product report APIs.
 */
@RestController
@RequestMapping("/reports/products")
@Tag(name = "Product Report API", description = "Các RESTful API báo cáo hàng tồn, hàng bán được, doanh thu, chi phí, lợi nhuận theo sản phẩm")
public class ProductReportController {

    private final ProductReportService productReportService;

    /**
     * Creates ProductReportController with service bean.
     *
     * @param productReportService service for product reports
     */
    public ProductReportController(ProductReportService productReportService) {
        this.productReportService = productReportService;
    }

    /**
     * Gets all product reports or filters by product ID.
     *
     * @param productId optional product ID filter
     * @return list of product reports
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả các báo cáo theo sản phẩm", description = "Trả về danh sách báo cáo sản phẩm, hỗ trợ lọc theo productId")
    public ResponseEntity<List<ProductReport>> getAllProductReports(
            @RequestParam(value = "productId", required = false) Integer productId) {
        return ResponseEntity.ok(productReportService.getAllProductReports(productId));
    }

    /**
     * Gets one product report by ID.
     *
     * @param id product report ID
     * @return product report or 404 message
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết báo cáo cho một sản phẩm", description = "Truy vấn thông tin chi tiết báo cáo sản phẩm theo ID")
    public ResponseEntity<?> getProductReportById(@PathVariable("id") Integer id) {
        Optional<ProductReport> reportOpt = productReportService.getProductReportById(id);
        if (reportOpt.isPresent()) {
            return ResponseEntity.ok(reportOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "Không tìm thấy báo cáo sản phẩm với ID = " + id));
    }

    /**
     * Creates a new product report from Product Service and Order Service data.
     *
     * @param request input data with product ID and optional cost settings
     * @return created product report
     */
    @PostMapping
    @Operation(summary = "Tạo báo cáo sản phẩm mới", description = "Tạo báo cáo sản phẩm mới dựa trên dữ liệu từ dịch vụ quản lý sản phẩm và quản lý đơn hàng")
    public ResponseEntity<ProductReport> createProductReport(@RequestBody CreateProductReportRequest request) {
        ProductReport created = productReportService.createProductReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Deletes a product report by ID.
     *
     * @param id product report ID
     * @return delete result message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa báo cáo sản phẩm", description = "Xóa báo cáo sản phẩm khỏi database theo ID")
    public ResponseEntity<ApiResponse<Void>> deleteProductReport(@PathVariable("id") Integer id) {
        boolean deleted = productReportService.deleteProductReport(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.ok("Đã xóa thành công báo cáo sản phẩm có ID = " + id, null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "Không tìm thấy báo cáo sản phẩm với ID = " + id + " để xóa"));
    }
}
