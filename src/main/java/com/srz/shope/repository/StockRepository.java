package com.srz.shope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.srz.shope.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
