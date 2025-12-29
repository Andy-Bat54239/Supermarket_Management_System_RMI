package model;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author andyb
 */

public class InventoryTransaction implements Serializable{
    private static final long serialVersionUID = 1L;
    private int transactionId;
    private Product product;
    private TransactionType transactionType;
    private int quantity;
    private String reason;
    private Employee employee;
    private Date transactionDate;

    public InventoryTransaction() {
    }

    public InventoryTransaction(Product product, TransactionType transactionType, int quantity, String reason, Employee employee) {
        this.product = product;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.reason = reason;
        this.employee = employee;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    
}
