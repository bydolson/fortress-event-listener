/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fortress.eventlistener.integration.broadcast.blockchain;

import net.fortress.eventlistener.dto.block.BlockDetails;
import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.dto.message.BlockEvent;
import net.fortress.eventlistener.dto.message.ContractEvent;
import net.fortress.eventlistener.dto.message.EventlistenerMessage;
import net.fortress.eventlistener.dto.message.TransactionEvent;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.integration.KafkaSettings;
import net.fortress.eventlistener.utils.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

/**
 * A BlockchainEventBroadcaster that broadcasts the events to a Kafka queue.
 *
 * The key for each message will defined by the correlationIdStrategy if configured,
 * or a combination of the transactionHash, blockHash and logIndex otherwise.
 *
 * The topic names for block and contract events can be configured via the
 * kafka.topic.contractEvents and kafka.topic.blockEvents properties.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public class KafkaBlockchainEventBroadcaster implements BlockchainEventBroadcaster {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaBlockchainEventBroadcaster.class);

    private KafkaTemplate<String, EventlistenerMessage> kafkaTemplate;

    private KafkaSettings kafkaSettings;

    private CrudRepository<ContractEventFilter, String> filterRespository;

    public KafkaBlockchainEventBroadcaster(KafkaTemplate<String, EventlistenerMessage> kafkaTemplate,
                                    KafkaSettings kafkaSettings,
                                    CrudRepository<ContractEventFilter, String> filterRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaSettings = kafkaSettings;
        this.filterRespository = filterRepository;
    }

    @Override
    public void broadcastNewBlock(BlockDetails block) {
        final EventlistenerMessage<BlockDetails> message = createBlockEventMessage(block);
        LOG.info("Sending block message: " + JSON.stringify(message));

        kafkaTemplate.send(kafkaSettings.getBlockEventsTopic(), message.getId(), message);
    }

    @Override
    public void broadcastContractEvent(ContractEventDetails eventDetails) {
        final EventlistenerMessage<ContractEventDetails> message = createContractEventMessage(eventDetails);
        LOG.info("Sending contract event message: " + JSON.stringify(message));

        kafkaTemplate.send(kafkaSettings.getContractEventsTopic(), getContractEventCorrelationId(message), message);
    }

    @Override
    public void broadcastTransaction(TransactionDetails transactionDetails) {
        final EventlistenerMessage<TransactionDetails> message = createTransactionEventMessage(transactionDetails);
        LOG.info("Sending transaction event message: " + JSON.stringify(message));

        kafkaTemplate.send(kafkaSettings.getTransactionEventsTopic(), transactionDetails.getBlockHash(), message);
    }

    protected EventlistenerMessage<BlockDetails> createBlockEventMessage(BlockDetails blockDetails) {
        return new BlockEvent(blockDetails);
    }

    protected EventlistenerMessage<ContractEventDetails> createContractEventMessage(ContractEventDetails contractEventDetails) {
        return new ContractEvent(contractEventDetails);
    }

    protected EventlistenerMessage<TransactionDetails> createTransactionEventMessage(TransactionDetails transactionDetails) {
        return new TransactionEvent(transactionDetails);
    }

    private String getContractEventCorrelationId(EventlistenerMessage<ContractEventDetails> message) {
        final Optional<ContractEventFilter> filter = filterRespository.findById(message.getDetails().getFilterId());

        if (!filter.isPresent() || filter.get().getCorrelationIdStrategy() == null) {
            return message.getId();
        }

        return filter
                .get()
                .getCorrelationIdStrategy()
                .getCorrelationId(message.getDetails());
    }
}
