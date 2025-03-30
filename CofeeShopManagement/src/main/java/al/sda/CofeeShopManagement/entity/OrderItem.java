package al.sda.CofeeShopManagement.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products productId;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Column(name = "price", nullable = false)
    private double price;
    
    public OrderItem() {
    }
    
    public OrderItem(Orders order, Products productId, int quantity, double price) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Orders getOrder() {
        return order;
    }
    
    public void setOrder(Orders order) {
        this.order = order;
    }
    
    public Products getProductId() {
        return productId;
    }
    
    public void setProductId(Products product) {
        this.productId = product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", order=" + order +
                ", product=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
