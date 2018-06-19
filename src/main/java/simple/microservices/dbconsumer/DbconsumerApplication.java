package simple.microservices.dbconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import simple.microservices.dbconsumer.consumer.Consumer;

@SpringBootApplication
public class DbconsumerApplication implements CommandLineRunner {

    @Autowired
    ApplicationContext context;

    @Value("${activemq.consumers}")
    private int consumerCount;

    public static void main(String[] args) {
        SpringApplication.run(DbconsumerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        for(int i = 0; i < consumerCount; i++) {
            context.getBean(Consumer.class).init();
        }

    }
}
