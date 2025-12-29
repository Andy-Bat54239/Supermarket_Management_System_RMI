package view;

import service.DashboardService;
import service.SalesService;
import client.RMIClientManager;
import util.ValidationUtil;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import model.DailySalesData;
import model.Employee;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
//import java.awt.geom.Ellipse2D.Double;

/**
 *
 * @author andyb
 */
public class DashboardPanel extends javax.swing.JPanel {
    private DashboardService dashboardService;
    private SalesService salesService;
    
    private Employee currentEmployee;
    private boolean isCashierMode = false;
    

    /**
     * Creates new form DashboardPanel
     */
    public DashboardPanel(Employee employee) {
        this.currentEmployee = employee;
        
        initComponents();
        
        try{
            dashboardService = RMIClientManager.getInstance().getDashboardService();
            salesService = RMIClientManager.getInstance().getSalesService();
        } catch (Exception e) {
            ValidationUtil.showError(this,
                    "Failed to connect to server: "+ e.getMessage(),
                    "Connection Error");
            e.printStackTrace();
        }
        
        loadDashboardStats();
        createSalesChart();
    }
    
    protected void loadDashboardStats(){
       try{
           Integer totalProducts = dashboardService.getTotalProducts();
           Integer totalSales = dashboardService.getTotalSales();
           Integer totalCustomers = dashboardService.getTotalCustomers();
           Double totalRevenue = dashboardService.getTotalRevenue();
           
           txtTotalProducts.setText(String.valueOf(totalProducts != null ? totalProducts : 0));
            txtTotalSales.setText(String.valueOf(totalSales != null ? totalSales : 0));
            txtTotalCustomers.setText(String.valueOf(totalCustomers != null ? totalCustomers : 0));
            txtTotalRevenue.setText(String.format("%.0f", totalRevenue != null ? totalRevenue : 0.0));
            
       } catch(Exception e){
           ValidationUtil.showError(this,
                   "Error loading dashboard statistics: " + e.getMessage(),
                   "Dashboard Error");
           
           txtTotalProducts.setText("0");
           txtTotalSales.setText("0");
           txtTotalCustomers.setText("0");
           txtTotalRevenue.setText("0");
       }
    }
    
    public void setCashierMode(boolean isCashier, int employeeId){
        this.isCashierMode = isCashier;
        
        if(isCashier){
            loadCashierDashboardData(employeeId);
            
            lblSales.setText("Your Sales");
            lblRevenue.setText("Your Revenue");
        }
    }
    
    public void setAdminMode(boolean isAdmin){
        if(isAdmin){
            loadDashboardStats();
        }
    }
    
    private void loadCashierDashboardData(int employeeId){
        try{
            Integer totalProducts = dashboardService.getTotalProducts();
            Integer totalCustomers = dashboardService.getTotalCustomers();
            
            Integer employeeSales = salesService.getSalesCountByEmployee(employeeId);
            Double employeeRevenue = salesService.getEmployeeRevenue(employeeId);
            
            txtTotalProducts.setText(String.valueOf(totalProducts != null ? totalProducts : 0));
            txtTotalCustomers.setText(String.valueOf(totalCustomers != null ? totalCustomers : 0));
            txtTotalSales.setText(String.valueOf(employeeSales != null ? employeeSales : 0));
            txtTotalRevenue.setText(String.format("%.0f", employeeRevenue != null ? employeeRevenue : 0.0));
            
        }catch(Exception e){
            
        }
        
    }
    
    private void createSalesChart(){
        try{
            DefaultCategoryDataset dataset = createDataset();
            
            JFreeChart chart = ChartFactory.createLineChart(null, "Date", "Sales Amount (RWF)", dataset, PlotOrientation.VERTICAL, false, true, false);
            
            customizeChart(chart);
            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(chartCanvas.getPreferredSize());
            chartPanel.setBackground(new Color(50, 50, 50));
            
            chartPanel.setBorder(null);
//            chart.setPadding(new org.jfree.ui.RectangleInsets(0, 0, 0, 0));
            
            chartCanvas.removeAll();
            chartCanvas.setLayout(new BorderLayout());
            chartCanvas.add(chartPanel, BorderLayout.CENTER);
            chartCanvas.revalidate();
            chartCanvas.repaint();
            
            System.out.println("Chart Created Successfully!");
        } catch (Exception e){
            ValidationUtil.showWarning(this,
                    "Unable to load sales chart. Chart will be empty.", "Chart Warning");
            e.printStackTrace();
        }
    }
    
