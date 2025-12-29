package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.*;
import java.util.prefs.Preferences;

/**
 * Theme Manager for Light/Dark Mode Toggle
 * Handles theme switching across the entire application
 */
public class ThemeManager {
    
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private static final String THEME_KEY = "app_theme";
    private static boolean isDarkMode = true; // Default to dark mode
    
    // Dark Theme Colors
    public static final Color DARK_BACKGROUND = new Color(30, 30, 30);
    public static final Color DARK_PANEL = new Color(40, 40, 40);
    public static final Color DARK_SIDEBAR = new Color(25, 25, 25);
    public static final Color DARK_TEXT = new Color(220, 220, 220);
    public static final Color DARK_TEXT_SECONDARY = new Color(160, 160, 160);
    public static final Color DARK_BORDER = new Color(60, 60, 60);
    public static final Color DARK_INPUT = new Color(45, 45, 45);
    
    // Light Theme Colors
    public static final Color LIGHT_BACKGROUND = new Color(245, 245, 245);
    public static final Color LIGHT_PANEL = new Color(255, 255, 255);
    public static final Color LIGHT_SIDEBAR = new Color(240, 240, 240);
    public static final Color LIGHT_TEXT = new Color(30, 30, 30);
    public static final Color LIGHT_TEXT_SECONDARY = new Color(100, 100, 100);
    public static final Color LIGHT_BORDER = new Color(220, 220, 220);
    public static final Color LIGHT_INPUT = new Color(250, 250, 250);
    
    // Accent Colors (Same for both themes)
    public static final Color ACCENT_PRIMARY = new Color(20, 184, 166); // Teal
    public static final Color ACCENT_HOVER = new Color(13, 148, 136);
    public static final Color ACCENT_SUCCESS = new Color(34, 197, 94);
    public static final Color ACCENT_WARNING = new Color(251, 146, 60);
    public static final Color ACCENT_ERROR = new Color(239, 68, 68);
    
    static {
        // Load saved theme preference
        String savedTheme = prefs.get(THEME_KEY, "dark");
        isDarkMode = savedTheme.equals("dark");
    }
    
    /**
     * Toggle between light and dark mode
     */
    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        saveTheme();
    }
    
    /**
     * Set theme explicitly
     */
    public static void setDarkMode(boolean dark) {
        isDarkMode = dark;
        saveTheme();
    }
    
    /**
     * Check if dark mode is active
     */
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    /**
     * Save theme preference
     */
    private static void saveTheme() {
        prefs.put(THEME_KEY, isDarkMode ? "dark" : "light");
    }
    
    // ==================== COLOR GETTERS ====================
    
    public static Color getBackgroundColor() {
        return isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }
    
    public static Color getPanelColor() {
        return isDarkMode ? DARK_PANEL : LIGHT_PANEL;
    }
    
    public static Color getSidebarColor() {
        return isDarkMode ? DARK_SIDEBAR : LIGHT_SIDEBAR;
    }
    
    public static Color getTextColor() {
        return isDarkMode ? DARK_TEXT : LIGHT_TEXT;
    }
    
    public static Color getTextSecondaryColor() {
        return isDarkMode ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }
    
    public static Color getBorderColor() {
        return isDarkMode ? DARK_BORDER : LIGHT_BORDER;
    }
    
    public static Color getInputColor() {
        return isDarkMode ? DARK_INPUT : LIGHT_INPUT;
    }
    
    // ==================== COMPONENT STYLING ====================
    
    /**
     * Apply theme to a component and all its children
     */
    public static void applyTheme(Component component) {
        if (component instanceof JPanel) {
            component.setBackground(getPanelColor());
            component.setForeground(getTextColor());
        } else if (component instanceof JLabel) {
            component.setForeground(getTextColor());
        } else if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setBackground(getInputColor());
            field.setForeground(getTextColor());
            field.setCaretColor(getTextColor());
            field.setBorder(BorderFactory.createLineBorder(getBorderColor()));
        } else if (component instanceof JTextArea) {
            JTextArea area = (JTextArea) component;
            area.setBackground(getInputColor());
            area.setForeground(getTextColor());
            area.setCaretColor(getTextColor());
            area.setBorder(BorderFactory.createLineBorder(getBorderColor()));
        } else if (component instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) component;
            combo.setBackground(getInputColor());
            combo.setForeground(getTextColor());
        } else if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(getPanelColor());
            table.setForeground(getTextColor());
            table.setGridColor(getBorderColor());
            table.getTableHeader().setBackground(getSidebarColor());
            table.getTableHeader().setForeground(getTextColor());
        } else if (component instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) component;
            scroll.getViewport().setBackground(getPanelColor());
            scroll.setBorder(BorderFactory.createLineBorder(getBorderColor()));
        }
        
        // Recursively apply to children
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                applyTheme(child);
            }
        }
    }
    
    /**
     * Style a primary button
     */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(ACCENT_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_PRIMARY);
            }
        });
    }
    
    /**
     * Style a secondary button
     */
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(getPanelColor());
        button.setForeground(getTextColor());
        button.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        Color normalBg = button.getBackground();
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(getSidebarColor());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalBg);
            }
        });
    }
    
    /**
     * Style a sidebar navigation button
     */
    public static void styleNavButton(JButton button, boolean isActive) {
        if (isActive) {
            button.setBackground(ACCENT_PRIMARY);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(getSidebarColor());
            button.setForeground(getTextSecondaryColor());
        }
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
}
