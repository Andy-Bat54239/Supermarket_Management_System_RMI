package service.implementation;

import dao.EmployeeDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Employee;
import model.EmployeeProfile;
import service.EmployeeService;

/**
 * Employee Service Implementation - COMPLETE VERSION
 * Implements all remote methods for employee management
 */
public class EmployeeServiceImpl extends UnicastRemoteObject implements EmployeeService {
    
    private EmployeeDao employeeDao = new EmployeeDao();
    
    public EmployeeServiceImpl() throws RemoteException {
    }

    // ========== AUTHENTICATION ==========
    
    @Override
    public Employee authenticate(String username, String password) throws RemoteException {
        return employeeDao.authenticate(username, password);
    }

    // ========== BASIC CRUD ==========
    
    @Override
    public Employee findEmployeeById(int employeeId) throws RemoteException {
        return employeeDao.findById(employeeId);
    }
    
    @Override
    public Employee findByUsername(String username) throws RemoteException {
        return employeeDao.findByUsername(username);
    }

    @Override
    public List<Employee> findAllEmployees() throws RemoteException {
        return employeeDao.findAll();
    }

    @Override
    public Integer addEmployee(Employee employee) throws RemoteException {
        return employeeDao.addEmployee(employee);
    }

    @Override
    public boolean updateEmployee(Employee employee) throws RemoteException {
        return employeeDao.updateEmployee(employee);
    }

    // ========== SEARCH & FILTER ==========
    
    @Override
    public List<Employee> findEmployeesByRole(String role) throws RemoteException {
        return employeeDao.findByRole(role);
    }
    
    @Override
    public List<Employee> searchEmployeesByName(String searchTerm) throws RemoteException {
        return employeeDao.searchByName(searchTerm);
    }
    
    @Override
    public List<Employee> findActiveEmployees() throws RemoteException {
        return employeeDao.findActiveEmployees();
    }
    
    @Override
    public Long getTotalEmployeeCount() throws RemoteException {
        return employeeDao.getTotalCount();
    }
    
    @Override
    public Long getEmployeeCountByRole(String role) throws RemoteException {
        return employeeDao.getCountByRole(role);
    }

    // ========== DELETE METHODS ==========
    
    @Override
    public boolean deleteEmployee(int employeeId) throws RemoteException {
        return employeeDao.deleteEmployee(employeeId);
    }

    @Override
    public boolean deactivateEmployee(int employeeId) throws RemoteException {
        return employeeDao.deactivateEmployee(employeeId);
    }

    @Override
    public boolean forceDeleteEmployee(int employeeId) throws RemoteException {
        return employeeDao.forceDeleteEmployee(employeeId);
    }

    @Override
    public boolean canDeleteEmployee(int employeeId) throws RemoteException {
        return employeeDao.canDeleteEmployee(employeeId);
    }
    
    @Override
    public boolean reactivateEmployee(int employeeId) throws RemoteException {
        return employeeDao.reactivateEmployee(employeeId);
    }

    // ========== STATISTICS & REPORTING ==========
    
    @Override
    public Long getEmployeeSalesCount(int employeeId) throws RemoteException {
        return employeeDao.getSalesCount(employeeId);
    }
    
    @Override
    public Double getEmployeeTotalRevenue(int employeeId) throws RemoteException {
        return employeeDao.getTotalRevenue(employeeId);
    }

    @Override
    public Long getEmployeeTransactionCount(int employeeId) throws RemoteException {
        return employeeDao.getTransactionCount(employeeId);
    }
    
    @Override
    public String getEmployeePerformanceSummary(int employeeId) throws RemoteException {
        Employee emp = employeeDao.findById(employeeId);
        if(emp == null) return "Employee not found";
        
        Long salesCount = employeeDao.getSalesCount(employeeId);
        Double revenue = employeeDao.getTotalRevenue(employeeId);
        Long transCount = employeeDao.getTransactionCount(employeeId);
        
        return String.format(
            "Employee: %s (%s)\n" +
            "Sales: %d transactions\n" +
            "Revenue: RWF %.2f\n" +
            "Inventory Transactions: %d",
            emp.getFullName(), emp.getRole(),
            salesCount, revenue, transCount
        );
    }

    // ========== PROFILE METHODS ==========
    
    @Override
    public EmployeeProfile getEmployeeProfile(int employeeId) throws RemoteException {
        return employeeDao.getEmployeeProfile(employeeId);
    }

    @Override
    public boolean saveEmployeeProfile(EmployeeProfile profile) throws RemoteException {
        return employeeDao.saveEmployeeProfile(profile);
    }

    @Override
    public boolean changePassword(int employeeId, String oldPassword, String newPassword) throws RemoteException {
        return employeeDao.changePassword(employeeId, oldPassword, newPassword);
    }

    @Override
    public boolean markFirstLoginComplete(int employeeId) throws RemoteException {
        return employeeDao.markFirstLoginComplete(employeeId);
    }
    
    @Override
    public boolean isFirstLogin(int employeeId) throws RemoteException {
        Employee emp = employeeDao.findById(employeeId);
        return emp != null && emp.isFirstLogin();
    }

    // ========== VALIDATION ==========
    
    @Override
    public boolean isUsernameAvailable(String username) throws RemoteException {
        Employee existing = employeeDao.findByUsername(username);
        return existing == null;
    }
    
    @Override
    public boolean isUsernameAvailable(String username, int excludeEmployeeId) throws RemoteException {
        Employee existing = employeeDao.findByUsername(username);
        return existing == null || existing.getEmployeeId() == excludeEmployeeId;
    }
}
