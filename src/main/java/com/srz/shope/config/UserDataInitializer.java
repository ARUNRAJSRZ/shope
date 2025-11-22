package com.srz.shope.config;

import com.srz.shope.model.Role;
import com.srz.shope.model.UserAccount;
import com.srz.shope.repository.RoleRepository;
import com.srz.shope.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // create roles if missing
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // create admin user in DB if not exists
        String adminUsername = System.getenv().getOrDefault("ADMIN_USERNAME", "admin");
        String adminPassword = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin");
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            UserAccount admin = new UserAccount();
            admin.setUsername(adminUsername);
            admin.setEmail(adminUsername + "@example.com");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.getRoles().add(adminRole);
            admin.getRoles().add(userRole);
            userRepository.save(admin);
        }
    }
}
