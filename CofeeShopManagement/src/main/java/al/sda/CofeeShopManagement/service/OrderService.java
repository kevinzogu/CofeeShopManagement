package al.sda.CofeeShopManagement.service;

import al.sda.CofeeShopManagement.entity.OrderItem;
import al.sda.CofeeShopManagement.entity.Orders;
import al.sda.CofeeShopManagement.entity.Products;
import al.sda.CofeeShopManagement.repository.OrderItemRepository;
import al.sda.CofeeShopManagement.repository.OrderRepository;
import al.sda.CofeeShopManagement.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }
    
    // ✅ Get Pending Orders
    public List<Orders> getPendingOrders() {
        return orderRepository.findByStatus("Pending");
    }
    
    // ✅ Get Completed Orders
    public List<Orders> getCompletedOrders() {
        return orderRepository.findByStatus("Completed");
    }
    
    // ✅ Mark an Order as Completed
    public void completeOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("Completed");
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    // ✅ Process the Order Form
    public List<OrderItem> processOrderForm(HttpServletRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (Products product : productRepository.findAll()) {
            String paramName = "product_" + product.getId();
            String quantityStr = request.getParameter(paramName);
            
            if (quantityStr != null && !quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity > 0 && quantity <= product.getQuantity()) {
                    OrderItem item = new OrderItem();
                    item.setProductId(product);
                    item.setQuantity(quantity);
                    item.setPrice(product.getPrice() * quantity);
                    orderItems.add(item);
                }
            }
        }
        return orderItems;
    }
    
    // ✅ Create a New Order
    @Transactional
    public void createOrder(String customerName, List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("No items selected for the order.");
        }
        
        Orders order = new Orders();
        order.setCustomerName(customerName);
        order.setTotalPrice(orderItems.stream().mapToDouble(OrderItem::getPrice).sum());
        order.setOrderDate(LocalDateTime.now().toString());
        order.setStatus("Pending");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(orderItems);
        
        orderRepository.save(order);
        
        for (OrderItem item : orderItems) {
            item.setOrder(order);
            orderItemRepository.save(item);
            
            // Reduce product quantity in stock
            Products product = item.getProductId();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }
    }
    
    public void saveOrder(Orders newOrder) {
        orderRepository.save(newOrder);
    }
    
    public List<Orders> findByStatusAndCreatedAtBetween(String status, LocalDateTime of, LocalDateTime of1) {
        return orderRepository.findByStatusAndCreatedAtBetween(status, of, of1);
    }
    
    public List<Orders> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Orders> findByCreatedAtBetween(LocalDateTime of, LocalDateTime of1) {
        return orderRepository.findByCreatedAtBetween(of, of1);
    }
    
    public List<Orders> findAll() {
            return orderRepository.findAll();
    }
    
    public Optional<Orders> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    public void save(Orders order) {
        orderRepository.save(order);
    }
}
