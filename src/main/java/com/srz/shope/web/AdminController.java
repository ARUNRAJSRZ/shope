package com.srz.shope.web;

import com.srz.shope.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository repository;

    public AdminController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String dashboard(java.security.Principal principal, org.springframework.security.core.Authentication authentication, Model model) {
        model.addAttribute("products", repository.findAll());
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
            model.addAttribute("isAdmin", isAdmin);
        }
        return "admin/index";
    }
}
