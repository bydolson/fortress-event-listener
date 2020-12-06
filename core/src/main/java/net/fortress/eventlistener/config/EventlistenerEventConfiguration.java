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

package net.fortress.eventlistener.config;

import net.fortress.eventlistener.dto.message.EventlistenerMessage;
import net.fortress.eventlistener.integration.KafkaSettings;
import net.fortress.eventlistener.integration.broadcast.internal.DoNothingEventlistenerEventBroadcaster;
import net.fortress.eventlistener.integration.broadcast.internal.EventlistenerEventBroadcaster;
import net.fortress.eventlistener.integration.broadcast.internal.KafkaEventlistenerEventBroadcaster;
import net.fortress.eventlistener.integration.consumer.EventlistenerInternalEventConsumer;
import net.fortress.eventlistener.integration.consumer.KafkaFilterEventConsumer;
import net.fortress.eventlistener.service.SubscriptionService;
import net.fortress.eventlistener.service.TransactionMonitoringService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Spring bean configuration for the FilterEvent broadcaster and consumer.
 *
 * If broadcaster.multiInstance is set to true, then register a Kafka broadcaster,
 * otherwise register a dummy broadcaster that does nothing.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
@Configuration
public class EventlistenerEventConfiguration {

    @Bean
    @ConditionalOnProperty(name="broadcaster.multiInstance", havingValue="true")
    public EventlistenerEventBroadcaster kafkaFilterEventBroadcaster(KafkaTemplate<String, EventlistenerMessage> kafkaTemplate,
                                                                KafkaSettings kafkaSettings) {
        return new KafkaEventlistenerEventBroadcaster(kafkaTemplate, kafkaSettings);
    }

    @Bean
    @ConditionalOnProperty(name="broadcaster.multiInstance", havingValue="true")
    public EventlistenerInternalEventConsumer kafkaFilterEventConsumer(SubscriptionService subscriptionService,
                                                                  TransactionMonitoringService transactionMonitoringService,
                                                                  KafkaSettings kafkaSettings) {
        return new KafkaFilterEventConsumer(subscriptionService, transactionMonitoringService, kafkaSettings);
    }

    @Bean
    @ConditionalOnProperty(name="broadcaster.multiInstance", havingValue="false")
    public EventlistenerEventBroadcaster doNothingFilterEventBroadcaster() {
        return new DoNothingEventlistenerEventBroadcaster();
    }
}
