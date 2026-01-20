package com.srz.shope.web;

import com.srz.shope.model.Order;
import com.srz.shope.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrdersController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/orders")
    public String orders(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "orders";
    }
}