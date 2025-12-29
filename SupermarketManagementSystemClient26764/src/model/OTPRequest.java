package model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author andyb
 */
public class OTPRequest implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String email;
    private String phoneNumber;
    private String otp;
    private Date generatedTime;
    private Date expiryTime;
    private boolean verified;
    private int attempts;
    
    public OTPRequest() {
        this.generatedTime = new Date();
        this.expiryTime = new Date(System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes
        this.verified = false;
        this.attempts = 0;
    }
    
    public OTPRequest(String username, String email, String phoneNumber, String otp) {
        this();
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }
    
    public boolean isExpired() {
        return new Date().after(expiryTime);
    }
    
    public boolean canRetry() {
        return attempts < 3; // Max 3 attempts
    }
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    
    public Date getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(Date generatedTime) { this.generatedTime = generatedTime; }
    
    public Date getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Date expiryTime) { this.expiryTime = expiryTime; }
    
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    
    public void incrementAttempts() { this.attempts++; }
    
}
