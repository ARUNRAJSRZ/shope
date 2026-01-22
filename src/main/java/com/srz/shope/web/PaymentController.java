package com.srz.shope.web;

import java.util.Date;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.srz.shope.model.Order;
import com.srz.shope.repository.OrderRepository;
import com.srz.shope.service.CashfreeService;

@Controller
public class PaymentController {

    private final OrderRepository orderRepository;
    private final CashfreeService cashfreeService;

    public PaymentController(OrderRepository orderRepository, CashfreeService cashfreeService) {
        this.orderRepository = orderRepository;
        this.cashfreeService = cashfreeService;
    }

    @GetMapping("/payment")
    public String paymentPage(@RequestParam(name = "total", required = false) Double total, Model model) {
        if (total == null) total = 0.0;
        model.addAttribute("total", total);
        return "payment";
    }

    @PostMapping("/payment/cod")
    public String payCod(@RequestParam(name = "total") Double total) {
        Order order = new Order();
        order.setTotalAmount(total);
        order.setStatus("COD");
        order.setDate(new Date());
        orderRepository.save(order);
        return "redirect:/orders";
    }

    @PostMapping("/payment/online")
    public String payOnline(@RequestParam(name = "total") Double total) {
        Order order = new Order();
        order.setTotalAmount(total);
        order.setStatus("PENDING");
        order.setDate(new Date());
        orderRepository.save(order);

        String returnUrl = "/payment/mock-success"; // Cashfree should call a real callback URL
        String redirect = cashfreeService.createPaymentRedirect(order.getId(), total, returnUrl);
        return "redirect:" + redirect;
    }

    @GetMapping("/payment/mock-success")
    public String mockSuccess(@RequestParam(name = "orderId") Long orderId) {
        Order o = orderRepository.findById(orderId);
        if (o != null) {
            o.setStatus("PAID");
            orderRepository.save(o);
        }
        return "redirect:/orders";
    }
}
