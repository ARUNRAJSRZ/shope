package com.srz.shope.controller;

import java.security.Principal;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }

    // POST /login is handled by Spring Security; keep this controller only for rendering the login page.

    @GetMapping({"/","/home"})
    public String home(Principal principal, org.springframework.security.core.Authentication authentication, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
            model.addAttribute("isAdmin", isAdmin);
        }
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

}
