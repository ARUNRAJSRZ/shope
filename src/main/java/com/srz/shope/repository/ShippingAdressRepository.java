package com.srz.shope.repository;

import com.srz.shope.model.ShippingAdress;
import com.srz.shope.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingAdressRepository extends JpaRepository<ShippingAdress, Long> {
    List<ShippingAdress> findByUser(UserAccount user);
}
