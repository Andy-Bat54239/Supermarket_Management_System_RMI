/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import service.*;
import client.RMIClientManager;
import util.ValidationUtil;
import util.ValidationUtil.ValidationResult;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import model.Employee;
import model.Product;
import model.Sales;

/**
 *
 * @author andyb
 */
public final class SalesPanel extends javax.swing.JPanel {
    
    private CustomerService customerService;
    private EmployeeService employeeService;
    private ProductService productService;
    private SalesService salesService;
    
    private Employee currentEmployee;
    private int cashierEmployeeId = -1;
    private boolean isCashierMode = false;
    
    private int selectedProductId;
    private double selectedProductPrice;
    private int selectedProductStock;
    private int selectedEmployeeId;
    
    DefaultTableModel tblModel = new DefaultTableModel();

    /**
     * Creates new form ProductsPanel
     */
    public SalesPanel(Employee employee) {
        // FIX: Validate employee is not null
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null! Login may have failed.");
        }
        
        this.currentEmployee = employee;
        
        // FIX: Initialize selectedEmployeeId from current employee
        // This ensures it's set even if combo box selection doesn't trigger
        this.selectedEmployeeId = employee.getEmployeeId();
        
        initComponents();
        
        try{
            customerService = RMIClientManager.getInstance().getCustomerService();
            employeeService = RMIClientManager.getInstance().getEmployeeService();
            productService = RMIClientManager.getInstance().getProductService();
            salesService = RMIClientManager.getInstance().getSalesService();
        } catch(Exception e ){
            ValidationUtil.showError(this,
                "Failed to connect to server: " + e.getMessage(),
                "Connection Error");
            e.printStackTrace();
        }
        
