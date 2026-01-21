package com.srz.shope.web;

import com.srz.shope.model.ShippingAdress;
import com.srz.shope.model.UserAccount;
import com.srz.shope.repository.ShippingAdressRepository;
import com.srz.shope.repository.UserRepository;
import com.srz.shope.web.dto.ShippingAddressDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
public class ShippingAddressApiController {

    @Autowired
    private ShippingAdressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<ShippingAddressDto> list(Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return addressRepository.findByUser(user).stream().map(a -> new ShippingAddressDto(
                a.getId(), a.getName(), a.getAddressLine1(), a.getAddressLine2(), a.getCity(), a.getState(), a.getPostalCode(), a.getCountry(), a.getPhoneNumber(), a.getIsActive()
                , user.getDefaultShippingAddress() != null && user.getDefaultShippingAddress().getId().equals(a.getId())
        )).collect(Collectors.toList());
    }

    @PostMapping
    public ShippingAddressDto create(@RequestBody ShippingAddressDto dto, Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        ShippingAdress a = new ShippingAdress();
        a.setName(dto.getName());
        a.setAddressLine1(dto.getAddressLine1());
        a.setAddressLine2(dto.getAddressLine2());
        a.setCity(dto.getCity());
        a.setState(dto.getState());
        a.setPostalCode(dto.getPostalCode());
        a.setCountry(dto.getCountry());
        a.setPhoneNumber(dto.getPhoneNumber());
        a.setIsActive(dto.getIsActive() == null ? true : dto.getIsActive());
        a.setUser(user);
        ShippingAdress saved = addressRepository.save(a);
        user.setDefaultShippingAddress(saved);
        userRepository.save(user);
        return new ShippingAddressDto(saved.getId(), saved.getName(), saved.getAddressLine1(), saved.getAddressLine2(), saved.getCity(), saved.getState(), saved.getPostalCode(), saved.getCountry(), saved.getPhoneNumber(), saved.getIsActive(),saved.getId().equals(user.getDefaultShippingAddress().getId()));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        ShippingAdress a = addressRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!a.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        // if deleting default, clear default
        if (user.getDefaultShippingAddress() != null && user.getDefaultShippingAddress().getId().equals(id)) {
            user.setDefaultShippingAddress(null);
            userRepository.save(user);
        }
        addressRepository.delete(a);
    }

    @PostMapping("/{id}/default")
    public void setDefault(@PathVariable Long id, Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserAccount user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        ShippingAdress a = addressRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!a.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        user.setDefaultShippingAddress(a);
        userRepository.save(user);
    }
}
