package fortress.eventlistener.service;

import net.fortress.eventlistener.dto.event.ContractEventDetails;
import fortress.eventconsumer.model.Name;

import java.util.List;

public interface NamesRegistryService {

    void storeFromContractEvent(ContractEventDetails contractEvent);

    List<Name> searchBySurname(String surname);

    List<Name> searchByFirstNameStartingWith(String startsWith);
}
