package com.mohan.inventory.controller;

import com.mohan.inventory.model.Product;
import com.mohan.inventory.service.CategoryService;
import com.mohan.inventory.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class ProductWebController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductWebController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "false") boolean lowStockOnly,
                       @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        Page<Product> page = productService.search(q, categoryId, lowStockOnly, pageable);
        model.addAttribute("page", page);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("lowStockOnly", lowStockOnly);
        model.addAttribute("lowStockCount", productService.lowStockCount());
        return "products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "product-form";
    }

    @PostMapping("/products")
    public String create(@Valid @ModelAttribute("product") Product product,
                         BindingResult br,
                         @RequestParam Long categoryId,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "product-form";
        }
        try {
            productService.create(product, categoryId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("error", e.getMessage());
            return "product-form";
        }
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.get(id));
        model.addAttribute("categories", categoryService.findAll());
        return "product-form";
    }

    @PostMapping("/products/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("product") Product product,
                         BindingResult br,
                         @RequestParam Long categoryId,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "product-form";
        }
        productService.update(id, product, categoryId);
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/adjust-stock")
    public String adjustStock(@PathVariable Long id, @RequestParam int delta, Model model) {
        try {
            productService.adjustStock(id, delta);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/products";
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {
        model.addAttribute("products", productService.lowStock());
        return "alerts";
    }
}
