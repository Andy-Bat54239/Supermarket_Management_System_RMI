package dao;

import java.util.List;
import model.Product;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 * Product Data Access Object
 */
public class ProductDao extends BaseDao<Product>{

    public ProductDao() {
        super(Product.class);
    }
    
    public List<Product> findLowStockProducts(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT prod FROM Product prod WHERE prod.stockQuantity <= prod.reorderLevel");
            List<Product> products = query.list();
            ss.close();
            return products;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateStock(int productId, int newStock){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            Product product = (Product) ss.get(Product.class, productId);
            if(product != null){
                product.setStockQuantity(newStock);
                ss.update(product);
                tr.commit();
                ss.close();
                return true;
            }
            ss.close();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete product with validation
     * Checks if product has related sales or inventory transactions before deleting
     * @param productId Product ID to delete
     * @return true if deleted successfully, false if has dependencies
     */
    public boolean deleteProduct(int productId) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Load product
            Product product = (Product) session.get(Product.class, productId);
            
            if (product == null) {
                System.err.println("Product not found with ID: " + productId);
                return false;
            }
            
            // Check if product has sales records
            Query salesCheck = session.createQuery("SELECT COUNT(*) FROM Sales WHERE product.productId = :prodId");
            salesCheck.setParameter("prodId", productId);
            Long salesCount = (Long) salesCheck.uniqueResult();
            
            if (salesCount > 0) {
                System.err.println("Cannot delete product: Product has " + salesCount + " sales records.");
                System.err.println("Product: " + product.getProductName() + " (ID: " + productId + ")");
                return false;
            }
            
            // Check if product has inventory transactions
            Query transCheck = session.createQuery("SELECT COUNT(*) FROM InventoryTransaction WHERE product.productId = :prodId");
            transCheck.setParameter("prodId", productId);
            Long transCount = (Long) transCheck.uniqueResult();
            
            if (transCount > 0) {
                System.err.println("Cannot delete product: Product has " + transCount + " inventory transactions.");
                System.err.println("Product: " + product.getProductName() + " (ID: " + productId + ")");
                return false;
            }
            
            // Safe to delete
            session.delete(product);
            transaction.commit();
            
            System.out.println("Product deleted successfully: " + product.getProductName());
            return true;
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting product:");
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Check if product can be safely deleted
     * @param productId Product ID
     * @return true if has no dependencies, false otherwise
     */
    public boolean canDeleteProduct(int productId) {
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Check sales
            Query salesCheck = session.createQuery("SELECT COUNT(*) FROM Sales WHERE product.productId = :prodId");
            salesCheck.setParameter("prodId", productId);
            Long salesCount = (Long) salesCheck.uniqueResult();
            
            // Check inventory transactions
            Query transCheck = session.createQuery("SELECT COUNT(*) FROM InventoryTransaction WHERE product.productId = :prodId");
            transCheck.setParameter("prodId", productId);
            Long transCount = (Long) transCheck.uniqueResult();
            
            return (salesCount == 0 && transCount == 0);
            
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
     * Get count of sales for product
     * @param productId Product ID
     * @return Number of sales
     */
    public Long getSalesCount(int productId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM Sales WHERE product.productId = :prodId");
            query.setParameter("prodId", productId);
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
     * Get count of inventory transactions for product
     * @param productId Product ID
     * @return Number of transactions
     */
    public Long getTransactionCount(int productId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM InventoryTransaction WHERE product.productId = :prodId");
            query.setParameter("prodId", productId);
            Long count = (Long) query.uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (session != null) session.close();
        }
    }
}
