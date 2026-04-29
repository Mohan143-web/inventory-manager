package com.mohan.inventory.service;

import com.mohan.inventory.model.Category;
import com.mohan.inventory.model.Product;
import com.mohan.inventory.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Page<Product> search(String q, Long categoryId, boolean lowStockOnly, Pageable pageable) {
        String trimmed = (q == null || q.isBlank()) ? null : q.trim();
        return productRepository.search(trimmed, categoryId, lowStockOnly, pageable);
    }

    public Product get(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public List<Product> lowStock() {
        return productRepository.findLowStock();
    }

    public long lowStockCount() {
        return productRepository.findLowStock().size();
    }

    @Transactional
    public Product create(Product product, Long categoryId) {
        if (productRepository.existsBySkuIgnoreCase(product.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + product.getSku());
        }
        Category category = categoryService.get(categoryId);
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product updates, Long categoryId) {
        Product existing = get(id);
        Category category = categoryService.get(categoryId);
        existing.setName(updates.getName());
        existing.setSku(updates.getSku());
        existing.setCategory(category);
        existing.setPrice(updates.getPrice());
        existing.setLowStockThreshold(updates.getLowStockThreshold());
        existing.setStockLevel(updates.getStockLevel());
        return existing;
    }

    /**
     * Adjust stock by a positive (receive) or negative (sell/return) delta. Runs
     * inside a transaction so the read-then-write under @Version optimistic
     * locking is safe under concurrent requests.
     */
    @Transactional
    public Product adjustStock(Long id, int delta) {
        Product product = get(id);
        int newLevel = product.getStockLevel() + delta;
        if (newLevel < 0) {
            throw new IllegalStateException(
                "Insufficient stock: current=" + product.getStockLevel() + " delta=" + delta);
        }
        product.setStockLevel(newLevel);
        return product;
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
