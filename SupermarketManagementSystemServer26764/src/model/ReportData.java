package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author andyb
 */
public class ReportData implements Serializable{
    
     private static final long serialVersionUID = 1L;
    
    private String reportTitle;
    private String reportType; // SALES, INVENTORY, CUSTOMER, PRODUCT, etc.
    private Date generatedDate;
    private Date startDate;
    private Date endDate;
    private String generatedBy;
    private List<String[]> headers;
    private List<List<String>> data;
    private String summary;
    
    public ReportData() {
        this.generatedDate = new Date();
    }

    // Getters and Setters
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public Date getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(Date generatedDate) { this.generatedDate = generatedDate; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    
    public List<String[]> getHeaders() { return headers; }
    public void setHeaders(List<String[]> headers) { this.headers = headers; }
    
    public List<List<String>> getData() { return data; }
    public void setData(List<List<String>> data) { this.data = data; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    
}
