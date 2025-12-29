package util;

import client.RMIClientManager;
import javax.swing.*;
import java.awt.*;

/**
 * RMI Connection Indicator
 * Visual component to show RMI connection status in UI
 */
public class RMIConnectionIndicator extends JPanel {
    
    private JLabel statusIcon;
    private JLabel statusText;
    private ConnectionStatus currentStatus;
    
    /**
     * Connection status enum
     */
    public enum ConnectionStatus {
        CONNECTED("✓ Connected", new Color(34, 197, 94)),      // Green
        DISCONNECTED("✗ Disconnected", new Color(239, 68, 68)), // Red
        CONNECTING("⟳ Connecting...", new Color(234, 179, 8));  // Yellow
        
        private final String displayText;
        private final Color color;
        
        ConnectionStatus(String displayText, Color color) {
            this.displayText = displayText;
            this.color = color;
        }
        
        public String getDisplayText() {
            return displayText;
        }
        
        public Color getColor() {
            return color;
        }
    }
    
    /**
     * Constructor
     */
    public RMIConnectionIndicator() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setOpaque(false);
        
        statusIcon = new JLabel();
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        statusText = new JLabel();
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        add(statusIcon);
        add(statusText);
        
        // Check initial status
        checkConnection();
    }
    
    /**
     * Check RMI connection status
     */
    public void checkConnection() {
        setStatus(ConnectionStatus.CONNECTING);
        
        // Check in background thread
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    RMIClientManager manager = RMIClientManager.getInstance();
                    
                    // Check if manager has all required services
                    boolean connected = (manager.getEmployeeService() != null &&
                                       manager.getCustomerService() != null &&
                                       manager.getProductService() != null &&
                                       manager.getSalesService() != null &&
                                       manager.getDashboardService() != null);
                    
                    return connected;
                } catch (Exception e) {
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    setStatus(connected ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED);
                } catch (Exception e) {
                    setStatus(ConnectionStatus.DISCONNECTED);
                }
            }
        }.execute();
    }
    
    /**
     * Set connection status
     */
    public void setStatus(ConnectionStatus status) {
        this.currentStatus = status;
        
        // Set icon based on status
        if (status == ConnectionStatus.CONNECTED) {
            statusIcon.setText("●");
        } else if (status == ConnectionStatus.CONNECTING) {
            statusIcon.setText("◐");
        } else {
            statusIcon.setText("●");
        }
        
        // Set text and colors
        statusText.setText(status.getDisplayText());
        statusIcon.setForeground(status.getColor());
        statusText.setForeground(status.getColor());
        
        // Repaint
        revalidate();
        repaint();
    }
    
    /**
     * Get current status
     */
    public ConnectionStatus getStatus() {
        return currentStatus;
    }
}