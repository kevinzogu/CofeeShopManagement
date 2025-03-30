package al.sda.CofeeShopManagement.service;

import al.sda.CofeeShopManagement.entity.Products;
import al.sda.CofeeShopManagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }
    
    public void saveProduct(Products product) {
        productRepository.save(product);
    }
    
    public Products getProductById(Long id) {
        return productRepository.findById(id).orElse(new Products());
    }
    
    public void updateProduct(Long id, Products product) {
        Products oldProduct = getProductById(id);
        if (oldProduct != null) {
            oldProduct.setName(product.getName());
            oldProduct.setPrice(product.getPrice());
            oldProduct.setQuantity(product.getQuantity());
            oldProduct.setCategory(product.getCategory());
            productRepository.save(oldProduct);
        }
    }
}
