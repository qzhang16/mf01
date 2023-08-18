package com.asg.mf01;

import java.util.Random;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ClaimApp {
    public static void main(String[] args) {
        try {
            InitialContext initContext = new InitialContext();
            Queue claimQ = (Queue) initContext.lookup("queue/claimQueue");

            try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin",
                    "admin");
                    JMSContext jmsContext = cf.createContext()) {

                JMSProducer producer = jmsContext.createProducer();
                Random rand01 = new Random(100);

                ObjectMessage msg = null;
                Claim claim = null;

                for (int i = 0; i < 10; i++) {
                    claim = new Claim();
                    claim.setHospitalId(i);
                    claim.setDoctorName("Doctor0" + i);
                    claim.setDoctorType("DocType" + rand01.nextInt(3));
                    claim.setInsuranceProvider("Insurance" + rand01.nextInt(4));
                    claim.setAmount(rand01.nextDouble() * 100);
                    
                    msg = jmsContext.createObjectMessage(claim);
                    msg.setStringProperty("class", "C" + rand01.nextInt(2));

                    producer.send(claimQ, msg);

                    System.out.println(
                            "ClaimApp sending: " + msg.getStringProperty("class") + " : " + claim.getHospitalId());
                    
                }
                

            } catch (JMSException e) {
                e.printStackTrace();
            }

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

}
