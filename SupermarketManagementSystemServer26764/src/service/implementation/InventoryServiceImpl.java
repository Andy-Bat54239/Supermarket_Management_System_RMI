package service.implementation;

import dao.InventoryTransactionDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.InventoryTransaction;
import model.TransactionType;
import service.InventoryService;

/**
 *
 * @author andyb
 */
public class InventoryServiceImpl extends UnicastRemoteObject implements InventoryService{
    
    private InventoryTransactionDao inventoryDao = new InventoryTransactionDao();
    
    public InventoryServiceImpl() throws RemoteException{
        
    }

    @Override
    public Integer recordTransaction(int productId, TransactionType type, int quantity, String reason, int employeeId) throws RemoteException {
        return inventoryDao.recordTransaction(productId, type, quantity, reason, employeeId);
    }

    @Override
    public List<InventoryTransaction> findByProduct(int productId) throws RemoteException {
        return inventoryDao.findByProduct(productId);
    }
}
