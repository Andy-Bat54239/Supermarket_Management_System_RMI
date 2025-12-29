package util;

import java.util.Map;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

/**
 *
 * @author andyb
 */
public class ActiveMQProducer {
    
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    
    public ActiveMQProducer(String queueName) throws JMSException {
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
        
        // Create producer
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        
        System.out.println("[ACTIVEMQ PRODUCER] Connected to queue: " + queueName);
    }
    
    /**
     * Send a text message to the queue
     */
    public void sendMessage(String messageText) throws JMSException {
        TextMessage message = session.createTextMessage(messageText);
        producer.send(message);
        System.out.println("[ACTIVEMQ PRODUCER] Message sent: " + messageText);
    }
    
    /**
     * Send a map message to the queue
     */
    public void sendMapMessage(java.util.Map<String, String> data) throws JMSException {
        MapMessage message = session.createMapMessage();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            message.setString(entry.getKey(), entry.getValue());
        }
        producer.send(message);
        System.out.println("[ACTIVEMQ PRODUCER] Map message sent");
    }
    
    /**
     * Close the producer
     */
    public void close() {
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            System.out.println("[ACTIVEMQ PRODUCER] Connection closed");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
}
