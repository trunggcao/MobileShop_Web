package com.example.Mobile.Ecommerce.controller;

import com.example.Mobile.Ecommerce.dto.RegisterDTO;
import com.example.Mobile.Ecommerce.entity.*;
import com.example.Mobile.Ecommerce.service.OrderService;
import com.example.Mobile.Ecommerce.service.ProductService;
import com.example.Mobile.Ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private ProductService productService;
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private OrderService orderService;

    public HomeController(ProductService productService,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          OrderService orderService) {
        this.productService = productService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.orderService =orderService;
    }

    @GetMapping("/")
    public String getHomePage(Model model){
        List<Product> products = this.productService.getAllProduct();
        model.addAttribute("products", products);
        return "user/home-page";
    }
    @GetMapping("/product/{id}")
    public String getHomePage(Model model, @PathVariable Long id){

        Product product = this.productService.findById(id);
        model.addAttribute("product", product);
        return "user/product-detail";
    }




    // register
    @GetMapping("/register")
    public String getRegister(Model model){
        model.addAttribute("registerUser", new RegisterDTO());
        return "user/auth/register";
    }

    @PostMapping("/register")
    public String hanldeRegister(@ModelAttribute("registerUser") @Valid RegisterDTO registerDTO,
                                 BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return "user/auth/register";
        }

        User user = this.userService.registerDTOtoUSer(registerDTO);

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setRole(this.userService.getRoleByName("USER"));

        this.userService.hanldeSaveUser(user);
        return "redirect:login";
    }

    @GetMapping("/login")
    public String getLogin(Model model){
        return "user/auth/login";
    }

    @GetMapping("/access-deny")
    public String getDenyPage(){
        return "user/auth/deny";
    }



    // add product to cart
    @PostMapping("/add-product-to-cart/{id}")
    public String addProductToCart(@PathVariable Long id,HttpServletRequest request){
        Long productId =id;
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        this.productService.hanldeAddProductToCart(email,productId,session);
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request){
        User currentuser = new User();
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("id");
        currentuser.setId(id);

        Cart cart = this.productService.fetchByUser(currentuser);

        List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>()  : cart.getCartDetails();

        double totalPrice = 0;
        for (CartDetail cd : cartDetails){
            totalPrice += cd.getPrice() * cd.getQuantity();
        }
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);

        model.addAttribute("cart", cart);


        return "user/cart";
    }

    @PostMapping("/delete-cart-product/{id}")
    public String deleteCartDetail(@PathVariable Long id, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        Long cartDetailId = id;
        this.productService.hanldeRemoveCartDetail(cartDetailId, session);
        return "redirect:/cart";
    }



    //check out



    @GetMapping("/checkout")
    public String getCheckOutPage(Model model, HttpServletRequest request) {
        User currentUser = new User();// null
        HttpSession session = request.getSession(false);
        long id = (long) session.getAttribute("id");
        currentUser.setId(id);

        Cart cart = this.productService.fetchByUser(currentUser);

        List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();

        double totalPrice = 0;
        for (CartDetail cd : cartDetails) {
            totalPrice += cd.getPrice() * cd.getQuantity();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);

        return "user/checkout";
    }

    @PostMapping("/confirm-checkout")
    public String getCheckOutPage(@ModelAttribute("cart") Cart cart){
        List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();
        this.productService.handleUpdateCartBeforeCheckout(cartDetails);
        return "redirect:/checkout";
    }

    @PostMapping("/place-order")
    public String handlePlaceOrder(
            HttpServletRequest request,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone) {
        User currentUser = new User();// null
        HttpSession session = request.getSession(false);
        long id = (long) session.getAttribute("id");
        currentUser.setId(id);

        this.productService.handlePlaceOrder(currentUser,session,receiverName,receiverAddress,receiverPhone);

        return "user/thanks";
    }


    //order history

    @GetMapping("/order-history")
    public String getOrderHistoryPage(Model model, HttpServletRequest request) {
        User currentUser = new User();// null
        HttpSession session = request.getSession(false);
        long id = (long) session.getAttribute("id");
        currentUser.setId(id);

        List<Order> orders = this.orderService.fetchOrderByUser(currentUser);
        model.addAttribute("orders", orders);

        return "user/order-history";
    }
    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> searchResults = productService.searchByName(keyword);
        model.addAttribute("products", searchResults);
        model.addAttribute("keyword", keyword);
        return "user/product-list";
    }
    @GetMapping("/search/category/{id}")
    public String searchProductsByCategory(@PathVariable Long id, Model model) {
        List<Product> searchResults = productService.findByCategoryId(id);
        model.addAttribute("products", searchResults);
        return "user/product-list";
    }


}
