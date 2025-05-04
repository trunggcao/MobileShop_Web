package com.example.Mobile.Ecommerce.controller;

import com.example.Mobile.Ecommerce.dao.ProductRepository;
import com.example.Mobile.Ecommerce.dao.UserRepository;
import com.example.Mobile.Ecommerce.entity.Category;
import com.example.Mobile.Ecommerce.entity.Order;
import com.example.Mobile.Ecommerce.entity.Product;
import com.example.Mobile.Ecommerce.entity.User;
import com.example.Mobile.Ecommerce.service.*;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AdminController {
    private UserService userService;
    private UploadService uploadService;
    private CategoryService categoryService;
    private PasswordEncoder passwordEncoder;
    private ProductService productService;
    private OrderService orderService;

    public AdminController(UserService userService,
                           UploadService uploadService,
                           PasswordEncoder passwordEncoder,
                           CategoryService categoryService,
                           ProductService productService,
                           OrderService orderService) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
        this.categoryService = categoryService;
        this.productService =productService;
        this.orderService =orderService;
    }

    @GetMapping("/admin")
    public String getDashBoard(Model model) {
        model.addAttribute("countUser",productService.countUser());
        model.addAttribute("countProduct",productService.countProduct());
        model.addAttribute("countCategory",productService.countCategory());
        model.addAttribute("countOrder",productService.countOrder());
        return "admin/dashboard";
    }
    // get table user
    @GetMapping("/admin/users")
    public String getListUser(Model model) {
        List<User> users = this.userService.getAllUser();
        model.addAttribute("users", users);
        System.out.println("check" +users);
        return "admin/table-user";
    }

    @GetMapping("/admin/users/{id}")
    public String getUserDetail(Model model, @PathVariable Long id) {
        User user = this.userService.findUserById(id);
        System.out.println("id:" + id);
        System.out.println("user:" + user.getFullName());
        model.addAttribute("id", id);
        model.addAttribute("user",user);
        return "admin/user-detail";
    }

    //create user form
    @GetMapping("/admin/user/create")
    public String getAdminUser(Model model) {
        model.addAttribute("user", new User());
        return "admin/createUser";
    }
    @PostMapping("/admin/user/create")
    public String getUserPage(Model model, @ModelAttribute("user") @Valid User usermoi,
                              BindingResult bindingResult,
                              @RequestParam("uploadFile")MultipartFile file){

        if (bindingResult.hasErrors()){
            return "admin/createUser";
        }


        System.out.println("Received file: " + file.getOriginalFilename());
        String avatarPath = uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(usermoi.getPassword());

        usermoi.setAvatar(avatarPath);
        usermoi.setPassword(hashPassword);

        usermoi.setRole(this.userService.getRoleByName(usermoi.getRole().getName()));
        this.userService.hanldeSaveUser(usermoi);
        return "redirect:/admin/users";
    }

    //uddate user
    @GetMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model,@PathVariable Long id) {
        User currentuser = this.userService.findUserById(id);
        model.addAttribute("newUser", currentuser);
        return "admin/update-user";
    }
    @PostMapping("/admin/user/update")
    public String postUpdateUser(Model model, @ModelAttribute("newUser") User newUser,
                                 @RequestParam("uploadFile")MultipartFile file) {
        System.out.println(this.userService.findUserById(newUser.getId()));
        User currentuser = this.userService.findUserById(newUser.getId());
        String avatarPath = uploadService.handleSaveUploadFile(file, "avatar");

        if (currentuser !=null){
            currentuser.setAddress(newUser.getAddress());
            currentuser.setFullName(newUser.getFullName());
            currentuser.setPhone(newUser.getPhone());
            if (!avatarPath.isEmpty()){
                currentuser.setAvatar(avatarPath);
            }
            this.userService.hanldeSaveUser(currentuser);
        }
        return "redirect:/admin/users";
    }


    //delete

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        User user = new User();
        user.setId(id);
        model.addAttribute("newUser", user);
        return "admin/delete-user";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("newUser") User user) {
        this.userService.deleteAUser(user.getId());
        return "redirect:/admin/users";
    }


    /////  Category


    @GetMapping("/admin/category")
    public String getListCategory(Model model) {
        List<Category> categories = this.categoryService.getAllCategory();
        model.addAttribute("categories", categories);
        System.out.println("check" + categories);
        return "admin/category";
    }

    @GetMapping("/admin/category/{id}")
    public String getCategoryDetail(Model model, @PathVariable Long id) {
        Category category = this.categoryService.findCategoryById(id);
        System.out.println("id:" + id);
        System.out.println("name:" + category.getName());
        model.addAttribute("id", id);
        model.addAttribute("category",category);
        return "admin/category-detail";
    }
    // them category
    @GetMapping("/admin/category/create")
    public String getCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/create-category";
    }
    @PostMapping("/admin/category/create")
    public String createCategory(Model model,
                                 @ModelAttribute("category") @Valid Category category,
                                 BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "admin/create-category";
        }

        this.categoryService.handleSaveCategory(category);
        return "redirect:/admin/category";
    }
    // update category
    @GetMapping("/admin/category/update/{id}")
    public String getUpdateCategory(Model model,@PathVariable Long id) {
        Category currentCategory = this.categoryService.findCategoryById(id);
        model.addAttribute("newCategory", currentCategory);
        return "admin/update-category";
    }
    @PostMapping("/admin/category/update")
    public String updateCategory(Model model, @ModelAttribute("newCategory") Category newCategory) {
        Category currentCategory = this.categoryService.findCategoryById(newCategory.getId());
        if (currentCategory !=null){
            currentCategory.setName(newCategory.getName());
            this.categoryService.handleSaveCategory(currentCategory);
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/admin/category/delete/{id}")
    public String getDeleteCategory(Model model, @PathVariable long id) {

        Category category = new Category();
        category.setId(id);
        model.addAttribute("newCategory", category);
        return "admin/delete-category";
    }

    @PostMapping("/admin/category/delete")
    public String postDeleteCategory(Model model, @ModelAttribute("newCategory") Category category) {
        this.categoryService.deleteCategory(category.getId());
        return "redirect:/admin/category";
    }





    // prodcuts

    @GetMapping("/admin/products")
    public String getListProduct(Model model){
        List<Product> products = this.productService.getAllProduct();
        model.addAttribute("products", products);
        System.out.println("check" + products);
        return "/admin/products";
    }
    // them san pham
    @GetMapping("/admin/products/create")
    public String getCreateProduct(Model model) {
        model.addAttribute("product", new Product());
        return "admin/create-product";
    }
    @PostMapping("/admin/products/create")
    public String createProduct(Model model,
                                @ModelAttribute("product") @Valid Product product,
                                BindingResult bindingResult,
                                @RequestParam("uploadFile")MultipartFile file){
        if (bindingResult.hasErrors()){
            return "admin/create-product";
        }

        System.out.println("Received file: " + file.getOriginalFilename());
        String avatarPath = uploadService.handleSaveUploadFile(file, "product");
        product.setImage(avatarPath);
        this.productService.hanldeSaveProduct(product);
        return "redirect:/admin/products";
    }
    // update san pham
    @GetMapping("/admin/products/update/{id}")
    public String getUpdateProduct(Model model,@PathVariable Long id) {
        Product currentProduct = this.productService.findById(id);
        model.addAttribute("newProduct", currentProduct);
        return "admin/update-product";
    }
    @PostMapping("/admin/products/update")
    public String updateProduct(Model model, @ModelAttribute("newProduct") Product newProduct,
                                @RequestParam("uploadFile")MultipartFile file) {

        Product  currentProduct = this.productService.findById(newProduct.getId());
        String avatarPath = uploadService.handleSaveUploadFile(file, "product");
        if (currentProduct !=null){
            currentProduct.setName(newProduct.getName());
            currentProduct.setPrice(newProduct.getPrice());
            currentProduct.setDetailDesc(newProduct.getDetailDesc());
            currentProduct.setShortDesc(newProduct.getShortDesc());
            currentProduct.setQuantity(newProduct.getQuantity());
            currentProduct.setFactory(newProduct.getFactory());
            currentProduct.setTarget(newProduct.getTarget());
            currentProduct.setCategory(newProduct.getCategory());
            if (!avatarPath.isEmpty()){
                currentProduct.setImage(avatarPath);
            }
            this.productService.hanldeSaveProduct(currentProduct);
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/{id}")
    public String getProductDetail(Model model, @PathVariable Long id) {
        Product product = this.productService.findById(id);

        model.addAttribute("id", id);
        model.addAttribute("product",product);
        return "admin/product-detail";
    }

    //deleteproduct

    @GetMapping("/admin/products/delete/{id}")
    public String getDeleteProduct(Model model, @PathVariable long id) {

        Product product = new Product();
        product.setId(id);
        model.addAttribute("newProduct", product);
        return "admin/delete-product";
    }

    @PostMapping("/admin/products/delete")
    public String postDeleteProduct(Model model, @ModelAttribute("newProduct") Product product) {
        this.productService.deleteProduct(product.getId());
        return "redirect:/admin/products";
    }


    // orders

    @GetMapping("/admin/orders")
    public String getLstOrder(Model model){
        List<Order> orders = this.orderService.getAllOrder();
        model.addAttribute("orders", orders);
        return "/admin/orders";
    }

    @GetMapping("/admin/orders/{id}")
    public String getOrderDetail(Model model, @PathVariable Long id) {
        Order order = this.orderService.findById(id);

        model.addAttribute("id", id);
        model.addAttribute("order",order);
        return "admin/order-detail";
    }

    @GetMapping("/admin/orders/update/{id}")
    public String getUpdateOrder(Model model,@PathVariable Long id) {
        Order currentOrder = this.orderService.findById(id);
        model.addAttribute("newOrder", currentOrder);
        return "admin/update-order";
    }

    @PostMapping("/admin/orders/update")
    public String updateOrder(Model model, @ModelAttribute("newOrder") Order newOrder) {

        Order currentOrder = this.orderService.findById(newOrder.getId());
        if (currentOrder !=null){
            currentOrder.setStatus(newOrder.getStatus());
            this.orderService.handleSaveOrder(currentOrder);
        }
        return "redirect:/admin/orders";
    }
}
