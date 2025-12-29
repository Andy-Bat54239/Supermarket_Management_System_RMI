package service.implementation;

import dao.SalesDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import model.Sales;
import service.SalesService;

/**
 *
 * @author andyb
 */
public class SalesServiceImpl extends UnicastRemoteObject implements SalesService{
    
    private SalesDao salesDao = new SalesDao();
    
    public SalesServiceImpl() throws RemoteException{
        
    }

    @Override
    public Integer processSale(int customerId, int employeeId, int productId, int quantity, double totalAmount) throws RemoteException {
        return salesDao.processSale(customerId, employeeId, productId, quantity, totalAmount);
    }

    @Override
    public Map<String, Double> getDailySalesForChart(int days) throws RemoteException {
        return salesDao.getDailySalesForChart(days);
    }

    @Override
    public Double getEmployeeRevenue(int employeeId) throws RemoteException {
        return salesDao.getEmployeeRevenue(employeeId);
    }

    @Override
    public Integer getSalesCountByEmployee(int employeeId) throws RemoteException {
        return salesDao.getSalesCountByEmployee(employeeId);
    }

    @Override
    public List<Sales> findAllSales() throws RemoteException {
        return salesDao.findAll();
    }

    @Override
    public List<Sales> getSalesByEmployee(int employeeId) throws RemoteException {
        return salesDao.getSalesByEmployee(employeeId);
    }
    
    
    
}
