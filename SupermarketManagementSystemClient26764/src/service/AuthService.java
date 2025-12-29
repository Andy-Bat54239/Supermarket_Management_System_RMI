package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author andyb
 */
public interface AuthService extends Remote{
    
    /**
     * Request OTP for login
     * @param username Username
     * @return true if OTP sent successfully
     */
    boolean requestOTP(String username) throws RemoteException;
    
    /**
     * Verify OTP and complete login
     * @param username Username
     * @param password Password
     * @param otp OTP code
     * @return Employee object if login successful, null otherwise
     */
    model.Employee verifyOTPAndLogin(String username, String password, String otp) throws RemoteException;
    
    /**
     * Resend OTP
     * @param username Username
     * @return true if OTP resent successfully
     */
    boolean resendOTP(String username) throws RemoteException;
    
}
