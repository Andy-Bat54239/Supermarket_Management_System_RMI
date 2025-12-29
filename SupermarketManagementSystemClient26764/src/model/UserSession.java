package model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author andyb
 */
public class UserSession implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String username;
    private String employeeName;
    private int employeeId;
    private String role;
    private Date loginTime;
    private Date lastActivityTime;
    private String clientIpAddress;
    private boolean active;
    
    private static final long SESSION_TIMEOUT = 30 * 1000;
    
    public UserSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.loginTime = new Date();
        this.lastActivityTime = new Date();
        this.active = true;
    }

    public UserSession(String username, String employeeName, int employeeId, String role) {
        this();
        this.username = username;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.role = role;
    }
    
    /**
     * Update last activity time
     */
    public void updateActivity() {
        this.lastActivityTime = new Date();
    }
    
    /**
     * Check if session has expired
     */
    public boolean isExpired() {
        long timeSinceLastActivity = new Date().getTime() - lastActivityTime.getTime();
        return timeSinceLastActivity > SESSION_TIMEOUT;
    }
    
    /**
     * Get session duration in minutes
     */
    public long getSessionDuration() {
        return (new Date().getTime() - loginTime.getTime()) / (60 * 1000);
    }
    
    /**
     * Terminate session
     */
    public void terminate() {
        this.active = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(Date lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "sessionId='" + sessionId + '\'' +
                ", username='" + username + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", role='" + role + '\'' +
                ", loginTime=" + loginTime +
                ", active=" + active +
                ", expired=" + isExpired() +
                '}';
    }   
}
