package dao;

import java.util.List;
import model.Employee;
import model.InventoryTransaction;
import model.Product;
import model.TransactionType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 *
 * @author andyb
 */
public class InventoryTransactionDao extends BaseDao<InventoryTransaction>{

    public InventoryTransactionDao() {
        super(InventoryTransaction.class);
    }
    
    public Integer recordTransaction(int productId, TransactionType type, int quantity, String reason, int employeeId){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            Product product = (Product) ss.get(Product.class, productId);
            Employee employee = (Employee) ss.get(Employee.class, employeeId);
            
            if(product != null){
                InventoryTransaction inventoryTransaction = new InventoryTransaction(product, type, quantity, reason, employee);
                
                int currentStock = product.getStockQuantity();
                switch(type){
                    case RESTOCK:
                    case ADJUSTMENT:
                        product.setStockQuantity(currentStock+quantity);
                        break;
                    case SALE:
                        product.setStockQuantity(currentStock - quantity);
                        break;
                }
                
                Integer transactionId = (Integer) ss.save(inventoryTransaction);
                ss.update(product);
                tr.commit();
                ss.close();
                return transactionId;
            }
            ss.close();
            return null;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public List<InventoryTransaction> findByProduct(int productId){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery(
               "FROM InventoryTransaction WHERE product.productId = :productId ORDER BY transactionDate DESC"
            );
            query.setParameter("productId", productId);
            ss.close();
            return query.list();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
