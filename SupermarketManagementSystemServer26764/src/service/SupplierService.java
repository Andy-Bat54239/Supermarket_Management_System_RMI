package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Supplier;

/**
 *
 * @author andyb
 */
public interface SupplierService extends Remote{
    Integer addSupplier(Supplier supplier) throws RemoteException;
    boolean updateSupplier(Supplier supplier) throws RemoteException;
    boolean deleteSupplier(int supplierId) throws RemoteException;
    Supplier findSupplierById(int supplierId) throws RemoteException;
    List<Supplier> findAllSuppliers() throws RemoteException;
}
