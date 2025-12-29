package view;

import client.RMIClientManager;
import model.ReportData;
import service.ReportService;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportsPanel extends JPanel {
    
    private static final Color DARK_BG = new Color(30, 30, 30);
    private static final Color ACCENT_TEAL = new Color(20, 184, 166);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 240);
    
    private JComboBox<String> cmbReportType;
    private JDateChooser dateStart;
    private JDateChooser dateEnd;
    private JButton btnGenerate;
    private JButton btnExportPDF;
    private JButton btnExportExcel;
    private JButton btnExportCSV;
    private JTable tblReport;
    private DefaultTableModel tableModel;
    private JLabel lblReportTitle;
    private JLabel lblSummary;
    
    private ReportService reportService;
    private ReportData currentReport;
    private String employeeName;
    
    public ReportsPanel(String employeeName) {
        this.employeeName = employeeName;
        initializeServices();
        initComponents();
    }
    
    private void initializeServices() {
        try {
            reportService = RMIClientManager.getInstance().getReportService();
            
            if (reportService == null) {
                JOptionPane.showMessageDialog(this,
                    "Report service not available!\nPlease ensure server is running.",
                    "Service Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to connect to report service!",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initComponents() {
        setLayout(null);
        setBackground(DARK_BG);
        
        // Title
        JLabel lblTitle = new JLabel("REPORTS");
        lblTitle.setBounds(20, 20, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(ACCENT_TEAL);
        add(lblTitle);
        
        // Report Type
        JLabel lblType = new JLabel("Report Type:");
        lblType.setBounds(20, 70, 100, 25);
        lblType.setForeground(TEXT_PRIMARY);
        add(lblType);
        
        String[] reportTypes = {
            "Sales Report",
            "Inventory Report",
            "Customer Report",
            "Product Report",
            "Low Stock Report"
        };
        cmbReportType = new JComboBox<>(reportTypes);
        cmbReportType.setBounds(130, 70, 200, 30);
        styleComboBox(cmbReportType);
        cmbReportType.addActionListener(e -> updateDateFields());
        add(cmbReportType);
        
        // Date Range (for Sales Report)
        JLabel lblStart = new JLabel("Start Date:");
        lblStart.setBounds(350, 70, 100, 25);
        lblStart.setForeground(TEXT_PRIMARY);
        add(lblStart);
        
        dateStart = new JDateChooser();
        dateStart.setBounds(450, 70, 150, 30);
        dateStart.setDate(new Date());
        add(dateStart);
        
        JLabel lblEnd = new JLabel("End Date:");
        lblEnd.setBounds(620, 70, 100, 25);
        lblEnd.setForeground(TEXT_PRIMARY);
        add(lblEnd);
        
        dateEnd = new JDateChooser();
        dateEnd.setBounds(720, 70, 150, 30);
        dateEnd.setDate(new Date());
        add(dateEnd);
        
        // Generate Button
        btnGenerate = new JButton("Generate Report");
        btnGenerate.setBounds(900, 70, 150, 35);
        styleButton(btnGenerate, ACCENT_TEAL);
        btnGenerate.addActionListener(e -> generateReport());
        add(btnGenerate);
        
        // Export Buttons
        btnExportPDF = new JButton("Export PDF");
        btnExportPDF.setBounds(20, 120, 120, 35);
        styleButton(btnExportPDF, new Color(220, 38, 38));
        btnExportPDF.setEnabled(false);
        btnExportPDF.addActionListener(e -> exportReport("PDF"));
        add(btnExportPDF);
        
        btnExportExcel = new JButton("Export Excel");
        btnExportExcel.setBounds(150, 120, 120, 35);
        styleButton(btnExportExcel, new Color(16, 185, 129));
        btnExportExcel.setEnabled(false);
        btnExportExcel.addActionListener(e -> exportReport("EXCEL"));
        add(btnExportExcel);
        
        btnExportCSV = new JButton("Export CSV");
        btnExportCSV.setBounds(280, 120, 120, 35);
        styleButton(btnExportCSV, new Color(59, 130, 246));
        btnExportCSV.setEnabled(false);
        btnExportCSV.addActionListener(e -> exportReport("CSV"));
        add(btnExportCSV);
        
        // Report Title Label
        lblReportTitle = new JLabel("");
        lblReportTitle.setBounds(20, 170, 800, 30);
        lblReportTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblReportTitle.setForeground(ACCENT_TEAL);
        add(lblReportTitle);
        
        // Table
        tableModel = new DefaultTableModel();
        tblReport = new JTable(tableModel);
        tblReport.setBackground(new Color(45, 45, 45));
        tblReport.setForeground(TEXT_PRIMARY);
        tblReport.setGridColor(new Color(60, 60, 60));
        tblReport.setSelectionBackground(ACCENT_TEAL);
        tblReport.getTableHeader().setBackground(new Color(35, 35, 35));
        tblReport.getTableHeader().setForeground(ACCENT_TEAL);
        
        JScrollPane scrollPane = new JScrollPane(tblReport);
        scrollPane.setBounds(20, 210, 1050, 400);
        add(scrollPane);
        
        // Summary Label
        lblSummary = new JLabel("");
        lblSummary.setBounds(20, 620, 1000, 30);
        lblSummary.setFont(new Font("Arial", Font.BOLD, 14));
        lblSummary.setForeground(ACCENT_TEAL);
        add(lblSummary);
        
        // Initially hide date fields
        updateDateFields();
    }
    
    private void updateDateFields() {
        boolean showDates = cmbReportType.getSelectedIndex() == 0; // Sales Report
        dateStart.setVisible(showDates);
        dateEnd.setVisible(showDates);
    }
    
    private void generateReport() {
        if (reportService == null) {
            JOptionPane.showMessageDialog(this,
                "Report service not available!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        btnGenerate.setEnabled(false);
        btnGenerate.setText("Generating...");
        
        new SwingWorker<ReportData, Void>() {
            @Override
            protected ReportData doInBackground() throws Exception {
                int reportType = cmbReportType.getSelectedIndex();
                
                switch (reportType) {
                    case 0: // Sales Report
                        return reportService.generateSalesReport(
                            dateStart.getDate(),
                            dateEnd.getDate(),
                            employeeName
                        );
                    case 1: // Inventory Report
                        return reportService.generateInventoryReport(employeeName);
                    case 2: // Customer Report
                        return reportService.generateCustomerReport(employeeName);
                    case 3: // Product Report
                        return reportService.generateProductReport(employeeName);
                    case 4: // Low Stock Report
                        return reportService.generateLowStockReport(employeeName);
                    default:
                        return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    currentReport = get();
                    displayReport(currentReport);
                    
                    // Enable export buttons
                    btnExportPDF.setEnabled(true);
                    btnExportExcel.setEnabled(true);
                    btnExportCSV.setEnabled(true);
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ReportsPanel.this,
                        "Error generating report: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    btnGenerate.setEnabled(true);
                    btnGenerate.setText("Generate Report");
                }
            }
        }.execute();
    }
    
    private void displayReport(ReportData report) {
        // Set title
        lblReportTitle.setText(report.getReportTitle());
        
        // Clear and set table model
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        
        // Add columns
        if (report.getHeaders() != null && !report.getHeaders().isEmpty()) {
            for (String header : report.getHeaders().get(0)) {
                tableModel.addColumn(header);
            }
        }
        
        // Add data
        if (report.getData() != null) {
            for (java.util.List<String> row : report.getData()) {
                tableModel.addRow(row.toArray());
            }
        }
        
        // Set summary
        if (report.getSummary() != null) {
            lblSummary.setText(report.getSummary());
        }
    }
    
    private void exportReport(String format) {
        if (currentReport == null) {
            return;
        }
        
        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        
        String extension = "";
        switch (format) {
            case "PDF": extension = ".pdf"; break;
            case "EXCEL": extension = ".xlsx"; break;
            case "CSV": extension = ".csv"; break;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = currentReport.getReportType() + "_" + sdf.format(new Date()) + extension;
        fileChooser.setSelectedFile(new File(filename));
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    byte[] data = null;
                    
                    switch (format) {
                        case "PDF":
                            data = reportService.exportToPDF(currentReport);
                            break;
                        case "EXCEL":
                            data = reportService.exportToExcel(currentReport);
                            break;
                        case "CSV":
                            data = reportService.exportToCSV(currentReport);
                            break;
                    }
                    
                    if (data != null) {
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            fos.write(data);
                        }
                    }
                    
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                            "Report exported successfully!\n" + file.getAbsolutePath(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Open file
                        Desktop.getDesktop().open(file);
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                            "Error exporting report: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }
    
    private void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(new Color(45, 45, 45));
        combo.setForeground(TEXT_PRIMARY);
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}