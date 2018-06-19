package simple.microservices.dbconsumer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import simple.microservices.dbconsumer.model.Tag;

@Repository
public interface TagCrudRepository extends CrudRepository<Tag, Integer> {
    Tag findByName(String name);
}
