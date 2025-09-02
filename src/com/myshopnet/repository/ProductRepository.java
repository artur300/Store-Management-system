package com.myshopnet.repository;

import com.myshopnet.data.Data;
import com.myshopnet.errors.EntityAlreadyExistsException;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.Order;
import com.myshopnet.models.Product;

import java.util.List;

public class ProductRepository implements Repository<Product> {
    @Override
    public Product create(Product product) {
        if (Data.getAllAccounts().containsKey(product.getSku())) {
            throw new EntityAlreadyExistsException("Product");
        }

        return Data.getProducts().put(product.getSku(), product);
    }

    @Override
    public Product update(String id, Product product) {
        Product updatedProduct = null;

        if (Data.getProducts().containsKey(id)) {
            updatedProduct = Data.getProducts().put(id, product);
        }
        else {
            throw new EntityNotFoundException("Product");
        }

        return updatedProduct;
    }

    @Override
    public void delete(String id) {
        Data.getProducts().remove(id);
    }

    @Override
    public Product get(String id) {
        return Data.getProducts().get(id);
    }

    @Override
    public List<Product> getAll() {
        return Data.getProducts().values().stream().toList();
    }
}
