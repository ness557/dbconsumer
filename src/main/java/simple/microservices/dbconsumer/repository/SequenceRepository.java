package simple.microservices.dbconsumer.repository;

public interface SequenceRepository {
    int getNextSeqId(String key);
}
