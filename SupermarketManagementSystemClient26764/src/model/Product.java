package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author andyb
 */
public class Product implements Serializable{
    private static final long serialVersionUID = 1L;
    private int productId;
    private String productName;
    private String category;
    private double price;
    private String supplierName;
    private int stockQuantity;
    private int reorderLevel = 10;
    private Date createdDate;
    private Set<Supplier> suppliers = new HashSet<>();
    private Set<Sales> sales = new HashSet<>();
    private Set<InventoryTransaction> inventoryTransactions = new HashSet<>();

    public Product() {
    }

    public Product(String productName, String category, double price, int stockQuantity, Date createdDate, int reorderLevel, String supplierName) {
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.supplierName = supplierName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Set<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<Supplier> suppliers) {
        this.suppliers = suppliers;
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
    
    public void addSupplier(Supplier supplier){
        this.suppliers.add(supplier);
        supplier.getProducts().add(this);
    }
    
    public void removeSupplier(Supplier supplier){
        this.suppliers.remove(supplier);
        supplier.getProducts().remove(this);
    }
    
    public String getSupplierName(){
        if(suppliers != null && !suppliers.isEmpty()) {
            return suppliers.iterator().next().getSupplierName();
        }
        return null;
    }
    
}
