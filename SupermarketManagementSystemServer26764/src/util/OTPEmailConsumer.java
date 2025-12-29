package util;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

/**
 *
 * @author andyb
 */
public class OTPEmailConsumer implements Runnable{
    
    private String queueName;
    private volatile boolean running = true;
    
    public OTPEmailConsumer(String queueName) {
        this.queueName = queueName;
    }
    
    @Override
    public void run() {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        
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
            Destination destination = session.createQueue(queueName);
            
            // Create consumer
            consumer = session.createConsumer(destination);
            
            System.out.println("[OTP EMAIL CONSUMER] Listening to queue: " + queueName);
            
            // Listen for messages
            while (running) {
                Message message = consumer.receive(1000); // 1 second timeout
                
                if (message instanceof MapMessage) {
                    MapMessage mapMessage = (MapMessage) message;
                    
                    String email = mapMessage.getString("email");
                    String username = mapMessage.getString("username");
                    String otp = mapMessage.getString("otp");
                    
                    System.out.println("[OTP EMAIL CONSUMER] Received OTP request for: " + username);
                    
                    // Send email
                    boolean sent = EmailSender.sendOTPEmail(email, username, otp);
                    
                    if (sent) {
                        System.out.println("[OTP EMAIL CONSUMER] OTP email sent successfully");
                    } else {
                        System.err.println("[OTP EMAIL CONSUMER] Failed to send OTP email");
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("[OTP EMAIL CONSUMER] Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (consumer != null) consumer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void stop() {
        running = false;
        System.out.println("[OTP EMAIL CONSUMER] Stopping...");
    }
    
}
