package model;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author andyb
 */

public class Sales implements Serializable{
    private static final long serialVersionUID = 1L;
    private int salesId;
    private Customer customer;
    private Employee employee;
    private Product product;
    private int quantity;
    private double totalAmount;
    private Date saleDate;
    private String customerName;
    private String employeeName;
    private String productName;
    private double unitPrice;

    public Sales() {
    }

    public Sales(Customer customer, Employee employee, Product product, int quantity, double totalAmount) {
        this.customer = customer;
        this.employee = employee;
        this.product = product;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public int getCustomerId(){
        return customer != null ? customer.getCustomerId() : 0;
    }
    
    public int getEmployeeId(){
        return employee != null ? employee.getEmployeeId() : 0;
    }
    
    public int getProductId(){
        return product != null ? product.getProductId() : 0;
    }
}
