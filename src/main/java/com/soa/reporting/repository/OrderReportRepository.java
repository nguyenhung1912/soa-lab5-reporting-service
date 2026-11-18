package com.soa.reporting.repository;

import com.soa.reporting.entity.OrderReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for orders_reports table.
 */
public interface OrderReportRepository extends JpaRepository<OrderReport, Integer> {

    /**
     * Finds one report by primary ID with child product reports.
     *
     * @param id order report ID
     * @return matching order report if found
     */
    @Override
    @EntityGraph(attributePaths = "productReports")
    Optional<OrderReport> findById(Integer id);

    /**
     * Finds one report by order ID.
     *
     * @param orderId order ID
     * @return matching order report if found
     */
    @EntityGraph(attributePaths = "productReports")
    Optional<OrderReport> findByOrderId(Integer orderId);

    /**
     * Checks if a report exists for an order ID.
     *
     * @param orderId order ID
     * @return true if exists
     */
    boolean existsByOrderId(Integer orderId);

    /**
     * Finds all order reports sorted by newest first.
     *
     * @return list of order reports
     */
    @EntityGraph(attributePaths = "productReports")
    List<OrderReport> findAllByOrderByCreatedAtDesc();
}
