package simple.microservices.dbconsumer.service;

import org.springframework.stereotype.Service;
import simple.microservices.dbconsumer.model.Poem;

public interface PoemService {
    void addPoem(Poem poem);
}
