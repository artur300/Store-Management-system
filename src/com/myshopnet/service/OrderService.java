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

import java.util.Map;
import java.util.UUID;


public class OrderService {
    private final OrderRepository orderRepository = Singletons.ORDER_REPO;
    private final BranchRepository branchRepository = Singletons.BRANCH_REPO;
    private final ProductRepository productRepository = Singletons.PRODUCT_REPO;
    private final CustomerService customerService = Singletons.CUSTOMER_SERVICE;
    private final CustomerRepository customerRepository = Singletons.CUSTOMER_REPO;
    private final StockService stockService = Singletons.STOCK_SERVICE;

    public Order performOrder(Map<String, Long> mapOfProductsAndQuantities, String branchId, String customerId) {
        Branch branch = branchRepository.get(branchId);
        Customer customer = customerRepository.get(customerId);
        Double baseTotal = 0.0;

        if (branch == null) {
            throw new EntityNotFoundException("Branch");
        }

        for (Map.Entry<String, Long> productStock : mapOfProductsAndQuantities.entrySet()) {
            String productId = productStock.getKey();
            Long requestedQuantity = productStock.getValue();

            Product product = productRepository.get(productId);

            if (product == null) {
                throw new EntityNotFoundException("Product");
            }

            Long currentStock = branch.getProductsStock().getStockOfProducts().get(product);
            if (currentStock == null || currentStock < requestedQuantity) {
                throw new StockException(product.getName());
            }
        }

        for (Map.Entry<String, Long> productQuantity : mapOfProductsAndQuantities.entrySet()) {
            String productId = productQuantity.getKey();
            Long requestedQuantity = productQuantity.getValue();

            Product product = productRepository.get(productId);
            baseTotal += product.getPrice() * requestedQuantity;
            Long currentStock = branch.getProductsStock().getStockOfProducts().get(product);
            Long newStock = currentStock - requestedQuantity;

            stockService.updateProductStock(branchId, productId, newStock);
        }

        customerService.checkCustomerStatus(customerId);

        Order order = new Order(UUID.randomUUID().toString(),
                branchId,
                customerId,
                baseTotal,
                customer.calcBuyingStrategy(baseTotal),
                mapOfProductsAndQuantities);
        orderRepository.create(order);
        Singletons.LOGGER.log(new LogEvent(LogType.PURCHASE,"Order Created"));


        return order;
    }
}
