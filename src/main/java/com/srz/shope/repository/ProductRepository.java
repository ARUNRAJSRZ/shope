package com.srz.shope.repository;

import com.srz.shope.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query("select p.name from Product p where p.id = :id")
	String findNameById(@Param("id") Long id);
}
