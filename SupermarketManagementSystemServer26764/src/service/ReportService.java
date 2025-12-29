package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import model.ReportData;

/**
 *
 * @author andyb
 */
public interface ReportService extends Remote{
    
    /**
     * Generate sales report
     */
    ReportData generateSalesReport(Date startDate, Date endDate, String employeeName) throws RemoteException;
    
    /**
     * Generate inventory report
     */
    ReportData generateInventoryReport(String employeeName) throws RemoteException;
    
    /**
     * Generate customer report
     */
    ReportData generateCustomerReport(String employeeName) throws RemoteException;
    
    /**
     * Generate product report
     */
    ReportData generateProductReport(String employeeName) throws RemoteException;
    
    /**
     * Generate low stock report
     */
    ReportData generateLowStockReport(String employeeName) throws RemoteException;
    
    /**
     * Export report to PDF
     * @return byte array of PDF file
     */
    byte[] exportToPDF(ReportData reportData) throws RemoteException;
    
    /**
     * Export report to Excel
     * @return byte array of Excel file
     */
    byte[] exportToExcel(ReportData reportData) throws RemoteException;
    
    /**
     * Export report to CSV
     * @return byte array of CSV file
     */
    byte[] exportToCSV(ReportData reportData) throws RemoteException;
    
}
