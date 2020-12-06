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
import net.fortress.eventlistener.dto.message.BlockEvent;
import net.fortress.eventlistener.dto.message.ContractEvent;
import net.fortress.eventlistener.dto.message.EventlistenerMessage;
import net.fortress.eventlistener.dto.message.TransactionEvent;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.integration.RabbitSettings;
import net.fortress.eventlistener.utils.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * A RabbitBlockChainEventBroadcaster that broadcasts the events to a RabbitMQ exchange.
 *
 * The routing key for each message will defined by the routingKeyPrefix configured,
 * plus filterId for new contract events.
 *
 * The exchange and routingKeyPrefix can be configured via the
 * rabbitmq.exchange and rabbitmq.routingKeyPrefix properties.
 *
 * @author ioBuilders technical team <tech@io.builders>
 */
public class RabbitBlockChainEventBroadcaster implements BlockchainEventBroadcaster {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitBlockChainEventBroadcaster.class);

    private RabbitTemplate rabbitTemplate;
    private RabbitSettings rabbitSettings;

    public RabbitBlockChainEventBroadcaster(RabbitTemplate rabbitTemplate, RabbitSettings rabbitSettings) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitSettings = rabbitSettings;
    }

    @Override
    public void broadcastNewBlock(BlockDetails block) {
        final EventlistenerMessage<BlockDetails> message = createBlockEventMessage(block);
        rabbitTemplate.convertAndSend(this.rabbitSettings.getExchange(),
                String.format("%s", this.rabbitSettings.getBlockEventsRoutingKey()),
                message);

        LOG.info(String.format("New block sent: [%s] to exchange [%s] with routing key [%s]",
                JSON.stringify(message),
                this.rabbitSettings.getExchange(),
                this.rabbitSettings.getBlockEventsRoutingKey())
        );
    }

    @Override
    public void broadcastContractEvent(ContractEventDetails eventDetails) {
        final EventlistenerMessage<ContractEventDetails> message = createContractEventMessage(eventDetails);
        rabbitTemplate.convertAndSend(this.rabbitSettings.getExchange(),
                String.format("%s.%s", this.rabbitSettings.getContractEventsRoutingKey(), eventDetails.getFilterId()),
                message);

        LOG.info(String.format("New contract event sent: [%s] to exchange [%s] with routing key [%s.%s]",
                JSON.stringify(message),
                this.rabbitSettings.getExchange(),
                this.rabbitSettings.getContractEventsRoutingKey(),
                eventDetails.getFilterId()));
    }

    @Override
    public void broadcastTransaction(TransactionDetails transactionDetails) {
        final EventlistenerMessage<TransactionDetails> message = createTransactionEventMessage(transactionDetails);
        rabbitTemplate.convertAndSend(this.rabbitSettings.getExchange(),
                String.format("%s.%s", this.rabbitSettings.getTransactionEventsRoutingKey(), transactionDetails.getHash()),
                message);

        LOG.info(String.format("New transaction event sent: [%s] to exchange [%s] with routing key [%s.%s]",
                JSON.stringify(message),
                this.rabbitSettings.getExchange(),
                this.rabbitSettings.getTransactionEventsRoutingKey(),
                transactionDetails.getHash()
                )
        );
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

}
