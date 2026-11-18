package com.soa.reporting.repository;

import com.soa.reporting.entity.ProductReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for product_reports table.
 */
public interface ProductReportRepository extends JpaRepository<ProductReport, Integer> {

    /**
     * Finds one product report by ID with parent order report.
     *
     * @param id product report ID
     * @return matching product report if found
     */
    @Override
    @EntityGraph(attributePaths = "orderReport")
    Optional<ProductReport> findById(Integer id);

    /**
     * Finds product reports by product ID.
     *
     * @param productId product ID
     * @return list of product reports
     */
    @EntityGraph(attributePaths = "orderReport")
    List<ProductReport> findByProductId(Integer productId);

    /**
     * Finds product reports by parent order report ID.
     *
     * @param orderReportId order report ID
     * @return list of product reports
     */
    @EntityGraph(attributePaths = "orderReport")
    List<ProductReport> findByOrderReportId(Integer orderReportId);

    /**
     * Finds all product reports sorted by newest first.
     *
     * @return list of product reports
     */
    @EntityGraph(attributePaths = "orderReport")
    List<ProductReport> findAllByOrderByCreatedAtDesc();
}
