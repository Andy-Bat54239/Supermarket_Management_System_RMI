package view;

import client.RMIClientManager;
import util.ValidationUtil;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import model.Employee;



/**
 *
 * @author andyb
 */
public class MainDashboard extends javax.swing.JFrame {
    
    private CardLayout cardLayout;
    private JButton activeButton;
    private javax.swing.Timer sessionCheckTimer;
    private JLabel lblSessionStatus;
    
    private DashboardPanel dashboardPanel;
    private SalesPanel salesPanel;
    private ProductsPanel productsPanel;
    private EmployeePanel employeePanel;
    private CustomerPanel customerPanel;
    private ReportsPanel reportsPanel;
    
    private Employee loggedInEmployee;
    
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainDashboard.class.getName());

    /**
     * Creates new form MainDashboard
     */
    public MainDashboard(Employee employee) {
        this.loggedInEmployee = employee;
        initComponents();
        RMIClientManager.getInstance().setCurrentEmployee(employee);
        startSessionMonitoring();
        System.out.println("Session monitoring started");
        addSessionStatusIndicator();
        setupPanels();
        configureRoleBasedAccess();
        
        setTitle("Supermarket Management System - "+ employee.getFullName()+"("+ employee.getRole()+")");
        
        logger.info("Dashboard opened for: " + employee.getFullName() + "(Role: " + employee.getRole() + ")");
    }
    
    private void startSessionMonitoring(){
        System.out.println("=== STARTING SESSION MONITORING ===");
        sessionCheckTimer = new javax.swing.Timer(10000, e -> {
            System.out.println("\n=== TIMER CHECK at " + new java.util.Date() + " ===");
            try{
                boolean valid = RMIClientManager.getInstance().validateSession();
                System.out.println("Session valid: " + valid);
                if(!valid){
                    System.out.println("SESSION EXPIRED - LOGGING OUT!");
                    sessionCheckTimer.stop();
                    
                    ValidationUtil.showWarning(this,
                            "Your session has expired. Please login again",
                            "Session Expired");
                    RMIClientManager.getInstance().clearSession();
                    new LoginPageWithOTP().setVisible(true);
                    dispose();
                }
            }catch(Exception ex){
                System.out.println("ERROR: " + ex.getMessage());
                ex.printStackTrace();
            } 
        });
        
        sessionCheckTimer.start();
        System.out.println("=== TIMER STARTED - Check every 10 seconds ===\n");
    }
    
    private void addSessionStatusIndicator(){
        lblSessionStatus = new JLabel("● Session Active");
        lblSessionStatus.setForeground(new Color(20,184,166));
        lblSessionStatus.setFont(new Font("Arial", Font.BOLD, 11));
    }
    
    private void setupPanels(){
        cardLayout = (CardLayout) contentPanel.getLayout();
        
        //Create panel instances
        dashboardPanel = new DashboardPanel(loggedInEmployee);
        salesPanel = new SalesPanel(loggedInEmployee);
        productsPanel = new ProductsPanel(salesPanel, loggedInEmployee);
        employeePanel = new EmployeePanel(salesPanel, loggedInEmployee);
        customerPanel = new CustomerPanel(salesPanel, loggedInEmployee);
        reportsPanel = new ReportsPanel(loggedInEmployee.getFullName());
        
        
        
        //Add Panels to content Panel
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(productsPanel, "PRODUCTS");
        contentPanel.add(employeePanel, "EMPLOYEES");
        contentPanel.add(customerPanel, "CUSTOMERS");
        contentPanel.add(salesPanel, "SALES");
        contentPanel.add(reportsPanel,"REPORT");
        
        //Show Dashboard By Default
        cardLayout.show(contentPanel, "DASHBOARD");
        activeButton = btnDashboard;
        setActiveButton(btnDashboard);
    }
    
    private void switchPage(String pageName){
        if(pageName.equals("EMPLOYEES") && loggedInEmployee.isCashier()){
            ValidationUtil.showWarning(this,
                    "Access Denied!\n\n" +
                    "Cashiers do not have permission to access Employee Management" +
                    "This feature is only available to administrators.",
                    "Access Denied");
            return;
        }
        cardLayout.show(contentPanel, pageName);
        
        logger.info("User " + loggedInEmployee.getFullName() + 
                   " switched to page: " + pageName);
    }
    
    private void setActiveButton(JButton button){
        if(activeButton != null){
            activeButton.setBackground(new Color(40, 40, 40));
            activeButton.setForeground(new Color(160, 160, 160));
        }
        activeButton = button;
        button.setBackground(new Color(20, 184, 166));
        button.setForeground(Color.WHITE);
    }
    
    private void configureRoleBasedAccess(){
        if(loggedInEmployee.isCashier()){
            
            btnEmployee.setVisible(false);
            
            productsPanel.setCashierMode(true);
            
            customerPanel.setCashierMode(true);
            
            salesPanel.setCashierMode(true, loggedInEmployee.getEmployeeId());
            
            dashboardPanel.setCashierMode(true, loggedInEmployee.getEmployeeId());
        } else if(loggedInEmployee.isAdmin()){
            System.out.println("Admin mmode activated");
            
            productsPanel.setAdminMode(true);
            employeePanel.setAdminMode(true);
            customerPanel.setAdminMode(true);
            salesPanel.setAdminMode(true);
            dashboardPanel.setAdminMode(true);
        }
    }
    
    private void logout(){
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirmation Logout",
                JOptionPane.YES_NO_OPTION);
        
        if(confirm == JOptionPane.YES_OPTION){
            try{
                String sessionId = RMIClientManager.getInstance().getCurrentSessionId();
                if(sessionId != null){
                    RMIClientManager.getInstance().getSessionService().terminateSession(sessionId);
                }
                
                RMIClientManager.getInstance().clearSession();
                
                if(sessionCheckTimer != null){
                    sessionCheckTimer.stop();
                }
                
                new LoginPageWithOTP().setVisible(true);
                dispose();
            } catch(Exception e){
                e.printStackTrace();
                RMIClientManager.getInstance().clearSession();
                new LoginPageWithOTP().setVisible(true);
                dispose();
            }
        }
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel11 = new javax.swing.JLabel();
        topBarPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        sideBarPanel = new javax.swing.JPanel();
        logoPanel = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5));
        btnDashboard = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5));
        btnEmployee = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5));
        btnCustomer = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5));
        btnSales = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5));
        btnLogout = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        jLabel11.setText("jLabel11");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1400, 210));

        topBarPanel.setBackground(new java.awt.Color(40, 40, 40));
        topBarPanel.setPreferredSize(new java.awt.Dimension(1400, 60));
        topBarPanel.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(240, 240, 240));
        lblTitle.setText("Supermarket Management System");
        topBarPanel.add(lblTitle, java.awt.BorderLayout.LINE_START);

        getContentPane().add(topBarPanel, java.awt.BorderLayout.PAGE_START);

        sideBarPanel.setBackground(new java.awt.Color(40, 40, 40));
        sideBarPanel.setMaximumSize(new java.awt.Dimension(250, 150));
        sideBarPanel.setPreferredSize(new java.awt.Dimension(250, 150));

        logoPanel.setBackground(new java.awt.Color(40, 40, 40));
        logoPanel.setMaximumSize(new java.awt.Dimension(250, 100));

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(240, 240, 240));
        lblLogo.setIcon(new javax.swing.ImageIcon("E:\\icons8-shopping-cart-50.png")); // NOI18N
        lblLogo.setText("Epignosis Supermarket");
        logoPanel.add(lblLogo);

        btnDashboard.setBackground(new java.awt.Color(40, 40, 40));
        btnDashboard.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(160, 160, 160));
        btnDashboard.setIcon(new javax.swing.ImageIcon("E:\\icons8-dashboard-50.png")); // NOI18N
        btnDashboard.setText("Dashboard");
        btnDashboard.setBorderPainted(false);
        btnDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDashboard.setFocusPainted(false);
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.setMaximumSize(new java.awt.Dimension(250, 45));
        btnDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboardActionPerformed(evt);
            }
        });

        btnProducts.setBackground(new java.awt.Color(40, 40, 40));
        btnProducts.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnProducts.setForeground(new java.awt.Color(160, 160, 160));
        btnProducts.setIcon(new javax.swing.ImageIcon("E:\\icons8-products-50.png")); // NOI18N
        btnProducts.setText("Products");
        btnProducts.setBorderPainted(false);
        btnProducts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProducts.setFocusPainted(false);
        btnProducts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProducts.setMaximumSize(new java.awt.Dimension(250, 45));
        btnProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductsActionPerformed(evt);
            }
        });

        btnEmployee.setBackground(new java.awt.Color(40, 40, 40));
        btnEmployee.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEmployee.setForeground(new java.awt.Color(160, 160, 160));
        btnEmployee.setIcon(new javax.swing.ImageIcon("E:\\icons8-employee-50.png")); // NOI18N
        btnEmployee.setText("Employee");
        btnEmployee.setBorderPainted(false);
        btnEmployee.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEmployee.setFocusPainted(false);
        btnEmployee.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEmployee.setMaximumSize(new java.awt.Dimension(250, 45));
        btnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeActionPerformed(evt);
            }
        });

        btnCustomer.setBackground(new java.awt.Color(40, 40, 40));
        btnCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnCustomer.setForeground(new java.awt.Color(160, 160, 160));
        btnCustomer.setIcon(new javax.swing.ImageIcon("E:\\icons8-customer-50.png")); // NOI18N
        btnCustomer.setText("Customer");
        btnCustomer.setBorderPainted(false);
        btnCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCustomer.setFocusPainted(false);
        btnCustomer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCustomer.setMaximumSize(new java.awt.Dimension(250, 45));
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });

        btnSales.setBackground(new java.awt.Color(40, 40, 40));
        btnSales.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSales.setForeground(new java.awt.Color(160, 160, 160));
        btnSales.setIcon(new javax.swing.ImageIcon("E:\\icons8-total-sales-50.png")); // NOI18N
        btnSales.setText("Sales");
        btnSales.setBorderPainted(false);
        btnSales.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSales.setFocusPainted(false);
        btnSales.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSales.setMaximumSize(new java.awt.Dimension(250, 45));
        btnSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalesActionPerformed(evt);
            }
        });

        btnLogout.setBackground(new java.awt.Color(40, 40, 40));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(160, 160, 160));
        btnLogout.setIcon(new javax.swing.ImageIcon("E:\\icons8-logout-50.png")); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.setMaximumSize(new java.awt.Dimension(250, 45));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(40, 40, 40));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(160, 160, 160));
        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\andyb\\Downloads\\icons8-report-48.png")); // NOI18N
        jButton1.setText("Report");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sideBarPanelLayout = new javax.swing.GroupLayout(sideBarPanel);
        sideBarPanel.setLayout(sideBarPanelLayout);
        sideBarPanelLayout.setHorizontalGroup(
            sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnEmployee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnProducts, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(sideBarPanelLayout.createSequentialGroup()
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(sideBarPanelLayout.createSequentialGroup()
                .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sideBarPanelLayout.createSequentialGroup()
                        .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSales, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        sideBarPanelLayout.setVerticalGroup(
            sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarPanelLayout.createSequentialGroup()
                .addComponent(logoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 256, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(sideBarPanel, java.awt.BorderLayout.LINE_START);

        contentPanel.setBackground(new java.awt.Color(30, 30, 30));
        contentPanel.setLayout(new java.awt.CardLayout());
        getContentPane().add(contentPanel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Logout");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem3.setText("About");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        // TODO add your handling code here:
        switchPage("PRODUCTS");
        setActiveButton(btnProducts);
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        // TODO add your handling code here:
        switchPage("DASHBOARD");
        dashboardPanel.loadDashboardStats();
        setActiveButton(btnDashboard);
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        // TODO add your handling code here:
        switchPage("EMPLOYEES");
        setActiveButton(btnEmployee);
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
        // TODO add your handling code here:
        switchPage("CUSTOMERS");
        setActiveButton(btnCustomer);
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalesActionPerformed
        // TODO add your handling code here:
        switchPage("SALES");
        setActiveButton(btnSales);
    }//GEN-LAST:event_btnSalesActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        boolean confirmed = ValidationUtil.showConfirmation(this,
                "Are you sure you want to exit the application?",
                "Exit Confirmation");
        
        if(confirmed) {
            logger.info("User " + loggedInEmployee.getFullName() + " exited application");
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        handleLogout();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this,
                "Supermarket Management System\n\n" +
                "Version: 2.0(RMI + Hibernate)\n" +
                "Developed by: Biyonga Bahati Andy\n\n" +
                "Current User: " + loggedInEmployee.getFullName() + "\n"+
                "Role: " + loggedInEmployee.getRole(),
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        try{
            String sessionId = RMIClientManager.getInstance().getCurrentSessionId();
            
            if(sessionId != null){
                RMIClientManager.getInstance().getSessionService().terminateSession(sessionId);
            }
            
            RMIClientManager.getInstance().clearSession();
            
            if(sessionCheckTimer != null){
                sessionCheckTimer.stop();
            }
            
            new LoginPageWithOTP().setVisible(true);
            dispose();
        } catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        switchPage("REPORT");
        setActiveButton(jButton1);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            UIManager.setLookAndFeel(new FlatDarkLaf());
//               
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> new MainDashboard().setVisible(true));
//    }
    
    private void handleLogout(){
        boolean confirmed = ValidationUtil.showConfirmation(this,
                "Are you sure you want to logout?\n\n" +
                "Current session for " + loggedInEmployee.getFullName() + "will be terminated.",
                "Logout Confirmation");
        
        if(confirmed){
            logger.info("User " + loggedInEmployee.getFullName() + " logged out");
            
            dispose();
            
            LoginPage login = new LoginPage();
            login.setVisible(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnSales;
    private javax.swing.JPanel contentPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel sideBarPanel;
    private javax.swing.JPanel topBarPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void dispose(){
        if(sessionCheckTimer != null) {
            sessionCheckTimer.stop();
        }
        super.dispose();
    }

}
