package view;

import client.RMIClientManager;
import model.Employee;
import model.EmployeeProfile;
import service.EmployeeService;
import util.ThemeManager;
import util.ValidationUtil;
import util.ValidationUtil.ValidationResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

/**
 * Employee Profile Panel
 * Opens on first login and accessible via profile button
 */
public class EmployeeProfilePanel extends JDialog {
    
    private Employee employee;
    private EmployeeProfile profile;
    private EmployeeService employeeService;
    private boolean isFirstLogin;
    
    // UI Components
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextArea txtAddress;
    private JTextField txtEmergencyContact;
    private JTextField txtEmergencyContactName;
    private JPasswordField txtOldPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    
    // Labels for display-only fields
    private JLabel lblFullName;
    private JLabel lblUsername;
    private JLabel lblRole;
    private JLabel lblHireDate;
    private JLabel lblSalary;
    
    public EmployeeProfilePanel(Frame parent, Employee employee, boolean isFirstLogin) {
        super(parent, "Employee Profile", true);
        this.employee = employee;
        this.isFirstLogin = isFirstLogin;
        
        try {
            this.employeeService = RMIClientManager.getInstance().getEmployeeService();
            loadProfile();
        } catch (RemoteException e) {
            ValidationUtil.showError(this, 
                "Failed to connect to server!\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Please ensure the server is running and try again.",
                "Connection Error");
            
            // Close dialog immediately - cannot function without server connection
            dispose();
            return;
        } catch (Exception e) {
            ValidationUtil.showError(this, 
                "Error initializing profile: " + e.getMessage(),
                "Initialization Error");
            dispose();
            return;
        }
        
        initComponents();
        setupUI();
        loadData();
        
        setSize(800, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(isFirstLogin ? DO_NOTHING_ON_CLOSE : DISPOSE_ON_CLOSE);
        
        if (isFirstLogin) {
            showFirstLoginMessage();
        }
    }
    
    private void loadProfile() throws RemoteException {
        // Try to load existing profile
        this.profile = employeeService.getEmployeeProfile(employee.getEmployeeId());
        if (this.profile == null) {
            this.profile = new EmployeeProfile();
            this.profile.setEmployee(employee);
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Content Panel with Scroll
        JPanel contentPanel = createContentPanel();
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel with buttons
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.ACCENT_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel lblTitle = new JLabel(isFirstLogin ? "Complete Your Profile" : "My Profile");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel(isFirstLogin ? 
            "Please complete your profile information" : 
            "View and update your profile information");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(220, 255, 255));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(ThemeManager.getBackgroundColor());
        
        // Employee Information Section (Read-Only)
        panel.add(createSection("Employee Information", createEmployeeInfoPanel()));
        panel.add(Box.createVerticalStrut(20));
        
        // Contact Information Section
        panel.add(createSection("Contact Information", createContactPanel()));
        panel.add(Box.createVerticalStrut(20));
        
        // Emergency Contact Section
        panel.add(createSection("Emergency Contact", createEmergencyContactPanel()));
        panel.add(Box.createVerticalStrut(20));
        
        // Change Password Section
        panel.add(createSection("Change Password", createPasswordPanel()));
        
        return panel;
    }
    
    private JPanel createSection(String title, JPanel content) {
        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setBackground(ThemeManager.getPanelColor());
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(ThemeManager.getTextColor());
        
        section.add(lblTitle, BorderLayout.NORTH);
        section.add(content, BorderLayout.CENTER);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));
        
