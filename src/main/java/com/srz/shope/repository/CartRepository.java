package com.srz.shope.repository;

import com.srz.shope.model.Cart;
import com.srz.shope.model.Product;
import com.srz.shope.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserAndProduct(UserAccount user, Product product);
    List<Cart> findByUserAndIsActiveTrue(UserAccount user);

    @Query("select new com.srz.shope.web.dto.CartDto(c.id, p.id, p.name, c.unitPrice, c.quantity, concat('/api/products/', p.id, '/image')) "
            + "from Cart c join c.product p where c.user = :user and c.isActive = true order by c.createdDate desc")
    List<com.srz.shope.web.dto.CartDto> findDtosByUser(@Param("user") UserAccount user);
}
