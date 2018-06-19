package simple.microservices.dbconsumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import simple.microservices.dbconsumer.model.Poem;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface PoemMongoRepository extends MongoRepository<Poem, Integer> {
    boolean existsByUsernameAndData(String username, String Data);
}
