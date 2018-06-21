package simple.microservices.dbconsumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import simple.microservices.dbconsumer.model.Poem;
import simple.microservices.dbconsumer.model.Tag;
import simple.microservices.dbconsumer.repository.PoemCrudRepository;
import simple.microservices.dbconsumer.repository.PoemMongoRepository;
import simple.microservices.dbconsumer.repository.TagCrudRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Scope("singleton")
@Transactional
public class PoemServiceImpl implements PoemService {

    private PoemCrudRepository poemRepository;
    private TagCrudRepository tagRepository;
    private PoemMongoRepository poemMongoRepository;
    private Lock lock;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PoemServiceImpl(PoemCrudRepository poemCrudRepository,
                           TagCrudRepository tagRepository,
                           PoemMongoRepository poemMongoRepository) {
        this.poemRepository = poemCrudRepository;
        this.tagRepository = tagRepository;
        this.poemMongoRepository = poemMongoRepository;
        this.lock = new ReentrantLock();

    }

    @Override
    public void addPoem(Poem poem) {

        Thread t = new Thread(() -> {
            syncAdd(poem, lock);
        });

        t.start();
    }

    private void syncAdd(Poem poem, Lock lock) {
        lock.lock();

        try {
            logger.info("Trying to add poem to jpa repo");

            if (!poemRepository.existsByUsernameAndData(poem.getUsername(), poem.getData())) {

                Set<Tag> updatedTags = new HashSet<>();
                Set<Tag> tags = poem.getTags();

                for (Tag t : tags) {
                    Tag fromDB = tagRepository.findByName(t.getName());
                    if (fromDB != null)
                        t = fromDB;
                    t.getPoems().add(poem);
                    updatedTags.add(t);
                }

                tagRepository.save(updatedTags);

                logger.info("added");
            }

            logger.info("Trying to add poem to mongo repo");
            if (!poemMongoRepository.existsByUsernameAndData(poem.getUsername(), poem.getData())) {

                poemMongoRepository.save(poem);
                logger.info("added");
            }
        } finally {
            lock.unlock();
        }
    }
}