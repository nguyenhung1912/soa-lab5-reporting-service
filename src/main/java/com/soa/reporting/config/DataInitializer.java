package com.soa.reporting.config;

import com.soa.reporting.entity.OrderReport;
import com.soa.reporting.entity.ProductReport;
import com.soa.reporting.repository.OrderReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Loads initial sample report data into reportdb on startup.
 */
@Configuration
public class DataInitializer {

    /**
     * Inserts sample order and product reports when table is empty.
     *
     * @param orderReportRepository repository for order reports
     * @return startup runner bean
     */
    @Bean
    public CommandLineRunner initReports(OrderReportRepository orderReportRepository) {
        return args -> {
            if (orderReportRepository.count() == 0) {
                OrderReport orderReport = new OrderReport(1, BigDecimal.ZERO, BigDecimal.ZERO);

                ProductReport item1 = new ProductReport(
                        1,
                        2,
                        25,
                        new BigDecimal("4300000.00"),
                        new BigDecimal("3010000.00"));

                ProductReport item2 = new ProductReport(
                        2,
                        1,
                        40,
                        new BigDecimal("2490000.00"),
                        new BigDecimal("1743000.00"));

                orderReport.addProductReport(item1);
                orderReport.addProductReport(item2);
                orderReport.recalculateTotals();

                orderReportRepository.save(orderReport);
            }
        };
    }
}
