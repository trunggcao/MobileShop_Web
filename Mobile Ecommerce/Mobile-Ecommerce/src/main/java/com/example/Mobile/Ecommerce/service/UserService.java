package com.example.Mobile.Ecommerce.service;

import com.example.Mobile.Ecommerce.dao.CartRepository;
import com.example.Mobile.Ecommerce.dao.OrderRepository;
import com.example.Mobile.Ecommerce.dao.RoleRepository;
import com.example.Mobile.Ecommerce.dao.UserRepository;
import com.example.Mobile.Ecommerce.dto.RegisterDTO;
import com.example.Mobile.Ecommerce.entity.Cart;
import com.example.Mobile.Ecommerce.entity.Order;
import com.example.Mobile.Ecommerce.entity.Role;
import com.example.Mobile.Ecommerce.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;
    private OrderRepository orderRepository;
    private CartRepository cartRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       OrderRepository orderRepository,
                       CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
        this.cartRepository =cartRepository;
    }


    public User hanldeSaveUser(User user){
        return this.userRepository.save(user);
    }
    public List<User> getAllUser(){
        return this.userRepository.findAll();
    }
    public List<User> getAllUserByEmail(String email){
        return this.userRepository.findByEmail(email);
    }
    public User findUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public Role getRoleByName(String name){
        return this.roleRepository.findByName(name);
    }


    public User registerDTOtoUSer(RegisterDTO registerDTO){
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());

        return user;
    }

    public boolean checkEmailExist(String email){
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email){
        return this.userRepository.findOneByEmail(email);
    }



    @Transactional
    public void deleteAUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        // 1. Xử lý Order
        List<Order> orders = orderRepository.findByUserId(userId);
        for (Order order : orders) {
            order.setUser(null);
        }
        orderRepository.saveAll(orders);

        // 2. Xử lý Cart (nếu không muốn mất cart thì giữ lại)
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.setUser(null); // ngắt liên kết trước khi xóa user
            cartRepository.save(cart);
        }

        // 3. Xóa user
        userRepository.delete(user);
    }


}
