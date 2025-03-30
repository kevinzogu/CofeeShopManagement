package al.sda.CofeeShopManagement.controller;

import al.sda.CofeeShopManagement.entity.*;
import al.sda.CofeeShopManagement.enums.Roles;
import al.sda.CofeeShopManagement.repository.CategoryRepository;
import al.sda.CofeeShopManagement.repository.OrderRepository;
import al.sda.CofeeShopManagement.repository.ProductRepository;
import al.sda.CofeeShopManagement.repository.UserRepository;
import al.sda.CofeeShopManagement.service.ReportService;
import al.sda.CofeeShopManagement.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReportService reportService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    public ManagerController(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, CategoryRepository categoryRepository, ReportService reportService, UserService userService, PasswordEncoder passwordEncoder) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.reportService = reportService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Display the manager dashboard
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        // Calculate today's start and end for filtering
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        // Get counts for dashboard stats
        long todayOrdersCount = orderRepository.countByCreatedAtBetween(todayStart, todayEnd);
        long activeUsersCount = userRepository.countByLastLoginAfter(todayStart.minusDays(7));
        long productsCount = productRepository.count();
        long categoriesCount = categoryRepository.count();
        
        // Get recent orders
        List<Orders> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc();
        
        // Get top selling products.html
        List<Products> topProducts = reportService.getTopSellingProducts(5);
        
        // Add attributes to model
        model.addAttribute("todayOrdersCount", todayOrdersCount);
        model.addAttribute("activeUsersCount", activeUsersCount);
        model.addAttribute("productsCount", productsCount);
        model.addAttribute("categoriesCount", categoriesCount);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("topProducts", topProducts);
        
        return "manager/dashboard";
    }
    
    /**
     * Display all users
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("baristas", users.stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.equals(Roles.BARISTA.getRoleName())))
                .toList());
        return "manager/users";
    }
    
    /**
     * Create a new user
     */
    @PostMapping("/baristas/create")
    public String createBarista(@ModelAttribute User user,
                                RedirectAttributes redirectAttributes) {
        user.setRoles(List.of(Roles.BARISTA.getRoleName()));
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "Barista created successfully");
        return "redirect:/manager/users";
    }
    
    @PostMapping("/baristas/update")
    public String updateBarista(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        user.setRoles(List.of(Roles.BARISTA.getRoleName()));
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("message", "Barista updated successfully");
        return "redirect:/manager/users";
    }
    
    /**
     * Delete a user
     */
    @PostMapping("/baristas/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/manager/users";
    }
    
    

   
    /**
     * Display products.html
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Products> products = productRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "manager/products";
    }
    
    /**
     * Create a new product
     */
    @PostMapping("/products/create")
    public String createProduct(@ModelAttribute Products product, RedirectAttributes redirectAttributes) {
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product " + product.getName() + " created successfully");
        return "redirect:/manager/products";
    }
    
    /**
     * Update a product
     */
    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Products product,
            RedirectAttributes redirectAttributes) {
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product updated successfully");
        return "redirect:/manager/products";
    }
    
    /**
     * Delete a product
     */
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully");
        return "redirect:/manager/products";
    }
    
    /**
     * Display categories
     */
    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "manager/categories";
    }
    
    /**
     * Create a new category
     */
    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage",
                "Category " + category.getName() + " created successfully");
        return "redirect:/manager/categories";
    }
    
    /**
     * Update a category
     */
    @PostMapping("/categories/update")
    public String updateCategory(
            @ModelAttribute Category category,
            RedirectAttributes redirectAttributes) {
        
        categoryRepository.save(category);
        
        redirectAttributes.addFlashAttribute("successMessage",
                "Category updated successfully");
        return "redirect:/manager/categories";
    }
    
    
    /**
     * Delete a category
     */
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Check if category is used by any products.html
        long productsWithCategory = productRepository.countByCategoryId(id);
        
        if (productsWithCategory > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete category. It is used by " + productsWithCategory + " products.html.");
        } else {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        }
        
        return "redirect:/manager/categories";
    }
    
    /**
     * Display reports.html page
     */
    @GetMapping("/reports")
    public String showReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        
        // Default to last 30 days if dates not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().minusDays(30);
        }
        
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        
        // Get sales data
        Map<String, Object> salesData = reportService.getSalesReport(fromDate, toDate);
        
        // Get product data
        List<Map<String, Object>> productSalesData = reportService.getProductSalesReport(fromDate, toDate);
        
        // Get category data
        List<Map<String, Object>> categorySalesData = reportService.getCategorySalesReport(fromDate, toDate);
        
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("salesData", salesData);
        model.addAttribute("productSalesData", productSalesData);
        model.addAttribute("categorySalesData", categorySalesData);
        
        return "manager/reports";
    }
}