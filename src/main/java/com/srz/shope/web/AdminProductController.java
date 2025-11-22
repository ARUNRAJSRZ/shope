package com.srz.shope.web;

import com.srz.shope.model.Product;
import com.srz.shope.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository repository;
    private final Logger log = LoggerFactory.getLogger(AdminProductController.class);

    public AdminProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        // template is located under templates/admin/add-product.html
        return "admin/add-product";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Product product = repository.findById(id).orElse(new Product());
        model.addAttribute("product", product);
        return "admin/add-product";
    }

    @PostMapping("/{id}")
    public String update(@org.springframework.web.bind.annotation.PathVariable Long id,
                         @ModelAttribute Product product,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Product existing = repository.findById(id).orElse(null);
            if (existing == null) {
                log.warn("Product id={} not found for update", id);
                return "redirect:/admin?error=notfound";
            }
            // update fields
            existing.setCode(product.getCode());
            existing.setName(product.getName());
            existing.setCategory(product.getCategory());
            existing.setPrice(product.getPrice());
            existing.setAffiliate(product.getAffiliate());
            existing.setDescription(product.getDescription());
            existing.setOffer(product.getOffer());

            if (imageFile != null && !imageFile.isEmpty()) {
                existing.setImage(imageFile.getOriginalFilename());
                try {
                    existing.setImageData(imageFile.getBytes());
                } catch (IOException e) {
                    log.warn("Failed to read uploaded image bytes", e);
                }
                existing.setImageContentType(imageFile.getContentType());
            }

            repository.save(existing);
            return "redirect:/admin";
        } catch (Exception ex) {
            log.error("Failed to update product id={}", id, ex);
            return "redirect:/admin?error=1";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@org.springframework.web.bind.annotation.PathVariable Long id) {
        try {
            repository.deleteById(id);
            return "redirect:/admin";
        } catch (Exception ex) {
            log.error("Failed to delete product id={}", id, ex);
            return "redirect:/admin?error=1";
        }
    }

    @PostMapping
    public String create(@ModelAttribute Product product,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        try {
            log.info("Creating product: {}", product.getName());
            if (imageFile != null && !imageFile.isEmpty()) {
                product.setImage(imageFile.getOriginalFilename());
                product.setImageData(imageFile.getBytes());
                product.setImageContentType(imageFile.getContentType());
                log.info("Received image {} ({} bytes)", imageFile.getOriginalFilename(), imageFile.getSize());
            }
            repository.save(product);
            log.info("Product saved with id={}", product.getId());
            // After adding a product, redirect back to the admin landing page
            return "redirect:/admin";
        } catch (Exception ex) {
            log.error("Failed to create product", ex);
            // redirect back to admin page with error flag for UI to show message
            return "redirect:/admin?error=1";
        }
    }
}
