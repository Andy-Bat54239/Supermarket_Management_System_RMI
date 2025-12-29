package model;

import java.io.Serializable;


/**
 * Employee Profile Entity
 * One-to-One relationship with Employee
 */
public class EmployeeProfile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int profileId;
    
    private Employee employee;
    
    private String email;
    
    private String phone;
    
    private String address;
    
    private String emergencyContactName;
    
    private String emergencyContactPhone;
    
    public EmployeeProfile() {
    }
    
    public EmployeeProfile(Employee employee) {
        this.employee = employee;
    }

    // Getters and Setters
    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    @Override
    public String toString() {
        return "EmployeeProfile{" +
                "profileId=" + profileId +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
