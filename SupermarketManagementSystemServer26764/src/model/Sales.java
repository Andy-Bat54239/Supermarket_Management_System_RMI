package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author andyb
 */
@Entity
@Table(name = "sales")
public class Sales implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private int salesId;
    
    // MANY-TO-ONE: Many Sales belong to one Customer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    // MANY-TO-ONE: Many Sales belong to one Employee
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    // MANY-TO-ONE: Many Sales belong to one Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Column(name = "total_amount", nullable = false)
    private double totalAmount;
    
    @Column(name = "sale_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date saleDate;
    
    // Transient fields (not persisted, for display purposes)
    @Transient
    private String customerName;
    
    @Transient
    private String employeeName;
    
    @Transient
    private String productName;
    
    @Transient
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