    private DefaultCategoryDataset createDataset(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            List<DailySalesData> dailySales = dashboardService.getDailySalesTrend();
            
            if(dailySales == null || dailySales.isEmpty()){
                System.out.println("No sales data found. Adding sample data for visualization...");
                
                dataset.addValue(5000, "Sales", "Mon");
                dataset.addValue(7500, "Sales", "Tue");
                dataset.addValue(6200, "Sales", "Web");
                dataset.addValue(8900, "Sales", "Thu");
                dataset.addValue(7100, "Sales", "Fri");
                dataset.addValue(9500, "Sales", "Sat");
                dataset.addValue(8300, "Sales", "Sun");
            } else{
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE");
                
                for(DailySalesData  data : dailySales) {
                    try{
                        String dateStr = data.getDate().toString();
                        String dayName = outputFormat.format(inputFormat.parse(dateStr));
                        double amount = data.getTotal();
                        
                        dataset.addValue(amount, "Sales", dayName);
                    }catch(Exception e){
                        dataset.addValue(data.getTotal(), "Sales", data.getDate());
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            
            dataset.addValue(0, "Sales", "No Data");
        }
        
        return dataset;
    }
    
    private void customizeChart(JFreeChart chart){
        
        Color backgroundColor = new Color(205, 205, 205);
        Color gridColor = new Color(185, 185, 185);
        Color textColor = new Color(15, 15, 15);
        Color lineColor = new Color(20, 184, 166);
        
        chart.setBorderPaint(backgroundColor);
        chart.setBorderVisible(false);
        
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(backgroundColor);
        plot.setDomainGridlinePaint(gridColor);
        plot.setRangeGridlinePaint(gridColor);
        plot.setOutlineVisible(true);
        
//        plot.setInsets(new org.jfree.ui.RectangleInsets(0, 0, 0, 0));
        
        LineAndShapeRenderer renderer = new DefaultCategoryItemRenderer();
        renderer.setSeriesOutlinePaint(0, lineColor);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));
        plot.setRenderer(renderer);
        
        plot.getDomainAxis().setTickLabelPaint(textColor);
        plot.getDomainAxis().setLabelPaint(textColor);
        
        plot.getRangeAxis().setTickLabelPaint(textColor);
        plot.getRangeAxis().setLabelPaint(textColor);
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
        plot.getDomainAxis().setTickLabelFont(labelFont);
        plot.getRangeAxis().setTickLabelFont(labelFont);
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 13));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 13));
    }
    
    public void refreshDashboard(){
        if (isCashierMode){
            loadCashierDashboardData(currentEmployee.getEmployeeId());
        } else {
            loadDashboardStats();
        }
        createSalesChart();
    }
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        barRenderer1 = new org.jfree.chart.renderer.category.BarRenderer();
        barRenderer2 = new org.jfree.chart.renderer.category.BarRenderer();
        dashboardPanel = new javax.swing.JPanel();
        dashboardScrollPanel = new javax.swing.JScrollPane();
        dashboardContentArea = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20));
        summaryCardsPanel = new javax.swing.JPanel();
        cardProducts = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtTotalProducts = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cardSales = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtTotalSales = new javax.swing.JLabel();
        lblSales = new javax.swing.JLabel();
        cardCustomers = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtTotalCustomers = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cardRevenue = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtTotalRevenue = new javax.swing.JLabel();
        lblRevenue = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30));
        salesChartCard = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        chartCanvas = new javax.swing.JPanel();

        dashboardPanel.setBackground(new java.awt.Color(30, 30, 30));
        dashboardPanel.setFocusCycleRoot(true);
        dashboardPanel.setLayout(new java.awt.BorderLayout());

        dashboardScrollPanel.setBorder(null);

        dashboardContentArea.setBackground(new java.awt.Color(30, 30, 30));
        dashboardContentArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 25, 25, 1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("Dashboard Overview");

        summaryCardsPanel.setBackground(new java.awt.Color(30, 30, 30));
        summaryCardsPanel.setMaximumSize(new java.awt.Dimension(32767, 130));
        summaryCardsPanel.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        cardProducts.setBackground(new java.awt.Color(50, 50, 50));
        cardProducts.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));
        cardProducts.setLayout(new java.awt.BorderLayout());

        jLabel2.setBackground(new java.awt.Color(53, 130, 246));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 40)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon("E:\\icons8-products-48.png")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(70, 70));
        cardProducts.add(jLabel2, java.awt.BorderLayout.LINE_START);

        jPanel4.setBackground(new java.awt.Color(50, 50, 50));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        txtTotalProducts.setBackground(new java.awt.Color(240, 240, 240));
        txtTotalProducts.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        txtTotalProducts.setText("1248");
        jPanel4.add(txtTotalProducts);

        jLabel4.setBackground(new java.awt.Color(160, 160, 160));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel4.setText("Total Products");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel4);

        cardProducts.add(jPanel4, java.awt.BorderLayout.CENTER);

        summaryCardsPanel.add(cardProducts);

        cardSales.setBackground(new java.awt.Color(50, 50, 50));
        cardSales.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));
        cardSales.setLayout(new java.awt.BorderLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 40)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon("E:\\icons8-total-sales-50.png")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(70, 70));
        cardSales.add(jLabel5, java.awt.BorderLayout.LINE_START);

        jPanel1.setBackground(new java.awt.Color(50, 50, 50));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        txtTotalSales.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        txtTotalSales.setForeground(new java.awt.Color(240, 240, 240));
        txtTotalSales.setText("45");
        jPanel1.add(txtTotalSales);

        lblSales.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSales.setForeground(new java.awt.Color(160, 160, 160));
        lblSales.setText("Total Sales");
        jPanel1.add(lblSales);

        cardSales.add(jPanel1, java.awt.BorderLayout.CENTER);

        summaryCardsPanel.add(cardSales);

        cardCustomers.setBackground(new java.awt.Color(50, 50, 50));
        cardCustomers.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));
        cardCustomers.setLayout(new java.awt.BorderLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 40)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon("E:\\icons8-customer-50.png")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(70, 70));
        cardCustomers.add(jLabel8, java.awt.BorderLayout.LINE_START);

        jPanel2.setBackground(new java.awt.Color(50, 50, 50));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        txtTotalCustomers.setBackground(new java.awt.Color(240, 240, 240));
        txtTotalCustomers.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        txtTotalCustomers.setForeground(new java.awt.Color(240, 240, 240));
        txtTotalCustomers.setText("100");
        jPanel2.add(txtTotalCustomers);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(160, 160, 160));
        jLabel10.setText("Total Customers");
        jPanel2.add(jLabel10);

        cardCustomers.add(jPanel2, java.awt.BorderLayout.CENTER);

        summaryCardsPanel.add(cardCustomers);

        cardRevenue.setBackground(new java.awt.Color(50, 50, 50));
        cardRevenue.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));
        cardRevenue.setLayout(new java.awt.BorderLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 40)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon("E:\\icons8-graph-report-50.png")); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(70, 70));
        cardRevenue.add(jLabel12, java.awt.BorderLayout.LINE_START);

        jPanel5.setBackground(new java.awt.Color(50, 50, 50));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        txtTotalRevenue.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        txtTotalRevenue.setForeground(new java.awt.Color(240, 240, 240));
        txtTotalRevenue.setText("10000");
        jPanel5.add(txtTotalRevenue);

        lblRevenue.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRevenue.setForeground(new java.awt.Color(160, 160, 160));
        lblRevenue.setText("Total Revenue");
        jPanel5.add(lblRevenue);

        cardRevenue.add(jPanel5, java.awt.BorderLayout.CENTER);

        summaryCardsPanel.add(cardRevenue);

        salesChartCard.setBackground(new java.awt.Color(50, 50, 50));
        salesChartCard.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("Daily Sales Trend");

        chartCanvas.setBackground(new java.awt.Color(50, 50, 50));

        javax.swing.GroupLayout chartCanvasLayout = new javax.swing.GroupLayout(chartCanvas);
        chartCanvas.setLayout(chartCanvasLayout);
        chartCanvasLayout.setHorizontalGroup(
            chartCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 899, Short.MAX_VALUE)
        );
        chartCanvasLayout.setVerticalGroup(
            chartCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout salesChartCardLayout = new javax.swing.GroupLayout(salesChartCard);
        salesChartCard.setLayout(salesChartCardLayout);
        salesChartCardLayout.setHorizontalGroup(
            salesChartCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesChartCardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 1040, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(salesChartCardLayout.createSequentialGroup()
                .addComponent(chartCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        salesChartCardLayout.setVerticalGroup(
            salesChartCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesChartCardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(chartCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout dashboardContentAreaLayout = new javax.swing.GroupLayout(dashboardContentArea);
        dashboardContentArea.setLayout(dashboardContentAreaLayout);
        dashboardContentAreaLayout.setHorizontalGroup(
            dashboardContentAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardContentAreaLayout.createSequentialGroup()
                .addGroup(dashboardContentAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dashboardContentAreaLayout.createSequentialGroup()
                        .addGroup(dashboardContentAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(summaryCardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(salesChartCard, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dashboardContentAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filler7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        dashboardContentAreaLayout.setVerticalGroup(
            dashboardContentAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardContentAreaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(dashboardContentAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dashboardContentAreaLayout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(filler7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dashboardContentAreaLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(summaryCardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(dashboardContentAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dashboardContentAreaLayout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dashboardContentAreaLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(salesChartCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        dashboardScrollPanel.setViewportView(dashboardContentArea);

        dashboardPanel.add(dashboardScrollPanel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1150, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(dashboardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 840, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(dashboardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jfree.chart.renderer.category.BarRenderer barRenderer1;
    private org.jfree.chart.renderer.category.BarRenderer barRenderer2;
    private javax.swing.JPanel cardCustomers;
    private javax.swing.JPanel cardProducts;
    private javax.swing.JPanel cardRevenue;
    private javax.swing.JPanel cardSales;
    private javax.swing.JPanel chartCanvas;
    private javax.swing.JPanel dashboardContentArea;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JScrollPane dashboardScrollPanel;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblRevenue;
    private javax.swing.JLabel lblSales;
    private javax.swing.JPanel salesChartCard;
    private javax.swing.JPanel summaryCardsPanel;
    private javax.swing.JLabel txtTotalCustomers;
    private javax.swing.JLabel txtTotalProducts;
    private javax.swing.JLabel txtTotalRevenue;
    private javax.swing.JLabel txtTotalSales;
    // End of variables declaration//GEN-END:variables
}
