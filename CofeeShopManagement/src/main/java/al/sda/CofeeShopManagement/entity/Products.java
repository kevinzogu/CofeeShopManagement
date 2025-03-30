package al.sda.CofeeShopManagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Products {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;
    @Column(name = "product_name", nullable = false)
    private String name;
    @Column(name = "product_price", nullable = false)
    private double price;
    @Column(name = "product_quantity", nullable = false)
    private int quantity;
    @Column(name = "sold_count")
    private int soldCount;
    @Column(name = "product_image")
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    public Products() {
    }
    
    public Products(Long id, String name, double price, int quantity, int soldCount,String imageUrl,Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.soldCount = soldCount;
        this.imageUrl = imageUrl;
        this.category = category;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int getSoldCount() {
        return soldCount;
    }
    
    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    @Override
    public String toString() {
        return "Products{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", soldCount=" + soldCount +
                ", imageUrl='" + imageUrl + '\'' +
                ", category=" + category +
                '}';
    }
}
