package com.srz.shope.service;

import com.srz.shope.model.UserAccount;
import com.srz.shope.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;


@Service
@Primary
public class CompositeUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final InMemoryUserDetailsManager inMemory;

    public CompositeUserDetailsService(UserRepository userRepository, InMemoryUserDetailsManager inMemory) {
        this.userRepository = userRepository;
        this.inMemory = inMemory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // prefer DB users (lookup by username OR email)
    UserAccount ua = userRepository.findByUsername(username).orElse(null);
    if (ua == null) {
        ua = userRepository.findByEmail(username).orElse(null);
    }
    if (ua != null) {
        var authorities = ua.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getName()))
            .toList();
        return new org.springframework.security.core.userdetails.User(
            ua.getUsername(), ua.getPassword(), authorities);
    }

        // fallback to in-memory (admin)
        if (inMemory.userExists(username)) {
            return inMemory.loadUserByUsername(username);
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
