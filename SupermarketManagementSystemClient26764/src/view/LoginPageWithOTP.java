package view;

import client.RMIClientManager;
import model.Employee;
import model.UserSession;
import service.AuthService;
import service.SessionService;
import util.ValidationUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPageWithOTP extends JFrame {
    
    private static final Color DARK_BG = new Color(30, 30, 30);
    private static final Color ACCENT_TEAL = new Color(20, 184, 166);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 240);
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtOTP;
    private JButton btnRequestOTP;
    private JButton btnLogin;
    private JButton btnResendOTP;
    private JLabel lblStatus;
    private JPanel otpPanel;
    
    private AuthService authService;
    private SessionService sessionService;
    private boolean otpSent = false;
    
    public LoginPageWithOTP() {
        initializeServices();
        initComponents();
        setupUI();
    }
    
    private void initializeServices() {
        try {
            authService = RMIClientManager.getInstance().getAuthService();
            sessionService = RMIClientManager.getInstance().getSessionService();
            
            if(authService == null || sessionService == null){
                throw new Exception("Auth or Session service not available");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to connect to server!\n" + e.getMessage() +
                "\n\nPlease ensure:\n" +
                "1. Server is running\n" +
                "2. ActiveMQ is running\n" +
                "3. Port 3500 is accessible",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initComponents() {
        setTitle("Epignosis Supermarket - Login");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(DARK_BG);
        
        // Title
        JLabel lblTitle = new JLabel("EPIGNOSIS SUPERMARKET");
        lblTitle.setBounds(50, 30, 350, 40);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(ACCENT_TEAL);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Management System");
        lblSubtitle.setBounds(50, 70, 350, 20);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_PRIMARY);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblSubtitle);
        
        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 120, 100, 25);
        lblUsername.setForeground(TEXT_PRIMARY);
        mainPanel.add(lblUsername);
        
        txtUsername = new JTextField();
        txtUsername.setBounds(50, 145, 350, 40);
        styleTextField(txtUsername);
        mainPanel.add(txtUsername);
        
        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 195, 100, 25);
        lblPassword.setForeground(TEXT_PRIMARY);
        mainPanel.add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 220, 350, 40);
        styleTextField(txtPassword);
        mainPanel.add(txtPassword);
        
        // Request OTP Button
        btnRequestOTP = new JButton("Request OTP");
        btnRequestOTP.setBounds(50, 280, 350, 45);
        styleButton(btnRequestOTP, ACCENT_TEAL);
        btnRequestOTP.addActionListener(e -> requestOTP());
        mainPanel.add(btnRequestOTP);
        
        // OTP Panel (initially hidden)
        otpPanel = new JPanel();
        otpPanel.setLayout(null);
        otpPanel.setBackground(DARK_BG);
        otpPanel.setBounds(50, 340, 350, 150);
        otpPanel.setVisible(false);
        
        JLabel lblOTP = new JLabel("Enter OTP:");
        lblOTP.setBounds(0, 0, 100, 25);
        lblOTP.setForeground(TEXT_PRIMARY);
        otpPanel.add(lblOTP);
        
        txtOTP = new JTextField();
        txtOTP.setBounds(0, 25, 350, 40);
        styleTextField(txtOTP);
        txtOTP.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                // Only allow digits and limit to 6 characters
                if (!Character.isDigit(e.getKeyChar()) || txtOTP.getText().length() >= 6) {
                    e.consume();
                }
            }
        });
        otpPanel.add(txtOTP);
        
        btnLogin = new JButton("Verify OTP & Login");
        btnLogin.setBounds(0, 75, 170, 40);
        styleButton(btnLogin, ACCENT_TEAL);
        btnLogin.addActionListener(e -> verifyOTPAndLogin());
        otpPanel.add(btnLogin);
        
        btnResendOTP = new JButton("Resend OTP");
        btnResendOTP.setBounds(180, 75, 170, 40);
        styleButton(btnResendOTP, new Color(100, 100, 100));
        btnResendOTP.addActionListener(e -> resendOTP());
        otpPanel.add(btnResendOTP);
        
        mainPanel.add(otpPanel);
        
        // Status Label
        lblStatus = new JLabel("");
        lblStatus.setBounds(50, 510, 350, 30);
        lblStatus.setForeground(ACCENT_TEAL);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblStatus);
        
        add(mainPanel);
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(45, 45, 45));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_TEAL, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void setupUI() {
        // Enter key listeners
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> {
            if (!otpSent) {
                requestOTP();
            }
        });
        txtOTP.addActionListener(e -> verifyOTPAndLogin());
    }
    
    private void requestOTP() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        
        // Validate inputs
        if (username.isEmpty()) {
            showStatus("Please enter username", Color.RED);
            return;
        }
        
        if (password.isEmpty()) {
            showStatus("Please enter password", Color.RED);
            return;
        }
        
        // Disable button and show loading
        btnRequestOTP.setEnabled(false);
        btnRequestOTP.setText("Sending OTP...");
        showStatus("Requesting OTP...", ACCENT_TEAL);
        
        // Request OTP in background thread
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.requestOTP(username);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        otpSent = true;
                        otpPanel.setVisible(true);
                        btnRequestOTP.setEnabled(false);
                        btnRequestOTP.setText("OTP Sent ✓");
                        showStatus("OTP sent to your email! Check your inbox.", ACCENT_TEAL);
                        txtOTP.requestFocus();
                        
                        // Adjust window size
                        setSize(450, 750);
                        setLocationRelativeTo(null);
                    } else {
                        showStatus("Failed to send OTP. Check username.", Color.RED);
                        btnRequestOTP.setEnabled(true);
                        btnRequestOTP.setText("Request OTP");
                    }
                } catch (Exception e) {
                    showStatus("Error: " + e.getMessage(), Color.RED);
                    btnRequestOTP.setEnabled(true);
                    btnRequestOTP.setText("Request OTP");
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void verifyOTPAndLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String otp = txtOTP.getText().trim();
        
        // Validate OTP
        if (otp.isEmpty() || otp.length() != 6) {
            showStatus("Please enter 6-digit OTP", Color.RED);
            return;
        }
        
        // Disable button and show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Verifying...");
        showStatus("Verifying OTP...", ACCENT_TEAL);
        
        // Verify OTP in background thread
        new SwingWorker<Employee, Void>() {
            @Override
            protected Employee doInBackground() throws Exception {
                return authService.verifyOTPAndLogin(username, password, otp);
            }
            
            @Override
            protected void done() {
                try {
                    Employee employee = get();
                    
                    if (employee != null) {
                        // Login successful - create session
                        String sessionId = sessionService.createSession(
                            employee.getUsername(),
                            employee.getFullName(),
                            employee.getEmployeeId(),
                            employee.getRole()
                        );
                        
                        // Store session
                        RMIClientManager.getInstance().setCurrentSessionId(sessionId);
                        RMIClientManager.getInstance().setCurrentEmployee(employee);
                        
                        // Get session details
                        UserSession session = sessionService.getSessionDetails(sessionId);
                        RMIClientManager.getInstance().setCurrentSession(session);
                        
                        showStatus("Login successful! Opening Dashboard...", ACCENT_TEAL);
                        
                        // Open main dashboard
                        SwingUtilities.invokeLater(() -> {
                            MainDashboard dashboard = new MainDashboard(employee);
                            dashboard.setVisible(true);
                            dispose();
                        });
                    } else {
                        showStatus("Invalid OTP or password!", Color.RED);
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Verify OTP & Login");
                        txtOTP.setText("");
                        txtOTP.requestFocus();
                    }
                } catch (Exception e) {
                    showStatus("Error: " + e.getMessage(), Color.RED);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Verify OTP & Login");
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void resendOTP() {
        String username = txtUsername.getText().trim();
        
        btnResendOTP.setEnabled(false);
        btnResendOTP.setText("Resending...");
        showStatus("Resending OTP...", ACCENT_TEAL);
        
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.resendOTP(username);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        showStatus("OTP resent successfully!", ACCENT_TEAL);
                        txtOTP.setText("");
                        txtOTP.requestFocus();
                    } else {
                        showStatus("Failed to resend OTP", Color.RED);
                    }
                    
                    btnResendOTP.setEnabled(true);
                    btnResendOTP.setText("Resend OTP");
                } catch (Exception e) {
                    showStatus("Error: " + e.getMessage(), Color.RED);
                    btnResendOTP.setEnabled(true);
                    btnResendOTP.setText("Resend OTP");
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void showStatus(String message, Color color) {
        lblStatus.setText(message);
        lblStatus.setForeground(color);
    }
    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new LoginPageWithOTP().setVisible(true);
//        });
//    }
}