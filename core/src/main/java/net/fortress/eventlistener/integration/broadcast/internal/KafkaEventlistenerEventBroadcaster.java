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

package net.fortress.eventlistener.integration.broadcast.internal;

import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.dto.message.*;
import net.fortress.eventlistener.integration.KafkaSettings;
import net.fortress.eventlistener.model.TransactionMonitoringSpec;
import net.fortress.eventlistener.utils.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * An EventlistenerEventBroadcaster that broadcasts the events to a Kafka queue.
 *
 * The topic name can be configured via the kafka.topic.eventlistenerEvents property.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public class KafkaEventlistenerEventBroadcaster implements EventlistenerEventBroadcaster {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventlistenerEventBroadcaster.class);

    private KafkaTemplate<String, EventlistenerMessage> kafkaTemplate;

    private KafkaSettings kafkaSettings;

    public KafkaEventlistenerEventBroadcaster(KafkaTemplate<String, EventlistenerMessage> kafkaTemplate,
                                         KafkaSettings kafkaSettings) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaSettings = kafkaSettings;
    }

    @Override
    public void broadcastEventFilterAdded(ContractEventFilter filter) {
        sendMessage(createContractEventFilterAddedMessage(filter));
    }

    @Override
    public void broadcastEventFilterRemoved(ContractEventFilter filter) {
        sendMessage(createContractEventFilterRemovedMessage(filter));
    }

    @Override
    public void broadcastTransactionMonitorAdded(TransactionMonitoringSpec spec) {
        sendMessage(createTransactionMonitorAddedMessage(spec));
    }

    @Override
    public void broadcastTransactionMonitorRemoved(TransactionMonitoringSpec spec) {
        sendMessage(createTransactionMonitorRemovedMessage(spec));
    }

    protected EventlistenerMessage createContractEventFilterAddedMessage(ContractEventFilter filter) {
        return new ContractEventFilterAdded(filter);
    }

    protected EventlistenerMessage createContractEventFilterRemovedMessage(ContractEventFilter filter) {
        return new ContractEventFilterRemoved(filter);
    }

    protected EventlistenerMessage createTransactionMonitorAddedMessage(TransactionMonitoringSpec spec) {
        return new TransactionMonitorAdded(spec);
    }

    protected EventlistenerMessage createTransactionMonitorRemovedMessage(TransactionMonitoringSpec spec) {
        return new TransactionMonitorRemoved(spec);
    }

    private void sendMessage(EventlistenerMessage message) {
        LOG.info("Sending message: " + JSON.stringify(message));
        kafkaTemplate.send(kafkaSettings.getEventlistenerEventsTopic(), message.getId(), message);
    }
}
