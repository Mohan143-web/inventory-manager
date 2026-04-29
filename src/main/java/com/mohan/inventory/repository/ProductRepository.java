package com.mohan.inventory.repository;

import com.mohan.inventory.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
             WHERE (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                                  OR LOWER(p.sku)  LIKE LOWER(CONCAT('%', :q, '%')))
               AND (:categoryId IS NULL OR p.category.id = :categoryId)
               AND (:lowStockOnly = FALSE OR p.stockLevel <= p.lowStockThreshold)
            """)
    Page<Product> search(@Param("q") String q,
                         @Param("categoryId") Long categoryId,
                         @Param("lowStockOnly") boolean lowStockOnly,
                         Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockLevel <= p.lowStockThreshold ORDER BY p.stockLevel ASC")
    List<Product> findLowStock();

    long countByStockLevelLessThanEqual(int threshold);
    boolean existsBySkuIgnoreCase(String sku);
}
