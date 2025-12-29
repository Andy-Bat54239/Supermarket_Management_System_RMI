package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.DailySalesData;

/**
 *
 * @author andyb
 */
public interface DashboardService extends Remote{
     Integer getTotalProducts() throws RemoteException;
     
    Integer getTotalSales() throws RemoteException;
    
    Integer getTotalCustomers() throws RemoteException;
    
    Double getTotalRevenue() throws RemoteException;
    
    List<DailySalesData> getDailySalesTrend() throws RemoteException;
}
