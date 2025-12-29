package service.implementation;

import dao.SupplierDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Supplier;
import service.SupplierService;

/**
 *
 * @author andyb
 */
public class SupplierServiceImpl extends UnicastRemoteObject implements SupplierService{
    
    private SupplierDao supplierDao = new SupplierDao();
    
    public SupplierServiceImpl() throws RemoteException{
        
    }

    @Override
    public Integer addSupplier(Supplier supplier) throws RemoteException {
        return supplierDao.save(supplier);
    }

    @Override
    public boolean updateSupplier(Supplier supplier) throws RemoteException {
        return supplierDao.update(supplier);
    }

    @Override
    public boolean deleteSupplier(int supplierId) throws RemoteException {
        Supplier supplier = supplierDao.findById(supplierId);
        if(supplier != null){
            supplierDao.delete(supplier);
        }
        return false;
    }

    @Override
    public Supplier findSupplierById(int supplierId) throws RemoteException {
        return supplierDao.findById(supplierId);
    }

    @Override
    public List<Supplier> findAllSuppliers() throws RemoteException {
        return supplierDao.findAll();
    }
    
    
    
}
