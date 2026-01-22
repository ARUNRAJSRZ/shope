package com.srz.shope.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {
    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/address")
    public String address() {
        return "address";
    }

    // @GetMapping("/payment")
    // public String payment() {
    //     return "payment";
    // }
}