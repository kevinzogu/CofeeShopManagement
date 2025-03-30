package al.sda.CofeeShopManagement.controller;

import al.sda.CofeeShopManagement.dto.OrderCreationDTO;
import al.sda.CofeeShopManagement.entity.OrderItem;
import al.sda.CofeeShopManagement.entity.Orders;
import al.sda.CofeeShopManagement.entity.Products;
import al.sda.CofeeShopManagement.entity.User;
import al.sda.CofeeShopManagement.service.OrderService;
import al.sda.CofeeShopManagement.service.ProductService;
import al.sda.CofeeShopManagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/barista")
public class BaristaController {
    
    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;
    
    public BaristaController(OrderService orderService, ProductService productService, UserService userService) {
        this.orderService = orderService;
        this.productService = productService;
        this.userService = userService;
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pendingOrders", orderService.getPendingOrders());
        model.addAttribute("completedOrders", orderService.getCompletedOrders());
        model.addAttribute("products", productService.getAllProducts());
        return "barista/dashboard";
    }
    
    @PostMapping("/orders/{id}/complete")
    public String completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return "redirect:/barista/dashboard";
    }
    
    // Process the order creation
    @PostMapping("/orders/new")
    public String createOrder(@ModelAttribute OrderCreationDTO orderDTO) {
        // Create a new order
        Orders newOrder = new Orders();
        newOrder.setCustomerName(orderDTO.getCustomerName());
        newOrder.setStatus("PENDING");
        newOrder.setOrderDate(LocalDate.now().toString());
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String username = user.getUsername();
            
            currentUser = userService.findByUsername(username);
        }
        
        newOrder.setUser(currentUser);
        
        // Calculate total quantity and price
        int totalQuantity = 0;
        double totalPrice = 0.0;
        
        // Create order items
        List<OrderItem> items = new ArrayList<>();
        
        for (Map.Entry<Long, OrderCreationDTO.OrderItemDTO> entry : orderDTO.getOrderItems().entrySet()) {
            OrderCreationDTO.OrderItemDTO itemDTO = entry.getValue();
            
            // Skip items with quantity 0
            if (itemDTO.getQuantity() <= 0) {
                continue;
            }
            
            // Get product from database
            Products product = productService.getProductById(itemDTO.getProductId());
            
            // Create order item
            OrderItem item = new OrderItem();
            item.setOrder(newOrder);
            item.setProductId(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(product.getPrice());
            
            items.add(item);
            
            // Update totals
            totalQuantity += itemDTO.getQuantity();
            totalPrice += product.getPrice() * itemDTO.getQuantity();
        }
        
        // Set remaining order fields
        newOrder.setQuantity(totalQuantity);
        newOrder.setTotalPrice(totalPrice);
        newOrder.setOrderItems(items);
        
        // Save order
        orderService.saveOrder(newOrder);
        
        return "redirect:/barista/dashboard";
    }
}

