package service.implementation;

import dao.DashboardDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.DailySalesData;
import service.DashboardService;

/**
 *
 * @author andyb
 */
public class DashboardServiceImpl extends UnicastRemoteObject implements DashboardService{
    
    private DashboardDao dashboardDao = new DashboardDao();
    
    public DashboardServiceImpl() throws RemoteException{
        
    }

    @Override
    public Integer getTotalProducts() throws RemoteException {
        return dashboardDao.getTotalProducts();
    }

    @Override
    public Integer getTotalSales() throws RemoteException {
        return dashboardDao.getTotalSales();
    }

    @Override
    public Integer getTotalCustomers() throws RemoteException {
        return dashboardDao.getTotalCustomers();
    }

    @Override
    public Double getTotalRevenue() throws RemoteException {
        return dashboardDao.getTotalRevenue();
    }

    @Override
    public List<DailySalesData> getDailySalesTrend() throws RemoteException {
        return dashboardDao.getDailySalesTrend();
    }
    
    
    
}
