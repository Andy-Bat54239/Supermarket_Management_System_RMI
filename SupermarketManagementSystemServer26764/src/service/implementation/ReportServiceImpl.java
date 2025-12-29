package service.implementation;

import dao.*;
import model.*;
import service.ReportService;
import util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author andyb
 */
public class ReportServiceImpl extends UnicastRemoteObject implements ReportService{
    private SalesDao salesDao = new SalesDao();
    private ProductDao productDao = new ProductDao();
    private CustomerDao customerDao = new CustomerDao();
    private InventoryTransactionDao inventoryDao = new InventoryTransactionDao();
    
    public ReportServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public ReportData generateSalesReport(Date startDate, Date endDate, String employeeName) throws RemoteException {
        try {
            ReportData report = new ReportData();
            report.setReportTitle("SALES REPORT");
            report.setReportType("SALES");
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setGeneratedBy(employeeName);
            
            // Set headers
            List<String[]> headers = new ArrayList<>();
            headers.add(new String[]{"Sale ID", "Date", "Product", "Quantity", "Price", "Total", "Customer"});
            report.setHeaders(headers);
            
            // Get sales data
            List<Sales> salesList = salesDao.findByDateRange(startDate, endDate);
            
            List<List<String>> data = new ArrayList<>();
            double totalRevenue = 0;
            int totalSales = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Sales sale : salesList) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(sale.getSalesId()));
                row.add(sdf.format(sale.getSaleDate()));
                row.add(sale.getProduct().getProductName());
                row.add(String.valueOf(sale.getQuantity()));
                row.add(String.format("%.2f", sale.getUnitPrice()));
                row.add(String.format("%.2f", sale.getTotalAmount()));
                row.add(sale.getCustomer() != null ? sale.getCustomer().getFullName() : "N/A");
                
