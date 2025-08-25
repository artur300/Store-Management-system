package com.myshopnet.service;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Product;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.ProductRepository;

import java.util.List;

public class StockService {
    private BranchRepository branchRepository = new BranchRepository();
    private ProductRepository productRepository = new ProductRepository();

    public void updateProductStock(String branchId, String productId, Long quantity) {
        Branch branch = branchRepository.get(branchId);
        Product product = productRepository.get(productId);

        if (product == null) {
            throw new EntityNotFoundException("Product");
        }

        if (branch == null) {
            throw new EntityNotFoundException("Branch");
        }

        branch.getProductsStock().getStockOfProducts().replace(product, quantity);
    }

    public void removeProductStockFromBranch(String productId) {
        List<Branch> allBranch = branchRepository.getAll();
        Product product = productRepository.get(productId);

        if (product == null) {
            throw new EntityNotFoundException("Product");
        }

        for (Branch branch : allBranch) {
            branch.getProductsStock().getStockOfProducts().remove(productId);

            branchRepository.update(branch.getId(), branch);
        }
    }
}