        loadCustomerIdToCombo();
        loadEmployeeNameToCombo();
        loadProductToCombo();
        setupTable();
        fillTable();
    }
    
    public void setCashierMode(boolean isCashier, int employeeId){
        this.isCashierMode = isCashier;
        this.cashierEmployeeId = employeeId;
        
        if(isCashier) {
            comboEmployee.setEnabled(false);
            
            selectEmployeeInComboBox(employeeId);
//            for(int i = 0; i < comboEmployee.getItemCount(); i++){
//                String item = comboEmployee.getItemAt(i).toString();
//                if(item.startsWith(employeeId +" - ")){
//                    comboEmployee.setSelectedIndex(i);
//                    break;
//                }
//            }
//            
            fillTable();
        }
    }
    
    public void setAdminMode(boolean isAdmin) {
        if(isAdmin) {
            comboEmployee.setEnabled(true);
        }
    }
    
    private void selectEmployeeInComboBox(int employeeId){
        try{
            Employee emp = employeeService.findEmployeeById(employeeId);

            if(emp == null){
                return;
            }

            for(int i = 0; i < comboEmployee.getItemCount(); i++){
                String item = comboEmployee.getItemAt(i).toString();

                if(item.startsWith(employeeId + " - ") || item.contains(emp.getFullName())){
                    comboEmployee.setSelectedIndex(i);
                    return;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    protected void loadCustomerIdToCombo(){
        comboCustomer.removeAllItems();
        
        try{
            List<Customer> customers = customerService.findAllCustomers();

            if(customers != null){
                for(Customer customer : customers){
                    comboCustomer.addItem(String.valueOf(customer.getCustomerId()));
                }
            }
        } catch(Exception e){
            ValidationUtil.showError(this,
                    "Error loading customers: " + e.getMessage(),
                    "Load Error");
            e.printStackTrace();
        }
    }
    
    protected void loadEmployeeNameToCombo(){
        comboEmployee.removeAllItems();
        
        try{
            List<Employee> employees = employeeService.findAllEmployees();
            if(employees != null){
                for(Employee employee : employees){
                    comboEmployee.addItem(employee.getEmployeeId() + " - " + employee.getFullName());
                }
            }
        } catch(Exception e){
            ValidationUtil.showError(this,
                "Error loading employees: " + e.getMessage(),
                "Load Error");
            e.printStackTrace();
        }
    }
    
    protected void loadProductToCombo(){
        comboProduct.removeAllItems();
        
        try{
            List<Product> products = productService.findAllProducts();

            if(products != null){
                for(Product product : products){
                    comboProduct.addItem(product.getProductId() + " - " + product.getProductName());
                }
            }
        }catch(Exception e){
            ValidationUtil.showError(this,
                "Error loading products: " + e.getMessage(),
                "Load Error");
            e.printStackTrace();
        }
    }
    
    private void setupTable(){
        tblModel.addColumn("Sales ID");
        tblModel.addColumn("Customer ID");
        tblModel.addColumn("Employee ID");
        tblModel.addColumn("Product ID");
        tblModel.addColumn("Quantity");
        tblModel.addColumn("Total Amount");
        tblModel.addColumn("Sale Date");
        
        salesTable.setModel(tblModel);
    }
    
    private void fillTable(){
        tblModel.setRowCount(0);
        
        
        try{
            List<Sales> sales;

            if(isCashierMode && cashierEmployeeId > 0){
               sales = salesService.getSalesByEmployee(cashierEmployeeId);
            } else {
                sales = salesService.findAllSales();
            }

            for(Sales sale : sales){
                tblModel.addRow(new Object[]{
                    sale.getSalesId(),
                    sale.getCustomerId(),
                    sale.getEmployeeId(),
                    sale.getProductId(),
                    sale.getQuantity(),
                    sale.getTotalAmount(),
                    sale.getSaleDate()
                });
            }
        } catch(Exception e){
            ValidationUtil.showError(this,
                "Error loading sales: " + e.getMessage(),
                "Load Error");
            e.printStackTrace();
        }
    }
    
    private void loadCashierSales(int employeeId){
        fillTable();
    }
    
    private void refresh(){
        txtQuantity.setText("");
        selectedProductId = 0;
        selectedProductPrice = 0.0;
        selectedProductStock = 0;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnProcess = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        comboEmployee = new javax.swing.JComboBox<>();
        comboCustomer = new javax.swing.JComboBox<>();
        comboProduct = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        salesTable = new javax.swing.JTable();

        jPanel5.setBackground(new java.awt.Color(50, 50, 50));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel8.setText("Sales Management");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(160, 160, 160));
        jLabel9.setText("Customer ID");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(160, 160, 160));
        jLabel10.setText("Select Employee");

        txtQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantityActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(160, 160, 160));
        jLabel11.setText("Quantity");

        btnProcess.setBackground(new java.awt.Color(34, 197, 94));
        btnProcess.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnProcess.setForeground(new java.awt.Color(240, 240, 240));
        btnProcess.setText("Process Sales");
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(160, 160, 160));
        jLabel14.setText("Product");

        comboEmployee.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEmployeeActionPerformed(evt);
            }
        });

        comboCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCustomerActionPerformed(evt);
            }
        });

        comboProduct.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboProductActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14)
                            .addComponent(comboProduct, 0, 500, Short.MAX_VALUE)
                            .addComponent(comboCustomer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(comboEmployee, 0, 500, Short.MAX_VALUE)
                            .addComponent(txtQuantity))
                        .addGap(20, 58, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(472, 472, 472)
                .addComponent(btnProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(109, 109, 109))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(48, 48, 48)
                        .addComponent(jLabel14))
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(btnProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        salesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        salesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salesTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(salesTable);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantityActionPerformed

    private void btnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessActionPerformed
        // Validate customer selection
        if(comboCustomer.getSelectedIndex() <= 0){
            ValidationUtil.showWarning(this,
                "Please select a customer",
                "Customer Required");
            comboCustomer.requestFocus();
            return;
        }
        
        // Validate product selection
        if(comboProduct.getSelectedIndex() <= 0 || selectedProductId == 0){
            ValidationUtil.showWarning(this,
                "Please select a product",
                "Product Required");
            comboProduct.requestFocus();
            return;
        }
        
        // FIX: Validate employee is selected
        if(selectedEmployeeId == 0){
            ValidationUtil.showError(this,
                "Employee ID not set!\n\n" +
                "Please select an employee from the dropdown.",
                "Employee Required");
            comboEmployee.requestFocus();
            return;
        }
        
        String quantityStr = txtQuantity.getText().trim();
        
        ValidationResult quantityValidation = ValidationUtil.validateInteger(quantityStr, "Quantity");
        if(!ValidationUtil.validateAndShow(this, quantityValidation)){
            txtQuantity.requestFocus();
            return;
        }
        
        int quantity = Integer.parseInt(quantityStr);
        
        ValidationResult stockValidation = ValidationUtil.validateSaleQuantity(quantity, selectedProductStock);
        if(!ValidationUtil.validateAndShow(this, stockValidation)){
            txtQuantity.requestFocus();
            return;
        }
        
        String customerSelection = comboCustomer.getSelectedItem().toString();
        int customerId = Integer.parseInt(customerSelection.split(" - ")[0]);
        
        double totalAmount = selectedProductPrice * quantity;
        
        try{
            Integer saleId = salesService.processSale(customerId, selectedEmployeeId, selectedProductId, quantity, totalAmount);
            
            if(saleId != null && saleId > 0){
                ValidationUtil.showSuccess(this,
                    "Sale processed successfully!\n\n" +
                    "Sale ID: " + saleId + "\n" +
                    "Quantity: " + quantity + " units\n" +
                    "Total: RWF " + String.format("%,.2f", totalAmount) + "\n\n" +
                    "Stock has been updated automatically.");
                
                fillTable();
                refresh();
            } else{
                ValidationUtil.showError(this,
                    "Failed to process sale.\n\n" +
                    "Please check the server console for details.\n\n" +
                    "Common issues:\n" +
                    "• Employee not found\n" +
                    "• Product out of stock\n" +
                    "• Invalid price",
                    "Sale Failed");
            }
        }catch(Exception e){
            ValidationUtil.showError(this,
                "Error processing sale: " + e.getMessage() + "\n\n" +
                "Please check:\n" +
                "• Product has sufficient stock\n" +
                "• Customer exists\n" +
                "• Employee is valid\n" +
                "• Server is running",
                "Server Error");
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnProcessActionPerformed

    private void comboCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCustomerActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_comboCustomerActionPerformed

    private void comboProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboProductActionPerformed
        // TODO add your handling code here:
        if(comboProduct.getSelectedIndex() <= 0){
            return;
        }
        
        try{
            String selection = comboProduct.getSelectedItem().toString();
            int productId = Integer.parseInt(selection.split(" - ")[0]);
            
            Product product = productService.findProductById(productId);
            
            if(product != null){
                selectedProductId = product.getProductId();
                selectedProductPrice = product.getPrice();
                selectedProductStock = product.getStockQuantity();
            }
        }catch(Exception e){
            ValidationUtil.showError(this,
                "Error loading product details: " + e.getMessage(),
                "Load Error");
            e.printStackTrace();
        }
    }//GEN-LAST:event_comboProductActionPerformed

    private void comboEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboEmployeeActionPerformed
        // TODO add your handling code here:
        if(comboEmployee.getSelectedIndex() <= 0){
            return;
        }
        
        try{
            
            String selection = comboEmployee.getSelectedItem().toString();
            int employeeId = Integer.parseInt(selection.split(" - ")[0]);
            selectedEmployeeId = employeeId;
        }catch(Exception e){
            ValidationUtil.showError(this,
                "Error loading employee details: " + e.getMessage(),
                "Load Error");
            e.printStackTrace();
        }
    }//GEN-LAST:event_comboEmployeeActionPerformed

    private void salesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_salesTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProcess;
    private javax.swing.JComboBox<String> comboCustomer;
    private javax.swing.JComboBox<String> comboEmployee;
    private javax.swing.JComboBox<String> comboProduct;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable salesTable;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
