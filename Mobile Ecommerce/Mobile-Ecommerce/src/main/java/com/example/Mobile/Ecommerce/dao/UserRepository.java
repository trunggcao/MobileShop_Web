package com.example.Mobile.Ecommerce.dao;


import com.example.Mobile.Ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User usermoi);
    List<User> findByEmail(String email);

    List<User> findAll();

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    User findOneByEmail(String email);
}
