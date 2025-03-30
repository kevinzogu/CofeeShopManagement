package al.sda.CofeeShopManagement.service;

import al.sda.CofeeShopManagement.entity.Products;
import al.sda.CofeeShopManagement.repository.OrderRepository;
import al.sda.CofeeShopManagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    public ReportService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }
    
    /**
     * Get top selling products.html
     *
     * @param limit Number of products.html to return
     * @return List of top selling products.html with sales count
     */
    public List<Products> getTopSellingProducts(int limit) {
        // Get products.html sold in the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> results = productRepository.findTopSellingProducts(thirtyDaysAgo, LocalDateTime.now());
        
        List<Products> topProducts = new ArrayList<>();
        
        for (int i = 0; i < Math.min(limit, results.size()); i++) {
            Object[] result = results.get(i);
            Products product = (Products) result[0];
            Long soldCount = (Long) result[1];
            
            // Add sold count as a transient property or via a wrapper object
            product.setSoldCount(soldCount.intValue());
            topProducts.add(product);
        }
        
        return topProducts;
    }
    
    /**
     * Generate sales report for a date range
     *
     * @param fromDate Start date
     * @param toDate End date
     * @return Map containing sales data
     */
    public Map<String, Object> getSalesReport(LocalDate fromDate, LocalDate toDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime start = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(toDate, LocalTime.MAX);
        
        // Use a native query for complex aggregations
        // This would typically call a repository method that runs SQL like:
        // SELECT
        //   DATE(created_at) as date,
        //   COUNT(*) as order_count,
        //   SUM(total_amount) as daily_total
        // FROM orders
        // WHERE created_at BETWEEN :start AND :end
        // GROUP BY DATE(created_at)
        // ORDER BY date
        
        // For demonstration, we'll mock the data
        List<Map<String, Object>> dailySales = new ArrayList<>();
        double totalSales = 0;
        int totalOrders = 0;
        
        // Generate sample data for each day in the range
        LocalDate current = fromDate;
        while (!current.isAfter(toDate)) {
            Map<String, Object> daySales = new HashMap<>();
            double dailyTotal = 100 + Math.random() * 500; // Random value between 100-600
            int orderCount = 5 + (int)(Math.random() * 20); // Random between 5-25
            
            daySales.put("date", current.format(DateTimeFormatter.ISO_DATE));
            daySales.put("orderCount", orderCount);
            daySales.put("total", dailyTotal);
            
            dailySales.add(daySales);
            totalSales += dailyTotal;
            totalOrders += orderCount;
            
            current = current.plusDays(1);
        }
        
        result.put("dailySales", dailySales);
        result.put("totalSales", totalSales);
        result.put("totalOrders", totalOrders);
        result.put("averageOrderValue", totalOrders > 0 ? totalSales / totalOrders : 0);
        
        return result;
    }
    
    /**
     * Generate product sales report for a date range
     *
     * @param fromDate Start date
     * @param toDate End date
     * @return List of maps containing product sales data
     */
    public List<Map<String, Object>> getProductSalesReport(LocalDate fromDate, LocalDate toDate) {
        // Similar to above, would use repository for real data
        // Mocking data for demonstration
        
        List<Map<String, Object>> result = new ArrayList<>();
        String[] productNames = {"Espresso", "Cappuccino", "Latte", "Americano", "Mocha", "Croissant", "Muffin"};
        
        for (String product : productNames) {
            Map<String, Object> productData = new HashMap<>();
            int quantity = 10 + (int)(Math.random() * 100);
            double revenue = quantity * (4 + Math.random() * 3);
            
            productData.put("name", product);
            productData.put("quantity", quantity);
            productData.put("revenue", revenue);
            
            result.add(productData);
        }
        
        // Sort by revenue descending
        result.sort((a, b) -> Double.compare((Double)b.get("revenue"), (Double)a.get("revenue")));
        
        return result;
    }
    
    /**
     * Generate category sales report for a date range
     *
     * @param fromDate Start date
     * @param toDate End date
     * @return List of maps containing category sales data
     */
    public List<Map<String, Object>> getCategorySalesReport(LocalDate fromDate, LocalDate toDate) {
        // Would use repository for real data
        // Mocking data for demonstration
        
        List<Map<String, Object>> result = new ArrayList<>();
        String[] categoryNames = {"Hot Coffee", "Cold Coffee", "Tea", "Pastries", "Sandwiches"};
        
        for (String category : categoryNames) {
            Map<String, Object> categoryData = new HashMap<>();
            int quantity = 20 + (int)(Math.random() * 200);
            double revenue = quantity * (5 + Math.random() * 5);
            
            categoryData.put("name", category);
            categoryData.put("quantity", quantity);
            categoryData.put("revenue", revenue);
            
            result.add(categoryData);
        }
        
        // Sort by revenue descending
        result.sort((a, b) -> Double.compare((Double)b.get("revenue"), (Double)a.get("revenue")));
        
        return result;
    }
}
