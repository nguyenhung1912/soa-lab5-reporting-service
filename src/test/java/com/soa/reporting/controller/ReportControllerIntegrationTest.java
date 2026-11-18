package com.soa.reporting.controller;

import com.soa.reporting.dto.AuthResponse;
import com.soa.reporting.dto.OrderDto;
import com.soa.reporting.dto.OrderItemDto;
import com.soa.reporting.dto.ProductDto;
import com.soa.reporting.service.AuthClientService;
import com.soa.reporting.service.OrderClientService;
import com.soa.reporting.service.ProductClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for report endpoints and JWT security filter.
 */
@SpringBootTest
class ReportControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AuthClientService authClientService;

    @MockitoBean
    private ProductClientService productClientService;

    @MockitoBean
    private OrderClientService orderClientService;

    private MockMvc mockMvc;

    /**
     * Builds MockMvc with Spring Security filter chain.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    /**
     * Checks that requests without token return 401 Unauthorized.
     *
     * @throws Exception when request fails
     */
    @Test
    void endpointsRequireJwtToken() throws Exception {
        mockMvc.perform(get("/reports/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        mockMvc.perform(get("/reports/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * Checks GET, POST, and DELETE endpoints for order and product reports.
     *
     * @throws Exception when request fails
     */
    @Test
    void reportCrudEndpointsWorkWithValidToken() throws Exception {
        when(authClientService.validateTokenWithAuthService("Bearer valid-token"))
                .thenReturn(new AuthResponse(true, "Valid", "valid-token", "admin"));

        OrderItemDto item = new OrderItemDto(99, 55, 3, "Monitor", 2,
                new BigDecimal("13500000.00"), new BigDecimal("27000000.00"));
        OrderDto orderDto = new OrderDto(55, "Khach Test", "test@example.com",
                new BigDecimal("27000000.00"), "completed", List.of(item));
        ProductDto productDto = new ProductDto(3, "Monitor", "4K",
                new BigDecimal("13500000.00"), 10);

        when(orderClientService.getOrderById(55, null)).thenReturn(orderDto);
        when(orderClientService.getAllOrders(null)).thenReturn(List.of(orderDto));
        when(productClientService.getProductById(3, null)).thenReturn(productDto);

        mockMvc.perform(get("/reports/orders")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].order_id").value(1));

        mockMvc.perform(post("/reports/orders")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"order_id\":55}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.order_id").value(55))
                .andExpect(jsonPath("$.total_revenue").value(27000000.00))
                .andExpect(jsonPath("$.total_cost").value(18900000.00))
                .andExpect(jsonPath("$.total_profit").value(8100000.00));

        mockMvc.perform(post("/reports/products")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"product_id\":3,\"unit_cost\":10000000.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product_id").value(3))
                .andExpect(jsonPath("$.total_sold").value(2))
                .andExpect(jsonPath("$.stock_quantity").value(10))
                .andExpect(jsonPath("$.revenue").value(27000000.00))
                .andExpect(jsonPath("$.cost").value(20000000.00))
                .andExpect(jsonPath("$.profit").value(7000000.00));

        mockMvc.perform(get("/reports/products?productId=3")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_id").value(3));

        mockMvc.perform(delete("/reports/orders/999")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }
}
