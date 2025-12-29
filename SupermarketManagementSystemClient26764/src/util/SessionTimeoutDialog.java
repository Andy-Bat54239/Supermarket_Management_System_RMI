package util;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author andyb
 */
public class SessionTimeoutDialog extends JDialog {
    
    private boolean extendSession = false;
    
    public SessionTimeoutDialog(JFrame parent) {
        super(parent, "Session Timeout Warning", true);
        initComponents();
    }
    
    private void initComponents() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        
        // Warning message
        JLabel lblMessage = new JLabel(
            "<html><center>Your session will expire in 2 minutes due to inactivity.<br>" +
            "Would you like to extend your session?</center></html>"
        );
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblMessage, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnExtend = new JButton("Extend Session");
        btnExtend.addActionListener(e -> {
            extendSession = true;
            dispose();
        });
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            extendSession = false;
            dispose();
        });
        
        buttonPanel.add(btnExtend);
        buttonPanel.add(btnLogout);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public boolean shouldExtendSession() {
        return extendSession;
    }
}