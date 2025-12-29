package view;

import service.EmployeeService;
import client.RMIClientManager;
import java.awt.BorderLayout;
import util.ValidationUtil;
import util.ValidationUtil.ValidationResult;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import model.Employee;
import util.RMIConnectionIndicator;

/**
 *
 * @author andyb
 */
public class LoginPage extends javax.swing.JFrame {
    
    private static final Color DARK_BG = new Color(30, 30, 30);
    private static final Color ACCENT_TEAL = new Color(20, 184, 166);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 240);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 160);
    private static final Color BUTTON_HOVER = new Color(60, 60, 60);
    private RMIConnectionIndicator connectionIndicator;
    
    private EmployeeService employeeService;
    
    private static final Logger logger = Logger.getLogger(LoginPage.class.getName());

    /**
     * Creates new form LoginPage
     */
    public LoginPage() {
        initComponents();
        
        connectionIndicator = new RMIConnectionIndicator();
        
        // Add to bottom of main panel
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        bottomPanel.setBackground(new java.awt.Color(30, 30, 30));
        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        bottomPanel.add(connectionIndicator);
        
        // Add to frame
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        
        try{
            employeeService = RMIClientManager.getInstance().getEmployeeService();
        }catch(Exception e){
            ValidationUtil.showError(this,
                "Failed to connect to server!\n\n" +
                "Please ensure the RMI server is running and try again.\n\n" +
                "Error: " + e.getMessage(),
                "Connection Error");
            logger.severe("Failed to connect to RMI server: " + e.getMessage());
            e.printStackTrace();
            
            btnLogin.setEnabled(false);
            btnLogin.setToolTipText("Server Offline");
        }
        
        setupTextFieldStyling();
        setupButtonHover();
//        setupButtonAction();
    }
    
    private void setupTextFieldStyling(){
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_TEAL, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        txtUsername.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                if (txtUsername.getText().equals("Username")){
                    txtUsername.setText("");
                }
            }
            
            public void focusLost(FocusEvent e){
                if (txtUsername.getText().isEmpty()){
                    txtUsername.setText("Username");
                }
            }
        });
        
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_TEAL, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        txtPassword.setEchoChar((char) 0);
        
        txtPassword.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                if(String.valueOf(txtPassword.getPassword()).equals("Password")){
                    txtPassword.setText("");
                    txtPassword.setEchoChar('*');
                }
            }
            
            public void focusLost(FocusEvent e){
                if(String.valueOf(txtPassword.getPassword()).isEmpty()){
                    txtPassword.setText("Password");
                    txtPassword.setEchoChar((char) 0);
                }
            }
        });
    }
    
    private void setupButtonHover(){
        btnLogin.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){
                btnLogin.setBackground(ACCENT_TEAL.darker());
            }
            
            public void mouseExited(MouseEvent e){
                btnLogin.setBackground(ACCENT_TEAL);
            }
        });
    }
    
//    private void setupButtonAction(){
//        btnLogin.addActionListener(e -> loginAction());
//    }
    
    private void loginAction(){
        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword());
        
        if(username.isEmpty() || username.equals("Username")){
            ValidationUtil.showWarning(this,
                    "Please enter your username",
                    "Username Required");
            txtUsername.requestFocus();
            return;
        }
        
        ValidationResult usernameValidation = ValidationUtil.validateUsername(username);
        if(!ValidationUtil.validateAndShow(this, usernameValidation)){
            txtUsername.requestFocus();
            return;
        }
        
        if(password.isEmpty() || password.equals("Password")){
            JOptionPane.showMessageDialog(this,
                    "Please Enter your password",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        
        // SECURITY FIX: Removed hardcoded admin/admin bypass
        // All authentication MUST go through server
        
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);
        btnLogin.setText("Authenticating...");
        
        
        
        try{
            Employee employee = employeeService.authenticate(username, password);
            
            if(employee != null){
                ValidationUtil.showSuccess(this,
                        "Login successful!\n\n" +
                        "Welcome, " + employee.getFullName() + "\n" + "Role: " + employee.getRole());
                logger.info("Login successful: " + username + "(" + employee.getRole() + ")");
                
                dispose();
                
                MainDashboard dashboard = new MainDashboard(employee);
                dashboard.setVisible(true);
                
                // FIX: Check for first login and show profile dialog
                if(employee.isFirstLogin() && employee.getFirstLogin() != null && employee.getFirstLogin()) {
                    try {
                        EmployeeProfilePanel profilePanel = new EmployeeProfilePanel(dashboard, employee, true);
                        profilePanel.setVisible(true);
                    } catch(Exception ex) {
                        logger.warning("Could not open employee profile panel: " + ex.getMessage());
                    }
                }
            } else {
                ValidationUtil.showError(this,
                    "Invalid username or password\n\n" +
                    "Please check your credentials and try again.\n\n" +
                    "Hint: Usernames and passwords are case-sensitive.",
                    "Login Failed");
                
                logger.warning("Failed login attempt for username: " + username);
                
                // FIX: Properly clear password field
                txtPassword.setText("");
                txtPassword.setEchoChar('•');
                txtPassword.requestFocus();
            }
        }catch(Exception e){
             ValidationUtil.showError(this,
                "Unable to connect to server!\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Please contact your system administrator.",
                "Server Error");
            
            logger.severe("Authentication error: " + e.getMessage());
            e.printStackTrace();
        } finally{
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
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

        mainPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30));
        txtUsername = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20));
        txtPassword = new javax.swing.JPasswordField();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30));
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login - Epignosis Supermarket");
        setPreferredSize(new java.awt.Dimension(641, 450));
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(30, 30, 30));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 50, 30, 50));

        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(20, 184, 166));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Epignosis Supermarket Login");

        txtUsername.setBackground(new java.awt.Color(60, 60, 60));
        txtUsername.setForeground(new java.awt.Color(240, 240, 240));
        txtUsername.setText("Username");

        txtPassword.setBackground(new java.awt.Color(60, 60, 60));
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(240, 240, 240));
        txtPassword.setText("Password");

        btnLogin.setBackground(new java.awt.Color(20, 184, 166));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new java.awt.Dimension(120, 45));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleLabel))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(209, 209, 209)
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(74, 74, 74)
                                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(45, 45, 45))
                    .addComponent(btnLogin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // TODO add your handling code here:
        loginAction();
    }//GEN-LAST:event_btnLoginActionPerformed

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
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> new LoginPage().setVisible(true));
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
