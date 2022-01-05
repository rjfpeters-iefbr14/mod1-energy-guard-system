package tld.yggdrasill.services.gcm.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import tld.yggdrasill.services.gcm.core.model.Contingency;

import java.util.UUID;

@Repository
public interface ContingencyRepository extends MongoRepository<Contingency, UUID>, QuerydslPredicateExecutor<Contingency> {

}
