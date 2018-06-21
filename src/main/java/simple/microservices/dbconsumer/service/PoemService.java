package simple.microservices.dbconsumer.service;

import org.springframework.stereotype.Service;
import simple.microservices.dbconsumer.model.Poem;
import simple.microservices.dbconsumer.model.Tag;

import java.util.Set;

public interface PoemService {
    void addPoem(Poem poem);
}
