package com.example.Mobile.Ecommerce.service;

import com.example.Mobile.Ecommerce.dao.*;
import com.example.Mobile.Ecommerce.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private CartDetailRepository cartDetailRepository;
    private UserService userService;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private OrderDetailRepository orderDetailRepository;
    private CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CartRepository cartRepository,
                          CartDetailRepository cartDetailRepository,
                          UserService userService,
                          UserRepository userRepository,
                          OrderRepository orderRepository,
                          OrderDetailRepository orderDetailRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.userRepository =userRepository;
        this.orderRepository= orderRepository;
        this.orderDetailRepository =orderDetailRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void deleteProduct(Long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = optionalProduct.get();


        List<OrderDetail> orderDetails = orderDetailRepository.findByProductId(id);
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setProduct(null);
        }
        orderDetailRepository.saveAll(orderDetails);


//        List<CartDetail> cartDetails = cartDetailRepository.findByProductId(id);
//
//        for (CartDetail cartDetail : cartDetails) {
//            cartDetail.setProduct(null);
//        }
        cartDetailRepository.deleteAllByProductId(id);



        productRepository.delete(product);

    }

    public List<Product> searchByName(String keyword){
        return this.productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> findByCategoryId(Long id){
        return this.productRepository.findByCategoryId(id);
    }


    public Product hanldeSaveProduct(Product product){
        return this.productRepository.save(product);
    }

    public Product findById(Long id){
        return this.productRepository.findById(id).orElse(null);
    }
    public List<Product> getAllProduct(){
        return this.productRepository.findAll();
    }

    public void hanldeAddProductToCart(String email, Long productId, HttpSession session){
        User user = this.userService.getUserByEmail(email);
        if (user != null){
            Cart cart = this.cartRepository.findByUser(user);

            if (cart == null){
                // create new cart
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);

                 cart = this.cartRepository.save(otherCart);
            }
            // save cart
            // find product by id

            Optional<Product> productOptional = this.productRepository.findById(productId);
            if (productOptional.isPresent()){

                Product realProduct =productOptional.get();

                // check product exists in cart
                CartDetail preCartDetail = this.cartDetailRepository.findByCartAndProduct(cart,realProduct);

                if (preCartDetail == null) {
                    CartDetail cd = new CartDetail();
                    cd.setCart(cart);
                    cd.setProduct(realProduct);
                    cd.setPrice(realProduct.getPrice());
                    cd.setQuantity(1);
                    this.cartDetailRepository.save(cd);

                    //update cart sum
                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                }else {
                    preCartDetail.setQuantity(preCartDetail.getQuantity() + 1);
                    this.cartDetailRepository.save(preCartDetail);
                }
            }


        }
    }

    public Cart fetchByUser(User user){
        return this.cartRepository.findByUser(user);
    }

    public void hanldeRemoveCartDetail(Long cartDetailId, HttpSession session){
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();

            Cart currentCart = cartDetail.getCart();
            // delete cart-detail
            this.cartDetailRepository.deleteById(cartDetailId);

            // update cart
            if (currentCart.getSum() > 0) {
                // update current cart
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(currentCart);
            } else {
                // delete cart (sum = 1)
                this.cartRepository.deleteById(currentCart.getId());
               //
                session.setAttribute("sum", 0);
            }
        }

    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    //order
    @Transactional
    public void handlePlaceOrder(User user,HttpSession session,String receiverName,
                                 String receiverAddress, String receiverPhone) {
        Cart cart = this.cartRepository.findByUser(user);
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();

            if (cartDetails != null) {

                // create order
                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhone);
                order.setStatus("PENDING");

                double sum = 0;
                for (CartDetail cd : cartDetails) {
                    sum += cd.getPrice();
                }
                order.setTotalPrice(sum);
                order = this.orderRepository.save(order);

                // create orderDetail

                for (CartDetail cd : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetail.setPrice(cd.getPrice());
                    orderDetail.setQuantity(cd.getQuantity());

                    orderDetail = this.orderDetailRepository.save(orderDetail);

                    Product product = cd.getProduct();
                    Long soldProduct = orderDetail.getQuantity();

                    product.setSold(product.getSold() + soldProduct); // tăng sold
                    product.setQuantity(product.getQuantity() - soldProduct); // giảm tồn kho

                    this.productRepository.save(product);
                }


                for (CartDetail cd : cartDetails) {
                    this.cartDetailRepository.deleteById(cd.getId());
                }

            //    this.cartRepository.deleteById(cart.getId());

                // step 3 : update session
                cart.setSum(0);
                session.setAttribute("sum", 0);
            }
        }

    }

    public long countUser(){
        return this.userRepository.count();
    }
    public long countProduct(){
        return this.productRepository.count();
    }
    public long countCategory(){
        return this.categoryRepository.count();
    }
    public long countOrder(){
        return this.orderRepository.count();
    }






}
