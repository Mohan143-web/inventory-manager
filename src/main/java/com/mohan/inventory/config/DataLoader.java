package com.mohan.inventory.config;

import com.mohan.inventory.model.Category;
import com.mohan.inventory.model.Product;
import com.mohan.inventory.repository.CategoryRepository;
import com.mohan.inventory.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seed(CategoryRepository categories, ProductRepository products) {
        return args -> {
            if (categories.count() > 0) return;

            Category electronics = categories.save(new Category("Electronics"));
            Category books = categories.save(new Category("Books"));
            Category groceries = categories.save(new Category("Groceries"));

            products.saveAll(List.of(
                    product("Wireless Mouse",  "ELE-001", electronics, "19.99",  42, 10),
                    product("USB-C Cable",     "ELE-002", electronics, "9.50",    8, 10),
                    product("Mechanical KB",   "ELE-003", electronics, "89.00",   3,  5),
                    product("Effective Java",  "BK-001",  books,       "44.99",  15,  5),
                    product("Clean Code",      "BK-002",  books,       "39.50",   2,  5),
                    product("Coffee Beans 1kg","GRO-001", groceries,   "12.00",  60, 20),
                    product("Olive Oil 500ml", "GRO-002", groceries,   "8.75",    4, 15)
            ));
        };
    }

    private static Product product(String name, String sku, Category cat,
                                   String price, int stock, int threshold) {
        Product p = new Product();
        p.setName(name);
        p.setSku(sku);
        p.setCategory(cat);
        p.setPrice(new BigDecimal(price));
        p.setStockLevel(stock);
        p.setLowStockThreshold(threshold);
        return p;
    }
}
