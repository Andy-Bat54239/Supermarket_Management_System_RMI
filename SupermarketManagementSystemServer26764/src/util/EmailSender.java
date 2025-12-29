package util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 *
 * @author andyb
 */
public class EmailSender {
    /**
     * Send OTP via email
     */
    public static boolean sendOTPEmail(String toEmail, String username, String otp) {
        try {
            // Setup mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", ActiveMQConfig.SMTP_HOST);
            props.put("mail.smtp.port", ActiveMQConfig.SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", ActiveMQConfig.SMTP_HOST);
            
            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        ActiveMQConfig.EMAIL_USERNAME,
                        ActiveMQConfig.EMAIL_PASSWORD
                    );
                }
            };
            
            // Create session with authentication
            Session session = Session.getInstance(props, auth);
            session.setDebug(false);
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ActiveMQConfig.EMAIL_USERNAME, ActiveMQConfig.EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your OTP for Epignosis Supermarket Login");
            
            // Create email body
            String emailBody = createOTPEmailBody(username, otp);
            message.setContent(emailBody, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(message);
            
            System.out.println("[EMAIL SENDER] OTP email sent to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("[EMAIL SENDER] Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create HTML email body for OTP (Java 8 compatible)
     */
    private static String createOTPEmailBody(String username, String otp) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }\n");
        html.append("        .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 10px; }\n");
        html.append("        .header { background-color: #14b8a6; color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }\n");
        html.append("        .content { padding: 30px; }\n");
        html.append("        .otp-box { background-color: #f0f9ff; border: 2px solid #14b8a6; padding: 20px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 10px; margin: 20px 0; border-radius: 5px; }\n");
        html.append("        .footer { text-align: center; color: #666; padding: 20px; font-size: 12px; }\n");
        html.append("        .warning { color: #ef4444; font-size: 14px; margin-top: 20px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <div class=\"header\">\n");
        html.append("            <h1>Epignosis Supermarket</h1>\n");
        html.append("            <p>One-Time Password (OTP)</p>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"content\">\n");
        html.append("            <p>Hello <strong>").append(username).append("</strong>,</p>\n");
        html.append("            <p>Your OTP for login is:</p>\n");
        html.append("            <div class=\"otp-box\">").append(otp).append("</div>\n");
        html.append("            <p>This OTP is valid for <strong>5 minutes</strong>.</p>\n");
        html.append("            <p>Please do not share this code with anyone.</p>\n");
        html.append("            <div class=\"warning\">\n");
        html.append("                ⚠️ If you did not request this OTP, please ignore this email and ensure your account is secure.\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"footer\">\n");
        html.append("            <p>&copy; 2024 Epignosis Supermarket Management System</p>\n");
        html.append("            <p>This is an automated message, please do not reply.</p>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * Send general notification email
     */
    public static boolean sendNotificationEmail(String toEmail, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", ActiveMQConfig.SMTP_HOST);
            props.put("mail.smtp.port", ActiveMQConfig.SMTP_PORT);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        ActiveMQConfig.EMAIL_USERNAME, 
                        ActiveMQConfig.EMAIL_PASSWORD
                    );
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ActiveMQConfig.EMAIL_USERNAME, ActiveMQConfig.EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
            
            System.out.println("[EMAIL SENDER] Notification email sent to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("[EMAIL SENDER] Error sending notification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
