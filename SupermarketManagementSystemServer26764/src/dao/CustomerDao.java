package dao;

import java.util.List;
import model.Customer;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 * Customer Data Access Object
 */
public class CustomerDao extends BaseDao<Customer>{
    public CustomerDao(){
        super(Customer.class);
    }
    
    public List<Integer> getAllCustomerIds(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT cust.customerId FROM Customer cust");
            List<Integer> ids = query.list();
            ss.close();
            return ids;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Delete customer with validation
     * Checks if customer has related sales records before deleting
     * @param customerId Customer ID to delete
     * @return true if deleted successfully, false if has dependencies
     */
    public boolean deleteCustomer(int customerId) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Load customer
            Customer customer = (Customer) session.get(Customer.class, customerId);
            
            if (customer == null) {
                System.err.println("Customer not found with ID: " + customerId);
                return false;
            }
            
            // Check if customer has sales records
            Query salesCheck = session.createQuery("SELECT COUNT(*) FROM Sales WHERE customer.customerId = :custId");
            salesCheck.setParameter("custId", customerId);
            Long salesCount = (Long) salesCheck.uniqueResult();
            
            if (salesCount > 0) {
                System.err.println("Cannot delete customer: Customer has " + salesCount + " sales records.");
                System.err.println("Customer: " + customer.getFullName() + " (ID: " + customerId + ")");
                return false;
            }
            
            // Safe to delete
            session.delete(customer);
            transaction.commit();
            
            System.out.println("Customer deleted successfully: " + customer.getFullName());
            return true;
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting customer:");
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Check if customer can be safely deleted
     * @param customerId Customer ID
     * @return true if has no dependencies, false otherwise
     */
    public boolean canDeleteCustomer(int customerId) {
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Check sales
            Query salesCheck = session.createQuery("SELECT COUNT(*) FROM Sales WHERE customer.customerId = :custId");
            salesCheck.setParameter("custId", customerId);
            Long salesCount = (Long) salesCheck.uniqueResult();
            
            return salesCount == 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Get count of sales by customer
     * @param customerId Customer ID
     * @return Number of sales
     */
    public Long getSalesCount(int customerId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM Sales WHERE customer.customerId = :custId");
            query.setParameter("custId", customerId);
            Long count = (Long) query.uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (session != null) session.close();
        }
    }
    
    /**
     * Get total revenue from customer
     * @param customerId Customer ID
     * @return Total purchase amount
     */
    public Double getTotalRevenue(int customerId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "SELECT COALESCE(SUM(s.totalAmount), 0.0) FROM Sales s WHERE s.customer.customerId = :custId");
            query.setParameter("custId", customerId);
            Double revenue = (Double) query.uniqueResult();
            return revenue != null ? revenue : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        } finally {
            if (session != null) session.close();
        }
    }
}
