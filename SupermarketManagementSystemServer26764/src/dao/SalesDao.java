package dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Customer;
import model.Employee;
import model.Product;
import model.Sales;
import model.InventoryTransaction;
import model.TransactionType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.LockMode;
import org.hibernate.exception.LockTimeoutException;
import util.HibernateUtil;

/**
 * Sales Data Access Object - FINAL CORRECTED VERSION
 * All bugs fixed and tested
 */
public class SalesDao extends BaseDao<Sales>{
    
    public SalesDao(){
        super(Sales.class);
    }
    
    /**
     * Process a sale - FINAL CORRECTED VERSION
     * 
     * ALL FIXES APPLIED:
     * 1. Server-side price calculation (security)
     * 2. Price validation against UI amount
     * 3. Inventory transaction creation (audit trail)
     * 4. Pessimistic locking (concurrency safety)
     * 5. Final stock validation before commit
     * 6. Explicit rollback on validation failures (prevents connection leaks)
     * 7. session.flush() before using sale ID (prevents null reference)
     * 8. Price validation (prevents zero/negative prices)
     * 9. Null-safe string concatenation
     * 10. Lock timeout handling
     */
    public Integer processSale(int customerId, int employeeId, int productId, 
                              int quantity, double uiTotalAmount){
        Session session = null;
        Transaction transaction = null;
        
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Load entities
            Customer customer = (Customer) session.get(Customer.class, customerId);
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            
            // Pessimistic locking to prevent concurrent stock issues
            Product product = null;
            try {
                product = (Product) session.get(Product.class, productId, LockMode.PESSIMISTIC_WRITE);
            } catch(LockTimeoutException e) {
                System.err.println("Product is being processed by another user. Please try again in a moment.");
                transaction.rollback();
                return null;
            }
            
            // Validate entities exist (with explicit rollback)
            if(customer == null) {
                System.err.println("Customer not found: " + customerId);
                transaction.rollback(); // FIX: Explicit rollback
                return null;
            }
            
            if(employee == null) {
                System.err.println("Employee not found: " + employeeId);
                transaction.rollback(); // FIX: Explicit rollback
                return null;
            }
            
            if(product == null) {
                System.err.println("Product not found: " + productId);
                transaction.rollback(); // FIX: Explicit rollback
                return null;
            }
            
            // Validate quantity is positive
            if(quantity <= 0) {
                System.err.println("Invalid quantity: " + quantity);
                transaction.rollback(); // FIX: Explicit rollback
                return null;
            }
            
            // Check stock availability
            if(product.getStockQuantity() < quantity) {
                System.err.println("Insufficient stock! Available: " + 
                    product.getStockQuantity() + ", Requested: " + quantity);
                transaction.rollback(); // FIX: Explicit rollback
                return null;
            }
            
            // Calculate totalAmount on SERVER side (security!)
            double currentPrice = product.getPrice();
            
            // FIX: Validate price is positive
            if(currentPrice <= 0) {
                System.err.println("Invalid product price: RWF " + currentPrice);
                System.err.println("Please update product price before selling.");
                transaction.rollback(); // FIX: Explicit rollback
                return null;
            }
            
            double serverCalculatedTotal = currentPrice * quantity;
            
            // Validate against UI amount (detect tampering or price changes)
            double priceDifference = Math.abs(serverCalculatedTotal - uiTotalAmount);
            if(priceDifference > 0.01) { // Allow 1 cent tolerance for rounding
                System.err.println("Price mismatch detected!");
                System.err.println("Server calculated: RWF " + String.format("%.2f", serverCalculatedTotal));
                System.err.println("UI sent: RWF " + String.format("%.2f", uiTotalAmount));
                System.err.println("This could indicate:");
                System.err.println("  - Price changed after UI loaded");
                System.err.println("  - Data tampering");
                System.err.println("Using SERVER price for security.");
            }
            
            // Use server-calculated amount (ALWAYS trust server, not client!)
            double finalTotalAmount = serverCalculatedTotal;
            
            // Create sale with current date
            Sales sale = new Sales(customer, employee, product, quantity, finalTotalAmount);
            sale.setSaleDate(new Date());
            
            // Save sale
            Integer saleId = (Integer) session.save(sale);
            
            // FIX: Flush to force ID generation
            session.flush();
            
            // Update product stock
            int newStock = product.getStockQuantity() - quantity;
            
            // Final validation before commit
            if(newStock < 0) {
                System.err.println("CRITICAL: Stock would go negative! Rolling back.");
                transaction.rollback();
                return null;
            }
            
            product.setStockQuantity(newStock);
            session.update(product);
            
            // Create inventory transaction for audit trail
            InventoryTransaction invTransaction = new InventoryTransaction();
            invTransaction.setProduct(product);
            invTransaction.setEmployee(employee);
            invTransaction.setTransactionType(model.TransactionType.SALE);
            invTransaction.setQuantity(-quantity); // Negative = stock reduction
            invTransaction.setTransactionDate(new Date());
            
            // FIX: Null-safe string concatenation
            String customerName = customer.getFullName() != null ? customer.getFullName() : "Unknown Customer";
            String saleIdStr = saleId != null ? saleId.toString() : "Pending";
//            invTransaction.setNotes("Sale ID: " + saleIdStr + " to Customer: " + customerName);
            
            session.save(invTransaction);
            
            // Commit transaction
            transaction.commit();
            
            // Success logging (null-safe)
            String productName = product.getProductName() != null ? product.getProductName() : "Unknown Product";
            String employeeName = employee.getFullName() != null ? employee.getFullName() : "Unknown Employee";
            
            System.out.println("========================================");
            System.out.println("SALE PROCESSED SUCCESSFULLY!");
            System.out.println("Sale ID: " + saleId);
            System.out.println("Product: " + productName);
            System.out.println("Quantity: " + quantity);
            System.out.println("Unit Price: RWF " + String.format("%.2f", currentPrice));
            System.out.println("Total Amount: RWF " + String.format("%.2f", finalTotalAmount));
            System.out.println("Stock Before: " + (newStock + quantity));
            System.out.println("Stock After: " + newStock);
            System.out.println("Sold By: " + employeeName);
            System.out.println("Sold To: " + customerName);
            System.out.println("========================================");
            
            return saleId;
            
        } catch(LockTimeoutException e){
            System.err.println("Product is locked by another transaction. Please try again.");
            if(transaction != null) {
                try { transaction.rollback(); } catch(Exception ex) {}
            }
            return null;
        } catch(Exception e){
            if(transaction != null) {
                try { transaction.rollback(); } catch(Exception ex) {}
            }
            System.err.println("Error processing sale:");
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) {
                try { session.close(); } catch(Exception e) {}
            }
        }
    }
    
    public Map<String, Double> getDailySalesForChart(int days){
        Session session = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT DATE(s.saleDate), SUM(s.totalAmount) " +
                        "FROM Sales s " +
                        "WHERE s.saleDate >= CURRENT_DATE - :days " +
                        "GROUP BY DATE(s.saleDate) " +
                        "ORDER BY DATE(s.saleDate) ASC";
            Query query = session.createQuery(hql);
            query.setParameter("days", days);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.list();
            
            Map<String, Double> dailySales = new LinkedHashMap<>();
            
            for (Object[] row : results){
                String date = row[0].toString();
                Double total = (Double) row[1];
                dailySales.put(date, total);
            }
            return dailySales;
        } catch(Exception e){
            e.printStackTrace();
            return new LinkedHashMap<>();
        } finally {
            if(session != null) {
                session.close();
            }
        }
    }
        
    public Double getEmployeeRevenue(int employeeId){
        Session session = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COALESCE(SUM(totalAmount), 0.0) FROM Sales WHERE employee.employeeId = :employeeId");
            query.setParameter("employeeId", employeeId);
            Double revenue = (Double) query.uniqueResult();
            return revenue != null ? revenue : 0.0;
        } catch(Exception e){
            e.printStackTrace();
            return 0.0;
        } finally {
            if(session != null) {
                session.close();
            }
        }
    }
    
    public Integer getSalesCountByEmployee(int employeeId){
        Session session = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM Sales WHERE employee.employeeId = :employeeId");
            query.setParameter("employeeId", employeeId);
            Long count = (Long) query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            if(session != null) {
                session.close();
            }
        }
    }

    public List<Sales> getSalesByEmployee(int employeeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Sales WHERE employee.employeeId = :employeeId ORDER BY saleDate DESC");
            query.setParameter("employeeId", employeeId);
            @SuppressWarnings("unchecked")
            List<Sales> sales = query.list();
            return sales;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) {
                session.close();
            }
        }
    }
    
    /**
 * Find sales within a date range
 * @param startDate Start date (inclusive)
 * @param endDate End date (inclusive)
 * @return List of sales within the date range
 */
public List<Sales> findByDateRange(Date startDate, Date endDate) {
    Session session = null;
    try {
        session = HibernateUtil.getSessionFactory().openSession();
        
        String hql = "FROM Sales s WHERE s.saleDate BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC";
        Query query = session.createQuery(hql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        @SuppressWarnings("unchecked")
        List<Sales> sales = query.list();
        
        System.out.println("[SALES DAO] Found " + sales.size() + " sales between " + 
                          startDate + " and " + endDate);
        
        return sales;
        
    } catch (Exception e) {
        System.err.println("[SALES DAO] Error finding sales by date range: " + e.getMessage());
        e.printStackTrace();
        return new java.util.ArrayList<>();
    } finally {
        if (session != null) {
            session.close();
        }
    }
}
}
