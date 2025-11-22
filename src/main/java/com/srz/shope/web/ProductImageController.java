package com.srz.shope.web;

import com.srz.shope.model.Product;
import com.srz.shope.repository.ProductRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {

    private final ProductRepository repository;

    public ProductImageController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Long id) {
        Optional<Product> p = repository.findById(id);
        if (p.isEmpty() || p.get().getImageData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Product prod = p.get();
        byte[] data = prod.getImageData();
        String contentType = prod.getImageContentType();
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            if (contentType != null) mediaType = MediaType.parseMediaType(contentType);
        } catch (Exception ignored) {}

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
