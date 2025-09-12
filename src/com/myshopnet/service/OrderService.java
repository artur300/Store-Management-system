package com.myshopnet.service;

import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.errors.StockException;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.models.*;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.CustomerRepository;
import com.myshopnet.repository.OrderRepository;
import com.myshopnet.repository.ProductRepository;
import com.myshopnet.utils.Singletons;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class OrderService {
    private final OrderRepository orderRepository = Singletons.ORDER_REPO;
    private final BranchRepository branchRepository = Singletons.BRANCH_REPO;
    private final ProductRepository productRepository = Singletons.PRODUCT_REPO;
    private final CustomerService customerService = Singletons.CUSTOMER_SERVICE;
    private final CustomerRepository customerRepository = Singletons.CUSTOMER_REPO;
    private final StockService stockService = Singletons.STOCK_SERVICE;

    public Order performOrder(List<Map<String, String>> mapOfProductsAndQuantities, String customerId) {
        Customer customer = customerRepository.get(customerId);
        Double baseTotal = 0.0;

        if (mapOfProductsAndQuantities != null && mapOfProductsAndQuantities.isEmpty()) {
            throw new RuntimeException("Nothing to perform order on.");
        }

        if (customer == null) {
            throw new EntityNotFoundException("Customer");
        }

        for (Map<String, String> mapOfProducts : mapOfProductsAndQuantities) {
            String productSku = mapOfProducts.get("productSku");
            String branchId = mapOfProducts.get("branchId");
            Long quantity = Long.valueOf(mapOfProducts.get("quantity"));

            Branch branch = branchRepository.get(branchId);
            Product product = productRepository.get(productSku);

            if (branch == null) {
                throw new EntityNotFoundException("Branch");
            }

            if (product == null) {
                throw new EntityNotFoundException("Product");
            }

            if (branch.getProductsStock() == null || branch.getProductsStock().getStockOfProducts() == null) {
                throw new EntityNotFoundException("Branch Stock");
            }

            if (branch.getProductsStock().getStockOfProducts().get(product) - quantity < 0) {
                throw new StockException(String.format("Product %s has not enough stock", product.getName()));
            }
        }


        for (Map<String, String> mapOfProducts : mapOfProductsAndQuantities) {
            String productSku = mapOfProducts.get("productSku");
            String branchId = mapOfProducts.get("branchId");
            Long quantity = Long.valueOf(mapOfProducts.get("quantity"));

            Branch branch = branchRepository.get(branchId);
            Product product = productRepository.get(productSku);

            baseTotal += product.getPrice() * quantity;
            Long currentStock = branch.getProductsStock().getStockOfProducts().get(product);
            Long newStock = currentStock - quantity;
            Singletons.STOCK_SERVICE.updateProductStock(branchId, productSku, newStock);
        }

        Singletons.CUSTOMER_SERVICE.checkCustomerStatus(customerId);

        Order order = new Order(UUID.randomUUID().toString(),
                customerId,
                baseTotal,
                customer.calcBuyingStrategy(baseTotal),
                mapOfProductsAndQuantities);

        orderRepository.create(order);
        Singletons.LOGGER.log(new LogEvent(LogType.PURCHASE,"Order Created"));

        return order;
    }
}
