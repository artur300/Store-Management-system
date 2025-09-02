package com.myshopnet.service;

import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Category;
import com.myshopnet.models.Product;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.ProductRepository;

import java.util.List;

public class ProductService {
    private ProductRepository productRepository = new ProductRepository();
    private BranchRepository branchRepository = new BranchRepository();
    private StockService stockService = new StockService();

    public Product createProduct(String productSku, String productName, Category productCategory, Double price) {
        if (price <= 0) {
            throw new ArithmeticException("Product Price must be greater than 0");
        }

        Product product = new Product(productSku, productName, productCategory, price);

        return productRepository.create(product);
    }

    public void addProductToAllBranches(String productId) {
        Product product = productRepository.get(productId);

        if (product == null) {
            throw new EntityNotFoundException("Product");
        }

        List<Branch> allBranches = branchRepository.getAll();

        for (Branch branch : allBranches) {
            branch.getProductsStock().getStockOfProducts().put(product, 0L);

            branchRepository.update(branch.getId(), branch);
        }
    }

    public void removeProductFromAllBranches(String productId) {
        Product product = productRepository.get(productId);
        if (product == null) {
            throw new EntityNotFoundException("Product");
        }

        List<Branch> allBranches = branchRepository.getAll();
        for (Branch branch : allBranches) {
            branch.getProductsStock().getStockOfProducts().remove(product);
            branchRepository.update(branch.getId(), branch);
        }
    }
    public void updateProductInAllBranches(String productId) {
        Product product = productRepository.get(productId);
        if (product == null) {
            throw new EntityNotFoundException("Product");
        }

        List<Branch> allBranches = branchRepository.getAll();
        for (Branch branch : allBranches) {
            if (branch.getProductsStock().getStockOfProducts().containsKey(product)) {
                Long currentStock = branch.getProductsStock().getStockOfProducts().get(product);
                branch.getProductsStock().getStockOfProducts().remove(product);
                branch.getProductsStock().getStockOfProducts().put(product, currentStock);
                branchRepository.update(branch.getId(), branch);
            }
        }
    }
    public void deleteProduct(String productId) {
        Product product = productRepository.get(productId);
        if (product == null) {
            throw new EntityNotFoundException("Product");
        }


        productRepository.delete(productId);


        List<Branch> allBranches = branchRepository.getAll();
        for (Branch branch : allBranches) {
            branch.getProductsStock().getStockOfProducts().remove(product);
            branchRepository.update(branch.getId(), branch);
        }
    }

}
