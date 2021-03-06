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

import net.fortress.eventlistener.chain.block.BlockListener;
import net.fortress.eventlistener.chain.block.EventStoreLatestBlockUpdater;
import net.fortress.eventlistener.chain.contract.ContractEventListener;
import net.fortress.eventlistener.chain.contract.EventStoreContractEventUpdater;
import net.fortress.eventlistener.chain.factory.BlockDetailsFactory;
import net.fortress.eventlistener.chain.service.container.ChainServicesContainer;
import net.fortress.eventlistener.factory.EventStoreFactory;
import net.fortress.eventlistener.integration.eventstore.SaveableEventStore;
import net.fortress.eventlistener.monitoring.EventlistenerValueMonitor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(1)
@ConditionalOnBean(EventStoreFactory.class)
public class CustomEventStoreConfiguration {

    @Bean
    public SaveableEventStore customEventStore(EventStoreFactory factory) {
        return factory.build();
    }

    @Bean
    public ContractEventListener eventStoreContractEventUpdater(SaveableEventStore eventStore) {
        return new EventStoreContractEventUpdater(eventStore);
    }

    @Bean
    public BlockListener eventStoreLatestBlockUpdater(SaveableEventStore eventStore,
                                                      BlockDetailsFactory blockDetailsFactory,
                                                      EventlistenerValueMonitor valueMonitor,
                                                      ChainServicesContainer chainServicesContainer) {
        return new EventStoreLatestBlockUpdater(eventStore, blockDetailsFactory,  valueMonitor, chainServicesContainer);
    }
}
