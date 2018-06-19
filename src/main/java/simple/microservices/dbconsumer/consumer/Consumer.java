package simple.microservices.dbconsumer.consumer;

import javax.jms.JMSException;

public interface Consumer {
    void init() throws JMSException;
}
