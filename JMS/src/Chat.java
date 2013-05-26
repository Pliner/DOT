import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class Chat implements javax.jms.MessageListener
{
    private TopicSession pubSession;
    private TopicSession subSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;

    /* Constructor. Establish JMS publisher and subscriber */
    public Chat(String username, String password)
            throws Exception
    {
        // Obtain a JNDI connection
        Properties env = new Properties();
        // ... specify the JNDI properties specific to the vendor

        env.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        env.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");

        InitialContext jndi = new InitialContext(env);

        // Look up a JMS connection factory
        TopicConnectionFactory conFactory =
                (TopicConnectionFactory) jndi.lookup("TopicConnectionFactory");

        // Create a JMS connection
        TopicConnection connection =
                conFactory.createTopicConnection(username, password);

        // Create two JMS session objects
        TopicSession pubSession =
                connection.createTopicSession(false,
                        Session.AUTO_ACKNOWLEDGE);
        TopicSession subSession =
                connection.createTopicSession(false,
                        Session.AUTO_ACKNOWLEDGE);


        Topic chat = pubSession.createTopic("Chat");
        // Look up a JMS topic

        // Create a JMS publisher and subscriber
        TopicPublisher publisher =
                pubSession.createPublisher(chat);
        TopicSubscriber subscriber =
                subSession.createSubscriber(chat);

        // Set a JMS message listener
        subscriber.setMessageListener(this);

        // Intialize the Chat application
        set(connection, pubSession, subSession, publisher, username);

        // Start the JMS connection; allows messages to be delivered
        connection.start();

    }

    /* Initialize the instance variables */
    public void set(TopicConnection con, TopicSession pubSess,
                    TopicSession subSess, TopicPublisher pub,
                    String username)
    {
        this.connection = con;
        this.pubSession = pubSess;
        this.subSession = subSess;
        this.publisher = pub;
        this.username = username;
    }

    /* Receive message from topic subscriber */
    public void onMessage(Message message)
    {
        try
        {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println(text);
        } catch (JMSException jmse)
        {
            jmse.printStackTrace();
        }
    }

    /* Create and send message using topic publisher */
    protected void writeMessage(String text) throws JMSException
    {
        TextMessage message = pubSession.createTextMessage();
        message.setText(username + " : <" + text + ">");
        publisher.publish(message);
    }

    /* Close the JMS connection */
    public void close() throws JMSException
    {
        connection.close();
    }

    /* Run the Chat client */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);
        try
        {
            Random random = new Random();
            String username = random.nextInt(100) + "";
            Chat chat = new Chat(username, "");

            System.out.println("Chat started with username=" + username);
            Scanner scanner = new Scanner(System.in);

            // Loop until the word "exit" is typed
            while (scanner.hasNextLine())
            {
                String s = scanner.nextLine();
                if (s.equalsIgnoreCase("exit"))
                {
                    chat.close(); // close down connection
                    System.exit(0);// exit program
                } else
                    chat.writeMessage(s);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
