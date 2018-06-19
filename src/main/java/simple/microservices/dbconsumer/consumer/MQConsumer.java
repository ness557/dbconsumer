package simple.microservices.dbconsumer.consumer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import simple.microservices.dbconsumer.model.Poem;
import simple.microservices.dbconsumer.model.Tag;
import simple.microservices.dbconsumer.service.PoemService;

import javax.jms.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class MQConsumer implements Consumer {

    @Value("${activemq.address}")
    private String address;
    @Value("${activemq.topic.poem}")
    private String poemTopic;
    @Value("${activemq.topic.activity}")
    private String activityTopic;

    private PoemService poemService;

    @Autowired
    public MQConsumer(PoemService poemService) {
        this.poemService = poemService;
    }

    @Override
    public void init() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(address);

        // Create a Connection
        javax.jms.Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination poemDestination = session.createTopic(poemTopic);
        Destination activityDestination = session.createTopic(activityTopic);


        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer poemConsumer = session.createConsumer(poemDestination);
        MessageConsumer activityConsumer = session.createConsumer(activityDestination);

        poemConsumer.setMessageListener((msg) -> {
            try {
                if (msg instanceof MapMessage) {
                    MapMessage mapMessage = (MapMessage) msg;

                    String tagString = mapMessage.getString("tags");
                    Set<Tag> tags =
                            Arrays.stream(tagString.split(","))
                                    .map(n -> {
                                        Tag tag = new Tag();
                                        tag.setName(n);
                                        return tag;
                                    })
                                    .collect(Collectors.toSet());

                    Poem poem = new Poem(null,
                            mapMessage.getString("username"),
                            mapMessage.getString("data"),
                            tags);

                    poemService.addPoem(poem);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        activityConsumer.setMessageListener((msg) -> {
            try {
                //todo db poemService and repo for user activity
                if (msg instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) msg;
                    System.out.println(textMessage.getText());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

    }
}
