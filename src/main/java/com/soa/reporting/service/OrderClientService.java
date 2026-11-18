package com.soa.reporting.service;

import com.soa.reporting.dto.OrderDto;
import com.soa.reporting.dto.OrderItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * Client service for calling Order Service APIs.
 */
@Service
public class OrderClientService {

    private static final String SERVICE_LABEL = "Order Service (Port 8082)";

    private final RestClient restClient;

    @Value("${order.service.url:http://localhost:8082}")
    private String orderServiceUrl;

    /**
     * Creates OrderClientService with RestClient.
     *
     * @param restClient HTTP client for remote calls
     */
    public OrderClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Gets all orders from Order Service.
     *
     * @param bearerToken optional JWT token
     * @return list of orders
     */
    public List<OrderDto> getAllOrders(String bearerToken) {
        List<OrderDto> orders = ServiceClientSupport.getParameterized(
                restClient,
                orderServiceUrl + "/orders",
                bearerToken,
                new ParameterizedTypeReference<List<OrderDto>>() {
                },
                SERVICE_LABEL);
        return orders != null ? orders : Collections.emptyList();
    }

    /**
     * Gets one order by ID from Order Service.
     *
     * @param orderId target order ID
     * @param bearerToken optional JWT token
     * @return order details with items
     */
    public OrderDto getOrderById(Integer orderId, String bearerToken) {
        if (orderId == null) {
            throw new IllegalArgumentException("ID đơn hàng không được để trống");
        }

        OrderDto order = ServiceClientSupport.getObject(
                restClient,
                orderServiceUrl + "/orders/" + orderId,
                bearerToken,
                OrderDto.class,
                "Không tìm thấy đơn hàng có ID = " + orderId + " trên " + SERVICE_LABEL,
                SERVICE_LABEL);

        if (order == null) {
            throw new IllegalArgumentException("Không thể lấy thông tin đơn hàng ID = " + orderId);
        }
        return order;
    }

    /**
     * Gets all order items or items of a specific order.
     *
     * @param orderId optional order ID filter
     * @param bearerToken optional JWT token
     * @return list of order items
     */
    public List<OrderItemDto> getOrderItems(Integer orderId, String bearerToken) {
        String url = orderId != null
                ? orderServiceUrl + "/order_items?orderId=" + orderId
                : orderServiceUrl + "/order_items";

        List<OrderItemDto> items = ServiceClientSupport.getParameterized(
                restClient,
                url,
                bearerToken,
                new ParameterizedTypeReference<List<OrderItemDto>>() {
                },
                SERVICE_LABEL);
        return items != null ? items : Collections.emptyList();
    }
}
