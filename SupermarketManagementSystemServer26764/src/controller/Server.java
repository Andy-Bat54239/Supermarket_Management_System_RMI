package controller;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import service.*;
import service.implementation.*;
import util.HibernateUtil;
import service.implementation.AuthServiceImpl;
import service.implementation.SessionServiceImpl;
import service.implementation.ReportServiceImpl;
import util.OTPEmailConsumer;
import util.ActiveMQConfig;
import util.SessionManager;

/**
 *
 * @author andyb
 */
public class Server {
    private static final int RMI_PORT = 3500;
    private static final String HOST = "localhost";
    
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("  Supermarket Management System Server");
            System.out.println("========================================");
            
            // 1. Initialize Hibernate
            System.out.println("\n[1] Initializing Hibernate...");
            HibernateUtil.getSessionFactory(); // This initializes the SessionFactory
            System.out.println("✓ Hibernate initialized successfully!");
            
            // 2. Create RMI Registry
            System.out.println("\n[2] Starting RMI Registry on port " + RMI_PORT + "...");
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            System.out.println("✓ RMI Registry started successfully!");
            
            // 3. Create and bind services
            System.out.println("\n[3] Creating and binding RMI services...");
            
            // Employee Service
            EmployeeService employeeService = new EmployeeServiceImpl();
            registry.rebind("EmployeeService", employeeService);
            System.out.println("   ✓ EmployeeService bound");
            
            // Customer Service
            CustomerService customerService = new CustomerServiceImpl();
            registry.rebind("CustomerService", customerService);
            System.out.println("   ✓ CustomerService bound");
            
            // Product Service
            ProductService productService = new ProductServiceImpl();
            registry.rebind("ProductService", productService);
            System.out.println("   ✓ ProductService bound");
            
            // Supplier Service
            SupplierService supplierService = new SupplierServiceImpl();
            registry.rebind("SupplierService", supplierService);
            System.out.println("   ✓ SupplierService bound");
            
            // Sales Service
            SalesService salesService = new SalesServiceImpl();
            registry.rebind("SalesService", salesService);
            System.out.println("   ✓ SalesService bound");
            
            // Inventory Service
            InventoryService inventoryService = new InventoryServiceImpl();
            registry.rebind("InventoryService", inventoryService);
            System.out.println("   ✓ InventoryService bound");
            
            // Dashboard Service
            DashboardService dashboardService = new DashboardServiceImpl();
            registry.rebind("DashboardService", dashboardService);
            System.out.println("   ✓ DashboardService bound");
            
            System.out.println("\n========================================");
            System.out.println("  SERVER IS RUNNING");
            System.out.println("  RMI Registry: rmi://" + HOST + ":" + RMI_PORT);
            System.out.println("========================================");
            // Register Authentication Service
        AuthServiceImpl authService = new AuthServiceImpl();
        registry.rebind("AuthService", authService);
        System.out.println("AuthService registered successfully");
        
        // Register Session Service
        SessionServiceImpl sessionService = new SessionServiceImpl();
        registry.rebind("SessionService", sessionService);
        System.out.println("SessionService registered successfully");
        
        // Register Report Service
        ReportServiceImpl reportService = new ReportServiceImpl();
        registry.rebind("ReportService", reportService);
        System.out.println("ReportService registered successfully");
        
        // Start ActiveMQ OTP Email Consumer
        Thread otpConsumerThread = new Thread(new OTPEmailConsumer(ActiveMQConfig.OTP_EMAIL_QUEUE));
        otpConsumerThread.setDaemon(true);
        otpConsumerThread.start();
        System.out.println("OTP Email Consumer started");
        
        // Start ActiveMQ Notification Consumer (optional)
        Thread notificationConsumerThread = new Thread(new OTPEmailConsumer(ActiveMQConfig.NOTIFICATION_QUEUE));
        notificationConsumerThread.setDaemon(true);
        notificationConsumerThread.start();
        System.out.println("Notification Consumer started");
        
        System.out.println("\n==============================================");
        System.out.println("  ALL SERVICES REGISTERED SUCCESSFULLY");
        System.out.println("  Server is ready to accept connections");
        System.out.println("==============================================\n");
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            SessionManager.getInstance().shutdown();
            System.out.println("Server shut down complete");
        }));
            
        } catch (Exception e) {
            System.err.println("Server startup failed: " + e.getMessage());
            e.printStackTrace();
        }
        Timer sessionMonitor = new Timer(true);
sessionMonitor.scheduleAtFixedRate(new TimerTask() {
    @Override
    public void run() {
        try {
            System.out.println("\n=== ACTIVE SESSIONS MONITOR ===");
            // This would require adding a getAllSessions() method to SessionService
            System.out.println("Monitoring sessions...");
            System.out.println("===============================\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}, 60000, 60000);
    }
}
