package com.srz.shope.web;

import com.srz.shope.model.Order;
import com.srz.shope.model.UserAccount;
import com.srz.shope.repository.OrderRepository;
import com.srz.shope.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;

@Controller
public class OrdersController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/orders")
    public String orders(Model model, Principal principal) {
         if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));     
        List<Order> orders = orderRepository.findAllByUser(user);
        model.addAttribute("orders", orders);
        return "orders";
    }
}