package al.sda.CofeeShopManagement.controller;

import al.sda.CofeeShopManagement.dto.OrderCreationDTO;
import al.sda.CofeeShopManagement.entity.OrderItem;
import al.sda.CofeeShopManagement.entity.Orders;
import al.sda.CofeeShopManagement.entity.Products;
import al.sda.CofeeShopManagement.entity.User;
import al.sda.CofeeShopManagement.enums.Roles;
import al.sda.CofeeShopManagement.service.OrderService;
import al.sda.CofeeShopManagement.service.ProductService;
import al.sda.CofeeShopManagement.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    
    public OrderController(OrderService orderService, UserService userService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pendingOrders", orderService.getPendingOrders());
        model.addAttribute("preparingOrders",orderService.getPreparingOrders());
        model.addAttribute("readyOrders",orderService.getReadyOrders());
        model.addAttribute("completedOrders", orderService.getCompletedOrders());
        model.addAttribute("canceledOrders",orderService.getCanceledOrders());
        model.addAttribute("products", productService.getAllProducts());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                
                String role = user.getAuthorities().iterator().next().getAuthority();
                
                String userRole = role.equals(Roles.MANAGER.getRoleName()) ? "manager" : "bartender";
                
                model.addAttribute("userRole", userRole);
            }
        }

        return "orders/dashboard";
    }
    
    /**
     * View order details
     */
    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Optional<Orders> orderOpt = orderService.findById(id);
        
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
            return "orders/order-details";
        } else {
            return "redirect:/manager/orders";
        }
    }
    
    
    /**
     * Update order status
     */
    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        
        Optional<Orders> orderOpt = orderService.findById(id);
        
        if (orderOpt.isPresent()) {
            Orders order = orderOpt.get();
            order.setStatus(status);
            
            if (status.equals("completed")) {
                order.setCompletedAt(LocalDateTime.now());
            }
            
            orderService.save(order);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Order status updated successfully");
        }
        
        return "redirect:/orders/view/" + id;
    }
    
    // Process the order creation
    @PostMapping("/create/new")
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
        
        return "redirect:/orders/dashboard";
    }
    
}
