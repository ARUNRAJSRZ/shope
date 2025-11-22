package com.srz.shope.web;

import com.srz.shope.model.Product;
import com.srz.shope.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductsApiController {

    private final ProductRepository repository;

    public ProductsApiController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products")
    public List<Product> list() {
        return repository.findAll();
    }
}
