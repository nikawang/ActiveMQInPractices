package per.daniel.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;


public class Consumer {

    private static String brokerURL = "failover:(tcp://10.66.218.108:61616,tcp://10.66.218.10:61616)?initialReconnectDelay=100";
    

    private static transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Session session;
    
    private String jobs[] = new String[]{"suspend", "delete"};
    
    public Consumer() throws JMSException {
    	factory = new ActiveMQConnectionFactory(brokerURL);
    	connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
    
    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }    
    
    public static void main(String[] args) throws JMSException {
    	Consumer consumer = new Consumer();
    	for (String job : consumer.jobs) {
    		Destination destination = consumer.getSession().createQueue("JOBS." + job);
    		MessageConsumer messageConsumer = consumer.getSession().createConsumer(destination);
    		messageConsumer.setMessageListener(new Listener(job));
    	}
    }
	
	public Session getSession() {
		return session;
	}


}
