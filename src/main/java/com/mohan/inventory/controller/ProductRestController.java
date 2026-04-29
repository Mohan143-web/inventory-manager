package com.mohan.inventory.controller;

import com.mohan.inventory.model.Product;
import com.mohan.inventory.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<Product> search(@RequestParam(required = false) String q,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(defaultValue = "false") boolean lowStockOnly,
                                @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
                                Pageable pageable) {
        return productService.search(q, categoryId, lowStockOnly, pageable);
    }

    @GetMapping("/low-stock")
    public List<Product> lowStock() {
        return productService.lowStock();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.get(id);
    }

    @PostMapping
    public Product create(@Valid @RequestBody Product product, @RequestParam Long categoryId) {
        return productService.create(product, categoryId);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id,
                          @Valid @RequestBody Product product,
                          @RequestParam Long categoryId) {
        return productService.update(id, product, categoryId);
    }

    @PostMapping("/{id}/adjust-stock")
    public Product adjustStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer delta = body.get("delta");
        if (delta == null) throw new IllegalArgumentException("'delta' required");
        return productService.adjustStock(id, delta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
