package com.srz.shope.web;

import com.srz.shope.model.UserAccount;
import com.srz.shope.model.Role;
import com.srz.shope.repository.UserRepository;
import com.srz.shope.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserAccount());
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute("user") UserAccount user, Model model) {
        // simple checks
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("error","Username is required");
            return "signup";
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error","Email is required");
            return "signup";
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error","Username already exists");
            return "signup";
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error","Email already registered");
            return "signup";
        }

        // encode password and save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user.getRoles().add(userRole);
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