                data.add(row);
                totalRevenue += sale.getTotalAmount();
                totalSales++;
            }
            
            report.setData(data);
            
            // Set summary
            String summary = String.format(
                "Total Sales: %d | Total Revenue: %.2f RWF", 
                totalSales, totalRevenue
            );
            report.setSummary(summary);
            
            return report;
            
        } catch (Exception e) {
            throw new RemoteException("Error generating sales report", e);
        }
    }
    
    @Override
    public ReportData generateInventoryReport(String employeeName) throws RemoteException {
        try {
            ReportData report = new ReportData();
            report.setReportTitle("INVENTORY REPORT");
            report.setReportType("INVENTORY");
            report.setGeneratedBy(employeeName);
            
            // Set headers
            List<String[]> headers = new ArrayList<>();
            headers.add(new String[]{"Product ID", "Product Name", "Category", "Stock", "Price", "Reorder Level", "Status"});
            report.setHeaders(headers);
            
            // Get inventory data
            List<Product> products = productDao.findAll();
            
            List<List<String>> data = new ArrayList<>();
            int totalProducts = 0;
            int lowStockProducts = 0;
            double totalValue = 0;
            
            for (Product product : products) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(product.getProductId()));
                row.add(product.getProductName());
                row.add(product.getCategory());
                row.add(String.valueOf(product.getStockQuantity()));
                row.add(String.format("%.2f", product.getPrice()));
                row.add(String.valueOf(product.getReorderLevel()));
                
                String status = product.getStockQuantity() <= product.getReorderLevel() ? "LOW STOCK" : "OK";
                row.add(status);
                
                data.add(row);
                totalProducts++;
                totalValue += product.getStockQuantity() * product.getPrice();
                
                if (product.getStockQuantity() <= product.getReorderLevel()) {
                    lowStockProducts++;
                }
            }
            
            report.setData(data);
            
            // Set summary
            String summary = String.format(
                "Total Products: %d | Low Stock Items: %d | Total Inventory Value: %.2f RWF",
                totalProducts, lowStockProducts, totalValue
            );
            report.setSummary(summary);
            
            return report;
            
        } catch (Exception e) {
            throw new RemoteException("Error generating inventory report", e);
        }
    }
    
    @Override
    public ReportData generateCustomerReport(String employeeName) throws RemoteException {
        try {
            ReportData report = new ReportData();
            report.setReportTitle("CUSTOMER REPORT");
            report.setReportType("CUSTOMER");
            report.setGeneratedBy(employeeName);
            
            // Set headers
            List<String[]> headers = new ArrayList<>();
            headers.add(new String[]{"Customer ID", "Name", "Email", "Phone", "Loyalty Points", "Membership Tier"});
            report.setHeaders(headers);
            
            // Get customer data
            List<Customer> customers = customerDao.findAll();
            
            List<List<String>> data = new ArrayList<>();
            int totalCustomers = 0;
            
            for (Customer customer : customers) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(customer.getCustomerId()));
                row.add(customer.getFullName());
                row.add(customer.getEmail());
                row.add(customer.getPhone());
                
                data.add(row);
                totalCustomers++;
            }
            
            report.setData(data);
            
            // Set summary
            String summary = String.format("Total Customers: %d", totalCustomers);
            report.setSummary(summary);
            
            return report;
            
        } catch (Exception e) {
            throw new RemoteException("Error generating customer report", e);
        }
    }
    
    @Override
    public ReportData generateProductReport(String employeeName) throws RemoteException {
        try {
            ReportData report = new ReportData();
            report.setReportTitle("PRODUCT REPORT");
            report.setReportType("PRODUCT");
            report.setGeneratedBy(employeeName);
            
            // Set headers
            List<String[]> headers = new ArrayList<>();
            headers.add(new String[]{"Product ID", "Name", "Category", "Price", "Stock", "Supplier"});
            report.setHeaders(headers);
            
            // Get product data
            List<Product> products = productDao.findAll();
            
            List<List<String>> data = new ArrayList<>();
            int totalProducts = 0;
            
            for (Product product : products) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(product.getProductId()));
                row.add(product.getProductName());
                row.add(product.getCategory());
                row.add(String.format("%.2f", product.getPrice()));
                row.add(String.valueOf(product.getStockQuantity()));
                row.add(product.getSupplierName() != null ? product.getSupplierName() : "N/A");
                
                data.add(row);
                totalProducts++;
            }
            
            report.setData(data);
            
            // Set summary
            String summary = String.format("Total Products: %d", totalProducts);
            report.setSummary(summary);
            
            return report;
            
        } catch (Exception e) {
            throw new RemoteException("Error generating product report", e);
        }
    }
    
    @Override
    public ReportData generateLowStockReport(String employeeName) throws RemoteException {
        try {
            ReportData report = new ReportData();
            report.setReportTitle("LOW STOCK REPORT");
            report.setReportType("LOW_STOCK");
            report.setGeneratedBy(employeeName);
            
            // Set headers
            List<String[]> headers = new ArrayList<>();
            headers.add(new String[]{"Product ID", "Product Name", "Category", "Current Stock", "Reorder Level", "Deficit"});
            report.setHeaders(headers);
            
            // Get low stock products
            List<Product> products = productDao.findLowStockProducts();
            
            List<List<String>> data = new ArrayList<>();
            int lowStockCount = 0;
            
            for (Product product : products) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(product.getProductId()));
                row.add(product.getProductName());
                row.add(product.getCategory());
                row.add(String.valueOf(product.getStockQuantity()));
                row.add(String.valueOf(product.getReorderLevel()));
                row.add(String.valueOf(product.getReorderLevel() - product.getStockQuantity()));
                
                data.add(row);
                lowStockCount++;
            }
            
            report.setData(data);
            
            // Set summary
            String summary = String.format("Low Stock Items: %d", lowStockCount);
            report.setSummary(summary);
            
            return report;
            
        } catch (Exception e) {
            throw new RemoteException("Error generating low stock report", e);
        }
    }
    
    @Override
    public byte[] exportToPDF(ReportData reportData) throws RemoteException {
        try {
            return PDFReportGenerator.generatePDF(reportData);
        } catch (Exception e) {
            throw new RemoteException("Error exporting to PDF", e);
        }
    }
    
    @Override
    public byte[] exportToExcel(ReportData reportData) throws RemoteException {
        try {
            return ExcelReportGenerator.generateExcel(reportData);
        } catch (Exception e) {
            throw new RemoteException("Error exporting to Excel", e);
        }
    }
    
    @Override
    public byte[] exportToCSV(ReportData reportData) throws RemoteException {
        try {
            return CSVReportGenerator.generateCSV(reportData);
        } catch (Exception e) {
            throw new RemoteException("Error exporting to CSV", e);
        }
    }
}
