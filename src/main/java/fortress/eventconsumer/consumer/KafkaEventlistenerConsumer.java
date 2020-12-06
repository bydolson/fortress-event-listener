package fortress.eventlistener.consumer;

import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.message.EventlistenerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import fortress.eventconsumer.service.NamesRegistryService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class KafkaEventlistenerConsumer {

    private static final String NAME_ADDED_EVENT = "NameAdded";

    private NamesRegistryService namesRegistryService;

    private Map<String, Consumer<ContractEventDetails>> consumers;

    @Autowired
    public KafkaEventlistenerConsumer(NamesRegistryService namesRegistryService) {
        this.namesRegistryService = namesRegistryService;

        consumers = new HashMap<>();

        consumers.put(NAME_ADDED_EVENT, (contractEventDetails -> {
            namesRegistryService.storeFromContractEvent(contractEventDetails);
        }));
    }

    @KafkaListener(topics = "contract-events", groupId = "eventlistenerExample")
    public void consumeContractEvent(EventlistenerMessage<ContractEventDetails> message) {
        final ContractEventDetails contractEventDetails = message.getDetails();
        final String eventName = contractEventDetails.getName();

        if (consumers.containsKey(eventName)) {
            consumers.get(eventName).accept(contractEventDetails);
        }
    }
}
