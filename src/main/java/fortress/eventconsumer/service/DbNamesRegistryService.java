package fortress.eventlistener.service;

import net.fortress.eventlistener.dto.event.ContractEventDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fortress.eventconsumer.converter.NameConverter;
import fortress.eventconsumer.model.Name;
import fortress.eventconsumer.repository.NamesRepository;

import java.util.List;

@Service
public class DbNamesRegistryService implements NamesRegistryService {

    private NamesRepository repository;
    private NameConverter<ContractEventDetails> converter;

    @Autowired
    public DbNamesRegistryService(NamesRepository repository,
                                  NameConverter<ContractEventDetails> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public void storeFromContractEvent(ContractEventDetails contractEvent) {
        final Name namedAccount = converter.convert(contractEvent);

        repository.save(namedAccount);
    }

    @Override
    public List<Name> searchBySurname(String surname) {
        return repository.findBySurname(surname);
    }

    @Override
    public List<Name> searchByFirstNameStartingWith(String startsWith) {
        return repository.findByFirstNameStartingWith(startsWith);
    }
}
