package com.asg.mf01;

import java.util.concurrent.CountDownLatch;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ClaimHandlerC00 implements MessageListener {
    private static final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void onMessage(Message message) {

        try {
            Claim claim = message.getBody(Claim.class);
            System.out.println("class: " + message.getStringProperty("class"));
            System.out.println("Hospital id: " + claim.getHospitalId());

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {

            InitialContext initContext = new InitialContext();
            Queue claimQ = (Queue) initContext.lookup("queue/claimQueue");

            String selector = "";

            if (args.length > 0)
                selector = args[0];
            
            System.out.println("selector: " + selector);
            //according to my test, from command line input, the selector has to be:  java -jar mf01.jar class=\'C0\'
            // System.out.println("args: " + Arrays.toString(args));
            
            try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin",
                    "admin");
                    JMSContext jmsContext = cf.createContext()) {

                JMSConsumer consumer = jmsContext.createConsumer(claimQ, selector);
                consumer.setMessageListener(new ClaimHandlerC00());

                latch.await();

            }

        } catch (NamingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
