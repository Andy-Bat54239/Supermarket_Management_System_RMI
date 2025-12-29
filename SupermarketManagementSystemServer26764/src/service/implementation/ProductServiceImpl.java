package service.implementation;

import dao.ProductDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Product;
import service.ProductService;

/**
 * Product Service Implementation
 */
public class ProductServiceImpl extends UnicastRemoteObject implements ProductService{
    
    private ProductDao productDao = new ProductDao();
    
    public ProductServiceImpl() throws RemoteException{
        
    }

    @Override
    public Integer addProduct(Product product) throws RemoteException {
        return productDao.save(product);
    }

    @Override
    public boolean updateProduct(Product product) throws RemoteException {
        return productDao.update(product);
    }

    @Override
    public boolean deleteProduct(int productId) throws RemoteException {
        // Use the proper delete method with validation
        return productDao.deleteProduct(productId);
    }

    @Override
    public Product findProductById(int productId) throws RemoteException {
        return productDao.findById(productId);
    }

    @Override
    public List<Product> findAllProducts() throws RemoteException {
        return productDao.findAll();
    }

    @Override
    public List<Product> findLowStockProducts() throws RemoteException {
        return productDao.findLowStockProducts();
    }

    @Override
    public boolean updateStock(int productId, int newStock) throws RemoteException {
        return productDao.updateStock(productId, newStock);
    }
}