        return section;
    }
    
    private JPanel createEmployeeInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 15, 15));
        panel.setBackground(ThemeManager.getPanelColor());
        
        lblFullName = new JLabel();
        lblUsername = new JLabel();
        lblRole = new JLabel();
        lblHireDate = new JLabel();
        lblSalary = new JLabel();
        
        panel.add(createLabel("Full Name:"));
        panel.add(lblFullName);
        panel.add(createLabel("Username:"));
        panel.add(lblUsername);
        panel.add(createLabel("Role:"));
        panel.add(lblRole);
        panel.add(createLabel("Hire Date:"));
        panel.add(lblHireDate);
        panel.add(createLabel("Salary:"));
        panel.add(lblSalary);
        
        return panel;
    }
    
    private JPanel createContactPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBackground(ThemeManager.getPanelColor());
        
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);
        
        panel.add(createLabel("Email:"));
        panel.add(txtEmail);
        panel.add(createLabel("Phone:"));
        panel.add(txtPhone);
        panel.add(createLabel("Address:"));
        panel.add(scrollAddress);
        
        return panel;
    }
    
    private JPanel createEmergencyContactPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(ThemeManager.getPanelColor());
        
        txtEmergencyContactName = new JTextField();
        txtEmergencyContact = new JTextField();
        
        panel.add(createLabel("Emergency Contact Name:"));
        panel.add(txtEmergencyContactName);
        panel.add(createLabel("Emergency Contact Phone:"));
        panel.add(txtEmergencyContact);
        
        return panel;
    }
    
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBackground(ThemeManager.getPanelColor());
        
        txtOldPassword = new JPasswordField();
        txtNewPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        
        panel.add(createLabel("Current Password:"));
        panel.add(txtOldPassword);
        panel.add(createLabel("New Password:"));
        panel.add(txtNewPassword);
        panel.add(createLabel("Confirm Password:"));
        panel.add(txtConfirmPassword);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(ThemeManager.getTextSecondaryColor());
        return label;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(ThemeManager.getPanelColor());
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.getBorderColor()));
        
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        
        btnSave.setPreferredSize(new Dimension(150, 40));
        btnCancel.setPreferredSize(new Dimension(150, 40));
        
        ThemeManager.stylePrimaryButton(btnSave);
        ThemeManager.styleSecondaryButton(btnCancel);
        
        btnSave.addActionListener(e -> saveProfile());
        btnCancel.addActionListener(e -> handleCancel());
        
        panel.add(btnCancel);
        panel.add(btnSave);
        
        return panel;
    }
    
    private void loadData() {
        // Load employee info
        lblFullName.setText(employee.getFullName());
        lblUsername.setText(employee.getUsername());
        lblRole.setText(employee.getRole());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        lblHireDate.setText(employee.getHireDate() != null ? sdf.format(employee.getHireDate()) : "N/A");
        lblSalary.setText(String.format("RWF %,.2f", employee.getSalary()));
        
        // Load profile info if exists
        if (profile != null) {
            txtEmail.setText(profile.getEmail() != null ? profile.getEmail() : "");
            txtPhone.setText(profile.getPhone() != null ? profile.getPhone() : "");
            txtAddress.setText(profile.getAddress() != null ? profile.getAddress() : "");
            txtEmergencyContactName.setText(profile.getEmergencyContactName() != null ? profile.getEmergencyContactName() : "");
            txtEmergencyContact.setText(profile.getEmergencyContactPhone() != null ? profile.getEmergencyContactPhone() : "");
        }
        
        // Apply theme
        ThemeManager.applyTheme(this);
    }
    
    private void saveProfile() {
        try {
            // Validate inputs
            ValidationResult emailValidation = ValidationUtil.validateEmail(txtEmail.getText().trim());
            if (!ValidationUtil.validateAndShow(this, emailValidation)) {
                txtEmail.requestFocus();
                return;
            }
            
            ValidationResult phoneValidation = ValidationUtil.validatePhone(txtPhone.getText().trim());
            if (!ValidationUtil.validateAndShow(this, phoneValidation)) {
                txtPhone.requestFocus();
                return;
            }
            
            // Update profile
            profile.setEmail(txtEmail.getText().trim());
            profile.setPhone(txtPhone.getText().trim());
            profile.setAddress(txtAddress.getText().trim());
            profile.setEmergencyContactName(txtEmergencyContactName.getText().trim());
            profile.setEmergencyContactPhone(txtEmergencyContact.getText().trim());
            
            // Handle password change if provided
            String oldPassword = new String(txtOldPassword.getPassword());
            String newPassword = new String(txtNewPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());
            
            if (!oldPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                if (!validatePasswordChange(oldPassword, newPassword, confirmPassword)) {
                    return;
                }
                // Update password via service
                boolean passwordUpdated = employeeService.changePassword(
                    employee.getEmployeeId(), oldPassword, newPassword);
                if (!passwordUpdated) {
                    ValidationUtil.showError(this, "Current password is incorrect", "Password Error");
                    txtOldPassword.requestFocus();
                    return;
                }
            }
            
            // Save profile
            boolean saved = employeeService.saveEmployeeProfile(profile);
            
            if (saved) {
                // Clear password fields for security
                txtOldPassword.setText("");
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
                
                // Mark first login complete if this is first login
                if (isFirstLogin) {
                    try {
                        employeeService.markFirstLoginComplete(employee.getEmployeeId());
                        employee.setFirstLogin(false); // Update in-memory object
                        System.out.println("First login marked as complete for employee: " + employee.getEmployeeId());
                    } catch (RemoteException ex) {
                        System.err.println("Failed to mark first login complete: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                
                ValidationUtil.showSuccess(this, "Profile updated successfully!");
                dispose();
            } else {
                ValidationUtil.showError(this, "Failed to save profile. Please try again.", "Save Error");
            }
            
        } catch (RemoteException e) {
            ValidationUtil.showError(this, "Error saving profile: " + e.getMessage(), "Server Error");
            e.printStackTrace();
        }
    }
    
    private boolean validatePasswordChange(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty()) {
            ValidationUtil.showError(this, "Please enter your current password", "Validation Error");
            txtOldPassword.requestFocus();
            return false;
        }
        
        ValidationResult newPassValidation = ValidationUtil.validatePassword(newPassword);
        if (!ValidationUtil.validateAndShow(this, newPassValidation)) {
            txtNewPassword.requestFocus();
            return false;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            ValidationUtil.showError(this, "New passwords do not match", "Validation Error");
            txtConfirmPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void handleCancel() {
        if (isFirstLogin) {
            int choice = JOptionPane.showConfirmDialog(this,
                "You must complete your profile on first login.\nAre you sure you want to logout?",
                "Incomplete Profile",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                // Close parent dashboard
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (window instanceof MainDashboard) {
                        window.dispose();
                    }
                }
            }
        } else {
            dispose();
        }
    }
    
    private void showFirstLoginMessage() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Welcome to Epignosis Supermarket!\n\n" +
                "This is your first login. Please complete your profile information.\n" +
                "This helps us keep your contact details up to date.",
                "First Login - Complete Profile",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void setupUI() {
        // Add ESC key to close (only if not first login)
        if (!isFirstLogin) {
            KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
    }
}
