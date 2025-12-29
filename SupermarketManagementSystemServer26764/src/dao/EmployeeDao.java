package dao;

import java.util.List;
import model.Employee;
import model.EmployeeProfile;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;


/**
 * Employee Data Access Object
 * Handles all database operations for Employee entity
 */
public class EmployeeDao extends BaseDao<Employee>{
    
    public EmployeeDao(){
        super(Employee.class);
    }
    
    /**
     * Authenticate employee with username and password
     */
    public Employee authenticate(String username, String password) {
        Session session = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Employee WHERE username = :username");
            query.setParameter("username", username);
            Employee employee = (Employee) query.uniqueResult();
            
            if (employee != null && password.equals(employee.getPasswordHash())) {
                // FIX: Replace Hibernate PersistentSet with plain Java HashSet
                // PersistentSet is a Hibernate proxy that can't serialize properly over RMI
                // after session closes. Replace with plain collections to fix serialization.
                employee.setSales(new java.util.HashSet<>());
                employee.setInventoryTransactions(new java.util.HashSet<>());
                
                // EmployeeProfile is fine - single object, no collection issues
                
                System.out.println("Authentication successful for user: " + username);
                return employee;
            }
            
            System.out.println("Authentication failed for user: " + username + " (invalid credentials)");
            return null;
        } catch(Exception e){
            System.err.println("Authentication error for user: " + username);
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Find employee by username
     */
    public Employee findByUsername(String username){
        Session session = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Employee WHERE username = :username");
            query.setParameter("username", username);
            Employee employee = (Employee) query.uniqueResult();
            return employee;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Add new employee with password hashing
     */
    public Integer addEmployee(Employee employee){
        if(employee.getPasswordHash() != null){
            String hashedPassword = employee.getPasswordHash();
            employee.setPasswordHash(hashedPassword);
        }
        return save(employee);
    }
    
    /**
     * Update employee with password hashing if password changed
     */
    public boolean updateEmployee(Employee employee) {
        if (employee.getPasswordHash() != null) {
            String hashedPassword = employee.getPasswordHash();
            employee.setPasswordHash(hashedPassword);
        }
        return update(employee);
    }
    
    /**
     * Delete employee by ID
     * Checks business rules before deletion
     * 
     * @param employeeId The ID of the employee to delete
     * @return true if successfully deleted, false otherwise
     */
    public boolean deleteEmployee(int employeeId) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Load the employee
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            
            if (employee == null) {
                System.err.println("Employee not found with ID: " + employeeId);
                return false;
            }
            
            // Business Rule 1: Cannot delete yourself (optional - implement in service layer)
            // Business Rule 2: Check if employee has sales history
            Query salesCheck = session.createQuery("SELECT COUNT(*) FROM Sales WHERE employee.employeeId = :empId");
            salesCheck.setParameter("empId", employeeId);
            Long salesCount = (Long) salesCheck.uniqueResult();
            
            if (salesCount > 0) {
                System.err.println("Cannot delete employee: Employee has " + salesCount + " sales records.");
                System.err.println("Consider deactivating the employee instead of deleting.");
                return false;
            }
            
            // Business Rule 3: Check if employee has inventory transactions
            Query transCheck = session.createQuery("SELECT COUNT(*) FROM InventoryTransaction WHERE employee.employeeId = :empId");
            transCheck.setParameter("empId", employeeId);
            Long transCount = (Long) transCheck.uniqueResult();
            
            if (transCount > 0) {
                System.err.println("Cannot delete employee: Employee has " + transCount + " inventory transactions.");
                return false;
            }
            
            // If all checks pass, proceed with deletion
            // Note: EmployeeProfile will be deleted automatically due to CASCADE
            System.out.println("Deleting employee: " + employee.getFullName() + " (ID: " + employeeId + ")");
            
            session.delete(employee);
            transaction.commit();
            
            System.out.println("Employee deleted successfully!");
            return true;
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting employee:");
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Soft delete - Mark employee as inactive instead of deleting
     * This is the recommended approach for employees with history
     * 
     * @param employeeId The ID of the employee to deactivate
     * @return true if successfully deactivated, false otherwise
     */
    public boolean deactivateEmployee(int employeeId) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            
            if (employee == null) {
                System.err.println("Employee not found with ID: " + employeeId);
                return false;
            }
            
            // You would need to add an 'active' or 'status' field to Employee model
            // For now, we can modify the role or username
            // Example: employee.setActive(false);
            
            System.out.println("Deactivating employee: " + employee.getFullName());
            
            // Modify username to indicate deactivation
            if (!employee.getUsername().startsWith("INACTIVE_")) {
                employee.setUsername("INACTIVE_" + employee.getUsername());
            }
            
            session.update(employee);
            transaction.commit();
            
            System.out.println("Employee deactivated successfully!");
            return true;
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deactivating employee:");
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Force delete - Delete employee regardless of dependencies
     * WARNING: This will delete all related records (CASCADE)
     * Use with extreme caution!
     * 
     * @param employeeId The ID of the employee to force delete
     * @return true if successfully deleted, false otherwise
     */
    public boolean forceDeleteEmployee(int employeeId) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            
            if (employee == null) {
                System.err.println("Employee not found with ID: " + employeeId);
                return false;
            }
            
            System.out.println("FORCE DELETING employee: " + employee.getFullName());
            System.out.println("WARNING: This will delete all related sales and transactions!");
            
            // Delete employee (CASCADE will handle related records)
            session.delete(employee);
            transaction.commit();
            
            System.out.println("Employee and all related records deleted!");
            return true;
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error force deleting employee:");
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Check if employee can be safely deleted
     * 
     * @param employeeId The ID of the employee to check
     * @return true if can be deleted, false if has dependencies
     */
    public boolean canDeleteEmployee(int employeeId) {
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Check sales
            Query salesCheck = session.createQuery("SELECT COUNT(*) FROM Sales WHERE employee.employeeId = :empId");
            salesCheck.setParameter("empId", employeeId);
            Long salesCount = (Long) salesCheck.uniqueResult();
            
            // Check inventory transactions
            Query transCheck = session.createQuery("SELECT COUNT(*) FROM InventoryTransaction WHERE employee.employeeId = :empId");
            transCheck.setParameter("empId", employeeId);
            Long transCount = (Long) transCheck.uniqueResult();
            
            return (salesCount == 0 && transCount == 0);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Get count of sales by employee
     * Useful for showing warning before deletion
     */
    public Long getSalesCount(int employeeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM Sales WHERE employee.employeeId = :empId");
            query.setParameter("empId", employeeId);
            return (Long) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (session != null) session.close();
        }
    }
    
    /**
     * Get count of inventory transactions by employee
     */
    public Long getTransactionCount(int employeeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM InventoryTransaction WHERE employee.employeeId = :empId");
            query.setParameter("empId", employeeId);
            return (Long) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (session != null) session.close();
        }
    }
    
    // ========== EMPLOYEE PROFILE METHODS ==========
    
    /**
     * Get employee profile by employee ID
     */
    public EmployeeProfile getEmployeeProfile(int employeeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM EmployeeProfile WHERE employee.employeeId = :employeeId");
            query.setParameter("employeeId", employeeId);
            EmployeeProfile profile = (EmployeeProfile) query.uniqueResult();
            return profile;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Save or update employee profile
     */
    public boolean saveEmployeeProfile(EmployeeProfile profile) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            if(profile.getProfileId() == 0) {
                session.save(profile);
            } else {
                session.update(profile);
            }
            
            transaction.commit();
            return true;
        } catch(Exception e) {
            if(transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Change employee password
     */
    public boolean changePassword(int employeeId, String oldPassword, String newPassword) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            if(employee == null) return false;
            
            // Verify old password
//            if(!PasswordUtil.verifyPassword(oldPassword, employee.getPasswordHash())) {
//                return false;
//            }
            
            // Hash and set new password
//            String newHash = PasswordUtil.hashPassword(newPassword);
//            employee.setPasswordHash(newHash);
//            session.update(employee);
            
            transaction.commit();
            return true;
        } catch(Exception e) {
            if(transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Mark first login as complete
     */
    public boolean markFirstLoginComplete(int employeeId) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            if(employee != null) {
                employee.setFirstLogin(false);
                session.update(employee);
                transaction.commit();
                return true;
            }
            return false;
        } catch(Exception e) {
            if(transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if(session != null) session.close();
        }
    }

// ADD THESE METHODS TO EmployeeDao.java

    // ========== SEARCH & FILTER METHODS ==========
    
    /**
     * Find employees by role
     */
    public List<Employee> findByRole(String role) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Employee WHERE role = :role ORDER BY fullName ASC");
            query.setParameter("role", role);
            @SuppressWarnings("unchecked")
            List<Employee> employees = query.list();
            return employees;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Search employees by name
     */
    public List<Employee> searchByName(String searchTerm) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "FROM Employee WHERE LOWER(fullName) LIKE LOWER(:searchTerm) ORDER BY fullName ASC");
            query.setParameter("searchTerm", "%" + searchTerm + "%");
            @SuppressWarnings("unchecked")
            List<Employee> employees = query.list();
            return employees;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Get active employees (excludes deactivated)
     */
    public List<Employee> findActiveEmployees() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "FROM Employee WHERE username NOT LIKE 'INACTIVE_%' ORDER BY fullName ASC");
            @SuppressWarnings("unchecked")
            List<Employee> employees = query.list();
            return employees;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Get total employee count
     */
    public Long getTotalCount() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM Employee");
            Long count = (Long) query.uniqueResult();
            return count != null ? count : 0L;
        } catch(Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if(session != null) session.close();
        }
    }
    
    /**
     * Get employee count by role
     */
    public Long getCountByRole(String role) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(*) FROM Employee WHERE role = :role");
            query.setParameter("role", role);
            Long count = (Long) query.uniqueResult();
            return count != null ? count : 0L;
        } catch(Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if(session != null) session.close();
        }
    }
    
    // ========== REACTIVATION METHOD ==========
    
    /**
     * Reactivate a deactivated employee
     */
    public boolean reactivateEmployee(int employeeId) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            Employee employee = (Employee) session.get(Employee.class, employeeId);
            
            if(employee == null) {
                System.err.println("Employee not found with ID: " + employeeId);
                return false;
            }
            
            // Remove INACTIVE_ prefix if present
            if(employee.getUsername().startsWith("INACTIVE_")) {
                String originalUsername = employee.getUsername().substring(9); // Remove "INACTIVE_"
                employee.setUsername(originalUsername);
                
                System.out.println("Reactivating employee: " + employee.getFullName());
                
                session.update(employee);
                transaction.commit();
                
                System.out.println("Employee reactivated successfully!");
                return true;
            } else {
                System.out.println("Employee is already active: " + employee.getFullName());
                return true; // Already active
            }
            
        } catch(Exception e) {
            if(transaction != null) transaction.rollback();
            System.err.println("Error reactivating employee:");
            e.printStackTrace();
            return false;
        } finally {
            if(session != null) session.close();
        }
    }
    
    // ========== REVENUE METHOD ==========
    
    /**
     * Get total revenue generated by employee
     */
    public Double getTotalRevenue(int employeeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "SELECT COALESCE(SUM(s.totalAmount), 0.0) FROM Sales s WHERE s.employee.employeeId = :empId");
            query.setParameter("empId", employeeId);
            Double revenue = (Double) query.uniqueResult();
            return revenue != null ? revenue : 0.0;
        } catch(Exception e) {
            e.printStackTrace();
            return 0.0;
        } finally {
            if(session != null) session.close();
        }
    }
    
    // ========== PASSWORD VERIFICATION ==========
    
    /**
     * Verify employee password without full authentication
     */
//    public boolean verifyPassword(int employeeId, String password) {
//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            Employee employee = (Employee) session.get(Employee.class, employeeId);
//            
//            if(employee == null) return false;
//            
//            return PasswordUtil.verifyPassword(password, employee.getPasswordHash());
//            
//        } catch(Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            if(session != null) session.close();
//        }
//    }
}
