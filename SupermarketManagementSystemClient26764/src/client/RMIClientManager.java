package client;

import service.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import model.Employee;
import model.UserSession;

/**
 *
 * @author andyb
 */
public class RMIClientManager {
    private static RMIClientManager instance;
    
    private static final String HOST = "localhost";
    private static final int PORT = 3500;
    
    private EmployeeService employeeService;
    private CustomerService customerService;
    private ProductService productService;
    private SupplierService supplierService;
    private SalesService salesService;
    private InventoryService inventoryService;
    private DashboardService dashboardService;
    
    private AuthService authService;
    private SessionService sessionService;
    private ReportService reportService;
    
    private String currentSessionId;
    private model.UserSession currentSession;
    private model.Employee currentEmployee;
    
    private Registry registry;
    
    private RMIClientManager(){
        try{
            connect();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static synchronized RMIClientManager getInstance(){
        if(instance == null){
            instance = new RMIClientManager();
        }
        return instance;
    }
    
    private void connect() throws Exception {
        System.out.println("Connecting to RMI server at " + HOST +":" + PORT + "...");
        
        registry= LocateRegistry.getRegistry(HOST, PORT);
        
        employeeService = (EmployeeService) registry.lookup("EmployeeService");
        customerService = (CustomerService) registry.lookup("CustomerService");
        productService = (ProductService) registry.lookup("ProductService");
        supplierService = (SupplierService) registry.lookup("SupplierService");
        salesService = (SalesService) registry.lookup("SalesService");
        inventoryService = (InventoryService) registry.lookup("InventoryService");
        dashboardService = (DashboardService) registry.lookup("DashboardService");
        
        authService = (AuthService) registry.lookup("AuthService");
        sessionService = (SessionService) registry.lookup("SessionService");
        reportService = (ReportService) registry.lookup("ReportService");
    }
    
    public void reconnect(){
        try{
            connect();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public SupplierService getSupplierService() {
        return supplierService;
    }

    public SalesService getSalesService() {
        return salesService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public DashboardService getDashboardService() {
        return dashboardService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public void setCurrentSessionId(String currentSessionId) {
        this.currentSessionId = currentSessionId;
    }

    public UserSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(UserSession currentSession) {
        this.currentSession = currentSession;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }
    
    
    
    
    public boolean isConnected() {
        return employeeService != null && 
               customerService != null && 
               productService != null && 
               salesService != null &&
               dashboardService != null;
    }
    
    public boolean isServiceAvailable(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "employee":
                return employeeService != null;
            case "customer":
                return customerService != null;
            case "product":
                return productService != null;
            case "sales":
                return salesService != null;
            case "dashboard":
                return dashboardService != null;
            case "supplier":
                return supplierService != null;
            case "inventory":
                return inventoryService != null;
            default:
                return false;
        }
    }
    
    public String getConnectionStatus() {
        if (isConnected()) {
            return "Connected to all core services";
        } else {
            StringBuilder status = new StringBuilder("Partial connection: ");
            if (employeeService == null) status.append("Employee ");
            if (customerService == null) status.append("Customer ");
            if (productService == null) status.append("Product ");
            if (salesService == null) status.append("Sales ");
            if (dashboardService == null) status.append("Dashboard ");
            status.append("service(s) unavailable");
            return status.toString();
        }
        
    }
    
    public boolean hasActiveSession() {
        return currentSessionId != null && !currentSessionId.isEmpty();
    }
    
    public void clearSession() {
        this.currentSessionId = null;
        this.currentSession = null;
    }
    
    public boolean validateSession() {
        if (!hasActiveSession()) {
            return false;
        }
        
        try {
            String sessionId = getCurrentSessionId();
            return sessionService.validateSession(sessionId);
        } catch (Exception e) {
            System.err.println("[RMI CLIENT] Session validation error: " + e.getMessage());
            return false;
        }
    }
    
}
