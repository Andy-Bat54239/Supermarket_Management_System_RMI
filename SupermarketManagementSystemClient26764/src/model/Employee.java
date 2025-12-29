package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class Employee implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int employeeId;
    
    private String fullName;
    
    private String role;
    
    private double salary;
    
    private Date hireDate;
    
    private String contact;
    
    private String username;
    
    private String passwordHash;
    
    private Boolean firstLogin = true;
    
    private EmployeeProfile employeeProfile;
    
    private Set<Sales> sales = new HashSet<>();
    
    private Set<InventoryTransaction> inventoryTransactions = new HashSet<>();

    public Employee() {
    }

    public Employee(String fullName, String role, double salary, Date hireDate, String contact, String username, String passwordHash) {
        this.fullName = fullName;
        this.role = role;
        this.salary = salary;
        this.hireDate = hireDate;
        this.contact = contact;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstLogin = true;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
    
    public boolean isFirstLogin() {
        return firstLogin != null && firstLogin;
    }

    public EmployeeProfile getEmployeeProfile() {
        return employeeProfile;
    }

    public void setEmployeeProfile(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
    }

    public Set<Sales> getSales() {
        return sales;
    }

    public void setSales(Set<Sales> sales) {
        this.sales = sales;
    }

    public Set<InventoryTransaction> getInventoryTransactions() {
        return inventoryTransactions;
    }

    public void setInventoryTransactions(Set<InventoryTransaction> inventoryTransactions) {
        this.inventoryTransactions = inventoryTransactions;
    }
    
    public boolean isCashier(){
        return "CASHIER".equalsIgnoreCase(this.role);
    }
    
    public boolean isAdmin(){
        return "ADMIN".equalsIgnoreCase(this.role);
    }
    
    public boolean isManager(){
        return "MANAGER".equalsIgnoreCase(this.role);
    }
}
