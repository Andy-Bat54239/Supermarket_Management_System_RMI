package util;

import model.OTPRequest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author andyb
 */
public class OTPManager {
    
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_MINUTES = 5*60*1000;
    private static final int MAX_ATTEMPTS = 3;
    
    // Store OTPs in memory (username -> OTPRequest)
    private static final Map<String, OTPRequest> otpStore = new ConcurrentHashMap<>();
    
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate a 6-digit OTP
     */
    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    /**
     * Store OTP for a user
     */
    public static void storeOTP(String username, String email, String phoneNumber, String otp) {
        OTPRequest otpRequest = new OTPRequest(username, email, phoneNumber, otp);
        otpStore.put(username, otpRequest);
        
        System.out.println("[OTP MANAGER] OTP generated for user: " + username);
        System.out.println("[OTP MANAGER] OTP: " + otp + " (Valid for " + OTP_VALIDITY_MINUTES + " minutes)");
    }
    
    /**
     * Validate OTP for a user
     */
    public static boolean validateOTP(String username, String inputOTP) {
        OTPRequest otpRequest = otpStore.get(username);
        
        if (otpRequest == null) {
            System.out.println("[OTP MANAGER] No OTP found for user: " + username);
            return false;
        }
        
        // Check if expired
        if (otpRequest.isExpired()) {
            System.out.println("[OTP MANAGER] OTP expired for user: " + username);
            otpStore.remove(username);
            return false;
        }
        
        // Check attempts
        if (!otpRequest.canRetry()) {
            System.out.println("[OTP MANAGER] Max attempts reached for user: " + username);
            otpStore.remove(username);
            return false;
        }
        
        // Validate OTP
        otpRequest.incrementAttempts();
        
        if (otpRequest.getOtp().equals(inputOTP)) {
            otpRequest.setVerified(true);
            System.out.println("[OTP MANAGER] OTP verified successfully for user: " + username);
            otpStore.remove(username); // Remove after successful verification
            return true;
        } else {
            System.out.println("[OTP MANAGER] Invalid OTP for user: " + username + 
                " (Attempt " + otpRequest.getAttempts() + "/" + MAX_ATTEMPTS + ")");
            return false;
        }
    }
    
    /**
     * Invalidate/remove OTP for a user
     */
    public static void invalidateOTP(String username) {
        otpStore.remove(username);
        System.out.println("[OTP MANAGER] OTP invalidated for user: " + username);
    }
    
    /**
     * Get OTP request details
     */
    public static OTPRequest getOTPRequest(String username) {
        return otpStore.get(username);
    }
    
    /**
     * Clean up expired OTPs (call periodically)
     */
    public static void cleanupExpiredOTPs() {
        otpStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
}
