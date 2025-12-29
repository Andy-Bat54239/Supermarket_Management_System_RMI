package util;

import dao.CustomerDao;
import dao.EmployeeDao;
import dao.ProductDao;
import dao.SalesDao;
import model.Customer;
import model.Employee;
import model.Product;
import java.util.List;

public class SalesDiagnostic {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   SALES DIAGNOSTIC TOOL");
        System.out.println("==========================================\n");
        
        CustomerDao customerDao = new CustomerDao();
        EmployeeDao employeeDao = new EmployeeDao();
        ProductDao productDao = new ProductDao();
        SalesDao salesDao = new SalesDao();
        
        // Test 1: Check Customers
        System.out.println("Test 1: Checking Customers...");
        try {
            List<Customer> customers = customerDao.findAll();
            if(customers == null || customers.isEmpty()) {
                System.out.println("  ❌ NO CUSTOMERS FOUND!");
                System.out.println("  Solution: Insert test customers with SQL");
            } else {
                System.out.println("  ✓ Found " + customers.size() + " customers");
                for(int i = 0; i < Math.min(3, customers.size()); i++) {
                    Customer c = customers.get(i);
                    System.out.println("    - ID: " + c.getCustomerId() + ", Name: " + c.getFullName());
                }
            }
        } catch(Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage());
        }
        System.out.println();
        
        // Test 2: Check Employees
        System.out.println("Test 2: Checking Employees...");
        try {
            List<Employee> employees = employeeDao.findAll();
            if(employees == null || employees.isEmpty()) {
                System.out.println("  ❌ NO EMPLOYEES FOUND!");
                System.out.println("  Solution: Insert test employees with SQL");
            } else {
                System.out.println("  ✓ Found " + employees.size() + " employees");
                for(int i = 0; i < Math.min(3, employees.size()); i++) {
                    Employee e = employees.get(i);
                    System.out.println("    - ID: " + e.getEmployeeId() + ", Name: " + e.getFullName() + ", Role: " + e.getRole());
                }
            }
        } catch(Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage());
        }
        System.out.println();
        
        // Test 3: Check Products
        System.out.println("Test 3: Checking Products...");
        try {
            List<Product> products = productDao.findAll();
            if(products == null || products.isEmpty()) {
                System.out.println("  ❌ NO PRODUCTS FOUND!");
                System.out.println("  Solution: Insert test products with SQL");
            } else {
                System.out.println("  ✓ Found " + products.size() + " products");
                for(int i = 0; i < Math.min(3, products.size()); i++) {
                    Product p = products.get(i);
                    System.out.println("    - ID: " + p.getProductId() + ", Name: " + p.getProductName() + ", Stock: " + p.getStockQuantity());
                }
            }
        } catch(Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage());
        }
        System.out.println();
        
        // Test 4: Try Processing a Sale
        System.out.println("Test 4: Testing processSale method...");
        try {
            List<Customer> customers = customerDao.findAll();
            List<Employee> employees = employeeDao.findAll();
            List<Product> products = productDao.findAll();
            
            if(customers == null || customers.isEmpty()) {
                System.out.println("  ❌ Cannot test: No customers");
            } else if(employees == null || employees.isEmpty()) {
                System.out.println("  ❌ Cannot test: No employees");
            } else if(products == null || products.isEmpty()) {
                System.out.println("  ❌ Cannot test: No products");
            } else {
                // Use first customer, employee, product
                Customer customer = customers.get(0);
                Employee employee = employees.get(0);
                Product product = products.get(0);
                
                System.out.println("  Testing with:");
                System.out.println("    Customer: " + customer.getFullName() + " (ID: " + customer.getCustomerId() + ")");
                System.out.println("    Employee: " + employee.getFullName() + " (ID: " + employee.getEmployeeId() + ")");
                System.out.println("    Product: " + product.getProductName() + " (ID: " + product.getProductId() + ")");
                System.out.println("    Current Stock: " + product.getStockQuantity());
                System.out.println("    Quantity to sell: 2");
                System.out.println("    Price: " + product.getPrice());
                
                if(product.getStockQuantity() < 2) {
                    System.out.println("\n  ❌ Insufficient stock for test!");
                    System.out.println("  Solution: Update product stock:");
                    System.out.println("  UPDATE products SET stock_quantity = 100 WHERE product_id = " + product.getProductId() + ";");
                } else {
                    double totalAmount = product.getPrice() * 2;
                    Integer saleId = salesDao.processSale(
                        customer.getCustomerId(),
                        employee.getEmployeeId(),
                        product.getProductId(),
                        2,
                        totalAmount
                    );
                    
                    if(saleId != null && saleId > 0) {
                        System.out.println("\n  ✓ SALE PROCESSED SUCCESSFULLY!");
                        System.out.println("  Sale ID: " + saleId);
                        System.out.println("  Total Amount: RWF " + String.format("%,.2f", totalAmount));
                        
                        // Check updated stock
                        Product updatedProduct = productDao.findById(product.getProductId());
                        System.out.println("  Stock after sale: " + updatedProduct.getStockQuantity());
                        System.out.println("\n  ✓✓✓ SALES PROCESSING IS WORKING! ✓✓✓");
                    } else {
                        System.out.println("\n  ❌ SALE FAILED!");
                        System.out.println("  Check server console for error details");
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n==========================================");
        System.out.println("DIAGNOSTIC COMPLETE");
        System.out.println("==========================================");
    }
}
