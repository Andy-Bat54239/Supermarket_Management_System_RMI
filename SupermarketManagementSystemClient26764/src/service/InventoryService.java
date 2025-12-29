package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.InventoryTransaction;
import model.TransactionType;

/**
 *
 * @author andyb
 */
public interface InventoryService extends Remote{
    Integer recordTransaction(int productId, TransactionType type, int quantity, String reason, int employeeId) throws RemoteException;
    
    List<InventoryTransaction> findByProduct(int productId) throws RemoteException;
}
