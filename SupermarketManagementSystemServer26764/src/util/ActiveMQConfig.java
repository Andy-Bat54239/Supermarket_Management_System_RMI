package util;

/**
 *
 * @author andyb
 */
public class ActiveMQConfig {
    
    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";
    
    // Queue Names
    public static final String OTP_EMAIL_QUEUE = "OTP_EMAIL_QUEUE";
    public static final String OTP_SMS_QUEUE = "OTP_SMS_QUEUE";
    public static final String NOTIFICATION_QUEUE = "NOTIFICATION_QUEUE";
    
    // Email Settings (Gmail example)
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";
    public static final String EMAIL_USERNAME = "biyonga.bahati@a2sv.org"; // TODO: Change this
    public static final String EMAIL_PASSWORD = "xnik nicw nbar efhf";     // TODO: Change this
    public static final String EMAIL_FROM = "Epignosis Supermarket";
    
    // SMS Settings (Twilio example - optional)
    public static final String TWILIO_ACCOUNT_SID = "your_account_sid";
    public static final String TWILIO_AUTH_TOKEN = "your_auth_token";
    public static final String TWILIO_PHONE_NUMBER = "+1234567890";
    
}
