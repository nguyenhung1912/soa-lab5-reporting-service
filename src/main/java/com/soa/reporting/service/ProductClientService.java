package com.soa.reporting.service;

import com.soa.reporting.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * Client service for calling Product Service APIs.
 */
@Service
public class ProductClientService {

    private static final String SERVICE_LABEL = "Product Service (Port 8081)";

    private final RestClient restClient;

    @Value("${product.service.url:http://localhost:8081/products}")
    private String productServiceUrl;

    /**
     * Creates ProductClientService with RestClient.
     *
     * @param restClient HTTP client for remote calls
     */
    public ProductClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Gets all products from Product Service.
     *
     * @param bearerToken optional JWT token
     * @return list of products
     */
    public List<ProductDto> getAllProducts(String bearerToken) {
        List<ProductDto> products = ServiceClientSupport.getParameterized(
                restClient,
                productServiceUrl,
                bearerToken,
                new ParameterizedTypeReference<List<ProductDto>>() {
                },
                SERVICE_LABEL);
        return products != null ? products : Collections.emptyList();
    }

    /**
     * Gets one product by ID from Product Service.
     *
     * @param productId target product ID
     * @param bearerToken optional JWT token
     * @return product details
     */
    public ProductDto getProductById(Integer productId, String bearerToken) {
        if (productId == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống");
        }

        ProductDto product = ServiceClientSupport.getObject(
                restClient,
                productServiceUrl + "/" + productId,
                bearerToken,
                ProductDto.class,
                "Không tìm thấy sản phẩm có ID = " + productId + " trên " + SERVICE_LABEL,
                SERVICE_LABEL);

        if (product == null) {
            throw new IllegalArgumentException("Không thể lấy thông tin sản phẩm ID = " + productId);
        }
        return product;
    }
}
