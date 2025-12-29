package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import model.Sales;

/**
 *
 * @author andyb
 */
public interface SalesService extends Remote{
    Integer processSale(int customerId, int employeeId, int productId, int quantity, double totalAmount) throws RemoteException;
    
     Map<String, Double> getDailySalesForChart(int days) throws RemoteException;
     
    Double getEmployeeRevenue(int employeeId) throws RemoteException;
    Integer getSalesCountByEmployee(int employeeId) throws RemoteException;
    
    List<Sales> findAllSales() throws RemoteException;
    
    List<Sales> getSalesByEmployee(int employeeId) throws RemoteException;
}
