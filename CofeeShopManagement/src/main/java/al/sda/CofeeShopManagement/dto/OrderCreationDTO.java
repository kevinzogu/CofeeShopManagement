package al.sda.CofeeShopManagement.dto;

import al.sda.CofeeShopManagement.entity.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class OrderCreationDTO {
    
    private String customerName;
    private Map<Long, OrderItemDTO> orderItems = new HashMap<>();
    
    public static class OrderItemDTO {
        private Long productId;
        private int quantity;
        
        public OrderItemDTO() {
        }
        
        OrderItemDTO(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        @Override
        public String toString() {
            return "OrderItemDTO{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    '}';
        }
        
    }
    
    public OrderCreationDTO() {
    }
    
    public OrderCreationDTO(String customerName, Map<Long, OrderItemDTO> orderItems) {
        this.customerName = customerName;
        this.orderItems = orderItems;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public Map<Long, OrderItemDTO> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(Map<Long, OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
    
    @Override
    public String toString() {
        return "OrderCreationDTO{" +
                "customerName='" + customerName + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }

}
