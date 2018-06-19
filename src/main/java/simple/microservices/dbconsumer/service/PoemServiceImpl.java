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
import simple.microservices.dbconsumer.repository.SequenceRepository;
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

    private PoemCrudRepository jpaRepository;
    private TagCrudRepository tagCrudRepository;
    private PoemMongoRepository mongoRepository;
    private SequenceRepository sequenceRepository;
    private Lock lock;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PoemServiceImpl(PoemCrudRepository poemCrudRepository,
                           TagCrudRepository tagCrudRepository,
                           PoemMongoRepository poemMongoRepository,
                           SequenceRepository sequenceRepository) {
        this.jpaRepository = poemCrudRepository;
        this.tagCrudRepository = tagCrudRepository;
        this.mongoRepository = poemMongoRepository;
        this.sequenceRepository = sequenceRepository;
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
            poem.setId(sequenceRepository.getNextSeqId(SeqKeys.POEM_SEQ));

            if (!jpaRepository.existsByUsernameAndData(poem.getUsername(), poem.getData())) {

                Set<Tag> updatedTags = new HashSet<>();
                for (Tag t : poem.getTags()) {
                    Tag fromDB = tagCrudRepository.findByName(t.getName());
                    if (fromDB != null)
                        t.setId(fromDB.getId());
                    else
                        t.setId(sequenceRepository.getNextSeqId(SeqKeys.TAG_SEQ));
                    updatedTags.add(t);

                }
                poem.setTags(updatedTags);

                jpaRepository.save(poem);
                logger.info("added");
            }

            logger.info("Trying to add poem to mongo repo");
            if (!mongoRepository.existsByUsernameAndData(poem.getUsername(), poem.getData())) {

                mongoRepository.save(poem);
                logger.info("added");
            }
        } finally {
            lock.unlock();
        }
    }
}