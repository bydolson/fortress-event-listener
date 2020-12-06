package fortress.eventlistener.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import fortress.eventconsumer.model.Name;

import java.util.List;

@Repository
public interface NamesRepository extends MongoRepository<Name, String> {

    List<Name> findByFirstNameStartingWith(String startingWith);

    List<Name> findBySurname(String surname);
}
