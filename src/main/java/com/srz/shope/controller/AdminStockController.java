package com.srz.shope.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.srz.shope.model.Product;
import com.srz.shope.model.Stock;
import com.srz.shope.repository.ProductRepository;
import com.srz.shope.repository.StockRepository;

@Controller
@RequestMapping("/admin")
public class AdminStockController {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public AdminStockController(ProductRepository productRepository, StockRepository stockRepository) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
    }

    @GetMapping("/stock")
    public String stockPage(Model model) {
        List<Product> products = productRepository.findAll();
        List<Stock> stocks = stockRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("stocks", stocks);
        return "admin/stock";
    }

    /**
     * Accepts arrays of form fields so multiple stock rows can be submitted at once.
     */
    @PostMapping("/stock/update")
    public String updateStock(
            @RequestParam(name = "productId") List<Long> productIds,
            @RequestParam(name = "quantity") List<Integer> quantities,
            @RequestParam(name = "unitPrice") List<Double> unitPrices,
            @RequestParam(name = "supplier", required = false) List<String> suppliers,
            @RequestParam(name = "arrivalDate", required = false) List<String> arrivalDates
    ) {
        int n = Math.min(productIds.size(), Math.min(quantities.size(), unitPrices.size()));
        for (int i = 0; i < n; i++) {
            Long pid = productIds.get(i);
            Integer qty = quantities.get(i);
            Double up = unitPrices.get(i);
            if (pid == null || qty == null || qty <= 0 || up == null) continue;

            Product p = productRepository.findById(pid).orElse(null);
            if (p == null) continue;

            Stock s = new Stock();
            s.setProduct(p);
            s.setQuantity(qty);
            s.setUnitPrice(up);
            s.setTotalPrice(up * qty);
            s.setCreatedDate(LocalDate.now());
            s.setStatus("RECEIVED");
            if (suppliers != null && suppliers.size() > i) s.setSupplier(suppliers.get(i));
            if (arrivalDates != null && arrivalDates.size() > i && arrivalDates.get(i) != null && !arrivalDates.get(i).isBlank()) {
                try { s.setArrivalDate(LocalDate.parse(arrivalDates.get(i))); } catch (Exception ex) { /* ignore parse */ }
            }
            stockRepository.save(s);
        

            // update product available quantity if present
            try {
                Integer available = p.getAvailableQuantity();
                if (available == null) available = 0;
                p.setAvailableQuantity(available + qty);
                // update averagePurchasePrice simple moving average approximation
                Double avg = p.getAveragePurchasePrice();
                if (avg == null) avg = up;
                else avg = (avg + up) / 2.0;
                p.setAveragePurchasePrice(avg);
                productRepository.save(p);
            } catch (Exception e) {
                // ignore product update failures to avoid blocking other rows
            }
        }
        return "redirect:/admin/stock";
    }
}
