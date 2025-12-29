package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Product;

/**
 *
 * @author andyb
 */
public interface ProductService extends Remote{
    Integer addProduct(Product product) throws RemoteException;
    boolean updateProduct(Product product) throws RemoteException;
    boolean deleteProduct(int productId) throws RemoteException;
    Product findProductById(int productId) throws RemoteException;
    List<Product> findAllProducts() throws RemoteException;
    List<Product> findLowStockProducts() throws RemoteException;
    boolean updateStock(int productId, int newStock) throws RemoteException;

}
