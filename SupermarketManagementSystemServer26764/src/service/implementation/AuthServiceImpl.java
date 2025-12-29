package service.implementation;

import dao.EmployeeDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import model.Employee;
import org.apache.activemq.ActiveMQConnectionFactory;
import service.AuthService;
import util.ActiveMQConfig;
import util.OTPManager;

/**
 *
 * @author andyb
 */
public class AuthServiceImpl extends UnicastRemoteObject implements AuthService{
    
    private EmployeeDao employeeDao = new EmployeeDao();
    
    public AuthServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public boolean requestOTP(String username) throws RemoteException {
        try {
            System.out.println("\n========================================");
            System.out.println("[AUTH SERVICE] OTP REQUEST");
            System.out.println("========================================");
            System.out.println("Username: " + username);
            
            // Get employee details
            Employee employee = employeeDao.findByUsername(username);
            
            if (employee == null) {
                System.out.println("[AUTH SERVICE] ✗ User not found: " + username);
                System.out.println("========================================\n");
                return false;
            }
            
            System.out.println("[AUTH SERVICE] ✓ User found: " + employee.getFullName());
            
            // Check if employee has email
            if (employee.getEmployeeProfile() == null || employee.getEmployeeProfile().getEmail() == null) {
                System.out.println("[AUTH SERVICE] No email found for user: " + username);
                return false;
            }
            
            // Generate OTP
            String otp = OTPManager.generateOTP();
            System.out.println("[AUTH SERVICE] Generated OTP: " + otp);
            
            // Store OTP
            OTPManager.storeOTP(
                username, 
                employee.getEmployeeProfile().getEmail(), 
                employee.getEmployeeProfile().getPhone(),
                otp
            );
            
            // Send OTP via ActiveMQ
            sendOTPViaActiveMQ(
                employee.getEmployeeProfile().getEmail(), 
                username, 
                otp
            );
            
            System.out.println("[AUTH SERVICE] OTP sent successfully to: " + employee.getEmployeeProfile().getEmail());
            return true;
            
        } catch (Exception e) {
            System.err.println("[AUTH SERVICE] Error requesting OTP: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error requesting OTP", e);
        }
    }
    
    @Override
    public Employee verifyOTPAndLogin(String username, String password, String otp) throws RemoteException {
        try {
            System.out.println("\n========================================");
            System.out.println("[AUTH SERVICE] LOGIN ATTEMPT");
            System.out.println("========================================");
            System.out.println("Username: " + username);
            System.out.println("Password: " + (password != null ? "***" + password.substring(Math.max(0, password.length()-3)) : "null"));
            System.out.println("OTP: " + otp);
            
            // Validate OTP first
            System.out.println("[AUTH SERVICE] Validating OTP...");
            boolean otpValid = OTPManager.validateOTP(username, otp);
            
            if (!otpValid) {
                System.out.println("[AUTH SERVICE] ✗ Invalid OTP for: " + username);
                System.out.println("========================================\n");
                return null;
            }
            
            System.out.println("[AUTH SERVICE] ✓ OTP validated successfully");
            
            // OTP is valid, now verify password
            Employee employee = employeeDao.authenticate(username, password);
            
            if (employee != null) {
                System.out.println("[AUTH SERVICE] ✓ Password verified");
                System.out.println("[AUTH SERVICE] ✓ Login successful for: " + username);
                System.out.println("[AUTH SERVICE] Employee: " + employee.getFullName());
                System.out.println("[AUTH SERVICE] Role: " + employee.getRole());
                
                // Send login notification
                sendLoginNotification(employee);
                
                return employee;
            } else {
                System.out.println("[AUTH SERVICE] ✗ Invalid password for: " + username);
                System.out.println("========================================\n");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("[AUTH SERVICE] Error during login: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error during login", e);
        }
    }
    
    @Override
    public boolean resendOTP(String username) throws RemoteException {
        // Invalidate old OTP and request new one
        System.out.println("[AUTH SERVICE] Resending OTP for: " + username);
        OTPManager.invalidateOTP(username);
        return requestOTP(username);
    }
    
    /**
     * Send OTP via ActiveMQ queue
     */
    private void sendOTPViaActiveMQ(String email, String username, String otp) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            // Create connection factory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConfig.USERNAME,
                ActiveMQConfig.PASSWORD,
                ActiveMQConfig.BROKER_URL
            );
            
            // Create connection
            connection = connectionFactory.createConnection();
            connection.start();
            
            // Create session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Create queue
            Destination destination = session.createQueue(ActiveMQConfig.OTP_EMAIL_QUEUE);
            
            // Create producer
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            
            // Create map message
            MapMessage message = session.createMapMessage();
            message.setString("email", email);
            message.setString("username", username);
            message.setString("otp", otp);
            
            // Send message
            producer.send(message);
            
            System.out.println("[AUTH SERVICE] OTP message sent to ActiveMQ");
            
            // Clean up
            
            
        } catch (Exception e) {
            System.err.println("[AUTH SERVICE] Error sending OTP via ActiveMQ: " + e.getMessage());
            e.printStackTrace();
        } finally{
            try{
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            } catch(JMSException e){
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Send login notification
     */
    private void sendLoginNotification(Employee employee) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            String email = employee.getEmployeeProfile().getEmail();
            String subject = "Login Alert - Epignosis Supermarket";
            String body = "Hello " + employee.getFullName() + ",\n\n" +
                         "You have successfully logged into the Epignosis Supermarket Management System.\n\n" +
                         "Time: " + new java.util.Date() + "\n\n" +
                         "If this wasn't you, please contact the administrator immediately.\n\n" +
                         "Thank you,\nEpignosis Supermarket Team";
            
            // Send via ActiveMQ
            connection = new ActiveMQConnectionFactory(
                ActiveMQConfig.USERNAME,
                ActiveMQConfig.PASSWORD,
                ActiveMQConfig.BROKER_URL
            ).createConnection();
            
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(ActiveMQConfig.NOTIFICATION_QUEUE);
            producer = session.createProducer(destination);
            
            MapMessage message = session.createMapMessage();
            message.setString("email", email);
            message.setString("subject", subject);
            message.setString("body", body);
            
            producer.send(message);
            
            producer.close();
            session.close();
            connection.close();
            
            System.out.println("[AUTH SERVICE] Login notification sent");
            
        } catch (Exception e) {
            System.err.println("[AUTH SERVICE] Error sending login notification: " + e.getMessage());
        }
    }  
}
