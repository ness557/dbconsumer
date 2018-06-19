package simple.microservices.dbconsumer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import simple.microservices.dbconsumer.model.Sequence;

@Repository
public class SequenceRepoImpl implements SequenceRepository{

    private MongoOperations operations;

    @Autowired
    public SequenceRepoImpl(MongoOperations mongoOperation){
        operations = mongoOperation;
    }

    @Override
    public int getNextSeqId(String key) {
        //get sequence id
        Query query = new Query(Criteria.where("_id").is(key));

        //increase sequence id by 1
        Update update = new Update();
        update.inc("number", 1);

        //return new increased id
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        //this is the magic happened.
        Sequence seqId =
                operations.findAndModify(query, update, options, Sequence.class);

        //if no id, throws SequenceException
        //optional, just a way to tell user when the sequence id is failed to generate.
        if (seqId == null) {
            throw new RuntimeException("Unable to get sequence id for key : " + key);
        }

        return seqId.getNumber();
    }

}
