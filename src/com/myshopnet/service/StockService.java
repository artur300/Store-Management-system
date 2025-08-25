package com.myshopnet.service;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Product;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.ProductRepository;

public class StockService {
    private BranchRepository branchRepository = new BranchRepository();
    private ProductRepository productRepository = new ProductRepository();


    public void updateProductStock(String branchId, String productId, Long quantity) {
        Branch branch = branchRepository.get(branchId);
        Product product = productRepository.get(productId);


    }
}


