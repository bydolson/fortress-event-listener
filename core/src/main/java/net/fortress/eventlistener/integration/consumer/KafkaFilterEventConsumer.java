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

package net.fortress.eventlistener.integration.consumer;

import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.dto.message.*;
import net.fortress.eventlistener.integration.KafkaSettings;
import net.fortress.eventlistener.model.TransactionMonitoringSpec;
import net.fortress.eventlistener.service.TransactionMonitoringService;
import net.fortress.eventlistener.service.exception.NotFoundException;
import net.fortress.eventlistener.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A FilterEventConsumer that consumes ContractFilterEvents messages from a Kafka topic.
 *
 * The topic to be consumed from can be configured via the kafka.topic.contractEvents property.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public class KafkaFilterEventConsumer implements EventlistenerInternalEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaFilterEventConsumer.class);

    private final Map<String, Consumer<EventlistenerMessage>> messageConsumers;

    @Autowired
    public KafkaFilterEventConsumer(SubscriptionService subscriptionService,
                                    TransactionMonitoringService transactionMonitoringService,
                                    KafkaSettings kafkaSettings) {

        messageConsumers = new HashMap<>();
        messageConsumers.put(ContractEventFilterAdded.TYPE, (message) -> {
            subscriptionService.registerContractEventFilter(
                    (ContractEventFilter) message.getDetails(), false);
        });

        messageConsumers.put(ContractEventFilterRemoved.TYPE, (message) -> {
            try {
                subscriptionService.unregisterContractEventFilter(
                        ((ContractEventFilter) message.getDetails()).getId(), false);
            } catch (NotFoundException e) {
                logger.debug("Received filter removed message but filter doesn't exist. (We probably sent message)");
            }
        });

        messageConsumers.put(TransactionMonitorAdded.TYPE, (message) -> {
            transactionMonitoringService.registerTransactionsToMonitor(
                    (TransactionMonitoringSpec) message.getDetails(), false);
        });

        messageConsumers.put(TransactionMonitorRemoved.TYPE, (message) -> {
            try {
                transactionMonitoringService.stopMonitoringTransactions(
                        ((TransactionMonitoringSpec) message.getDetails()).getId(), false);
            } catch (NotFoundException e) {
                logger.debug("Received transaction monitor removed message but monitor doesn't exist. (We probably sent message)");
            }
        });
    }

    @Override
    @KafkaListener(topics = "#{eventlistenerKafkaSettings.eventlistenerEventsTopic}", groupId = "#{eventlistenerKafkaSettings.groupId}",
            containerFactory = "eventlistenerKafkaListenerContainerFactory")
    public void onMessage(EventlistenerMessage message) {
        final Consumer<EventlistenerMessage> consumer = messageConsumers.get(message.getType());

        if (consumer == null) {
            logger.error(String.format("No consumer for message type %s!", message.getType()));
            return;
        }

        consumer.accept(message);
    }
}
