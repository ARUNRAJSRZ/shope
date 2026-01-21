package com.srz.shope.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import com.srz.shope.repository.OrderRepository;

@Controller
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/order-confirm")
    public String confirmOrder() {
        // Example: Save a dummy order (replace with real cart logic)
       // java.util.List<String> items = java.util.Arrays.asList("Product A", "Product B");
        // com.srz.shope.model.Order order = new com.srz.shope.model.Order( 
        //     System.currentTimeMillis(),
        //     items,
        //     "Placed",
        //     new java.util.Date()
        // );

        // Order order = new Order(System.currentTimeMillis(), items, "Placed", new java.util.Date()); 

        //  orderRepository.save(order);
        return "redirect:/orders";
    }
}