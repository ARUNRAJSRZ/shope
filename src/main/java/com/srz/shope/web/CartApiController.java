package com.srz.shope.web;

import com.srz.shope.model.Cart;
import com.srz.shope.model.Product;
import com.srz.shope.model.UserAccount;
import com.srz.shope.repository.CartRepository;
import com.srz.shope.repository.ProductRepository;
import com.srz.shope.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.srz.shope.web.dto.CartDto;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public com.srz.shope.web.dto.CartDto addToCart(@RequestParam Long productId, @RequestParam Integer quantity, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Product product = productRepository.findById(productId).orElseThrow();
        Cart cart = cartRepository.findByUserAndProduct(user, product);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            cart.setUnitPrice(product.getPrice().doubleValue());
            cart.setStatus("ACTIVE");
            cart.setIsActive(true);
        } else {
            cart.setQuantity(cart.getQuantity() + quantity);
        }
        Cart saved = cartRepository.save(cart);
        String img = null;
        Long pid = saved.getProduct() != null ? saved.getProduct().getId() : null;
        if (pid != null) {
            img = "/api/products/" + pid + "/image";
        }
        String pname = pid != null ? productRepository.findNameById(pid) : "";
        return new com.srz.shope.web.dto.CartDto(saved.getId(), pid, pname, saved.getUnitPrice(), saved.getQuantity(), img);
    }

    @GetMapping
    public List<CartDto> getCart(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return cartRepository.findDtosByUser(user);
    }

    @PutMapping("/{id}")
    public CartDto updateQuantity(@PathVariable Long id, @RequestParam Integer quantity, Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!cart.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        cart.setQuantity(quantity);
        Cart saved = cartRepository.save(cart);
        String img = null;
        Long pid = saved.getProduct() != null ? saved.getProduct().getId() : null;
        if (pid != null) {
            img = "/api/products/" + pid + "/image";
        }
        String pname = pid != null ? productRepository.findNameById(pid) : "";
        return new CartDto(saved.getId(), pid, pname, saved.getUnitPrice(), saved.getQuantity(), img);
    }

    @PostMapping("/buynow")
    public com.srz.shope.web.dto.CartDto buyNow(@RequestParam Long productId, @RequestParam Integer quantity, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Product product = productRepository.findById(productId).orElseThrow();
        // Remove existing active cart items for this user
        List<Cart> existing = cartRepository.findByUserAndIsActiveTrue(user);
        if (existing != null && !existing.isEmpty()) {
            cartRepository.deleteAll(existing);
        }
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setUnitPrice(product.getPrice().doubleValue());
        cart.setStatus("ACTIVE");
        cart.setIsActive(true);
        Cart saved = cartRepository.save(cart);
        Long pid = saved.getProduct() != null ? saved.getProduct().getId() : null;
        String img = pid != null ? "/api/products/" + pid + "/image" : null;
        String pname = pid != null ? productRepository.findNameById(pid) : "";
        return new com.srz.shope.web.dto.CartDto(saved.getId(), pid, pname, saved.getUnitPrice(), saved.getQuantity(), img);
    }

    // @DeleteMapping("/{id}")
    // public void deleteCartItem(@PathVariable Long id, Principal principal) {
    //     if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    //     UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    //     Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    //     if (!cart.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    //     cartRepository.delete(cart);
    // }

    // @PutMapping("/{id}")
    // public CartDto updateQuantity2(@PathVariable Long id, @RequestParam Integer quantity, Principal principal) {
    //     if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    //     UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    //     Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    //     if (!cart.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    //     cart.setQuantity(quantity);
    //     Cart saved = cartRepository.save(cart);
    //     String img = null;
    //     if (saved.getProduct() != null && saved.getProduct().getId() != null) img = "/api/products/" + saved.getProduct().getId() + "/image";
    //     return new CartDto(saved.getId(), saved.getProduct() != null ? saved.getProduct().getId() : null, saved.getProduct() != null ? saved.getProduct().getName() : "", saved.getUnitPrice(), saved.getQuantity(), img);
    // }

    @DeleteMapping("/{id}")
    public void deleteCart(@PathVariable Long id, Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!cart.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        cartRepository.delete(cart);
    }
}