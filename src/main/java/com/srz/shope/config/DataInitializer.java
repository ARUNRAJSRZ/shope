package com.srz.shope.config;

import com.srz.shope.model.Product;
import com.srz.shope.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository repository;

    public DataInitializer(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) return; // don't overwrite existing data

        List<Product> seeds = List.of(
                new Product(null, "p1", "Car Mobile mount", "Car Accessories", 1099, "carmount.jpg", null, "Mini Dashboard Mount for iPhone 17/16/15/13/12, Samsung Galaxy Series.", null, null, true, null, null, 0, null, null),
                new Product(null, "p2", "Safety Hand Gloves", "Accessories", 144, "SafetyHandGloves.jpg", null, "Industrial Safety Hand Gloves (Pack of 1) Anti-Cut | Cut Resistant | Heat Resistant | Industrial Use | for Finger and Hand Protection.", null, null, false, null, null, 0, null, null),
                new Product(null, "p3", "Baby Cradle Swing", "Baby products", 1599, "Baby_Cradle_Swing.jpg", null, "Baby Cradle Swing/Jhula (Thottil Cloth, Palna, Dolna) Set | Cradle Cloth with Padded Bed", null, null, true, null, null, 0, null, null),
                new Product(null, "p4", "Magnetic Phones Holder", "Car Accessories", 629, "Magnetic_Phones_Holder.jpg", null, "360° Vaccum Magnetic Phones Holder, Magnetic Car Phone Mount", null, null, false, null, null, 0, null, null)
        );

        for (Product p : seeds) {
            try {
                ClassPathResource res = new ClassPathResource("static/images/" + p.getImage());
                if (res.exists()) {
                    try (InputStream in = res.getInputStream()) {
                        byte[] bytes = in.readAllBytes();
                        p.setImageData(bytes);
                        String ct = Files.probeContentType(res.getFile().toPath());
                        p.setImageContentType(ct != null ? ct : "image/jpeg");
                    }
                }
            } catch (Exception e) {
                // ignore image load failures; product will still be inserted with image filename
            }
            repository.save(p);
        }
    }
}

