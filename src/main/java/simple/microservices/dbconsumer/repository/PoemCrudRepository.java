package simple.microservices.dbconsumer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import simple.microservices.dbconsumer.model.Poem;


@Repository
public interface PoemCrudRepository extends CrudRepository<Poem, Integer> {
    boolean existsByUsernameAndData(String username, String Data);
}
