package com.srz.shope.web;

import com.srz.shope.model.Product;
import com.srz.shope.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import com.srz.shope.web.dto.ProductDto;

@RestController
@RequestMapping("/api")
public class ProductsApiController {

    private final ProductRepository repository;

    public ProductsApiController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products")
    public List<ProductDto> list() {
        return repository.findAll().stream().map(p -> new ProductDto(
                p.getId(), p.getCode(), p.getName(), p.getCategory(), p.getPrice(),
                (p.getId() != null ? ("/api/products/" + p.getId() + "/image") : null),
                p.getDescription(), p.getAffiliate(), p.getOffer()
        )).collect(Collectors.toList());
    }
}
