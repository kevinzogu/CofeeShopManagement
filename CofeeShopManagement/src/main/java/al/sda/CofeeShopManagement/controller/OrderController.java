package al.sda.CofeeShopManagement.controller;

import al.sda.CofeeShopManagement.entity.Orders;
import al.sda.CofeeShopManagement.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping("/list")
    public String listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        
        List<Orders> orders;
        
        // Filter orders based on parameters
        if (status != null && !status.isEmpty()) {
            if (fromDate != null && toDate != null) {
                orders = orderService.findByStatusAndCreatedAtBetween(
                        status,
                        LocalDateTime.of(fromDate, LocalTime.MIN),
                        LocalDateTime.of(toDate, LocalTime.MAX));
            } else {
                orders = orderService.findByStatus(status);
            }
        } else if (fromDate != null && toDate != null) {
            orders = orderService.findByCreatedAtBetween(
                    LocalDateTime.of(fromDate, LocalTime.MIN),
                    LocalDateTime.of(toDate, LocalTime.MAX));
        } else {
            orders = orderService.findAll();
        }
        
        model.addAttribute("orders", orders);
        return "orders/list";
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
    
}
