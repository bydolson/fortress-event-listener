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
import net.fortress.eventlistener.integration.eventstore.EventStore;
import net.fortress.eventlistener.integration.eventstore.SaveableEventStore;
import net.fortress.eventlistener.integration.eventstore.db.MongoEventStore;
import net.fortress.eventlistener.integration.eventstore.db.SqlEventStore;
import net.fortress.eventlistener.integration.eventstore.db.repository.ContractEventDetailsRepository;
import net.fortress.eventlistener.integration.eventstore.db.repository.LatestBlockRepository;
import net.fortress.eventlistener.integration.eventstore.rest.RESTEventStore;
import net.fortress.eventlistener.integration.eventstore.rest.client.EventStoreClient;

import net.fortress.eventlistener.monitoring.EventlistenerValueMonitor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Order(0)
public class EventStoreConfiguration {

	@Configuration
	@ConditionalOnExpression("'${eventStore.type}:${database.type}'=='DB:MONGO'")
	@ConditionalOnMissingBean(EventStoreFactory.class)
	public static class MongoEventStoreConfiguration {

		@Bean
		public SaveableEventStore dbEventStore(
				ContractEventDetailsRepository contractEventRepository,
				LatestBlockRepository latestBlockRepository,
				MongoTemplate mongoTemplate) {
			return new MongoEventStore(contractEventRepository, latestBlockRepository, mongoTemplate);
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
			return new EventStoreLatestBlockUpdater(eventStore, blockDetailsFactory, valueMonitor, chainServicesContainer);
		}
	}

	@Configuration
	@ConditionalOnExpression("'${eventStore.type}:${database.type}'=='DB:SQL'")
	@ConditionalOnMissingBean(EventStoreFactory.class)
	public static class SqlEventStoreConfiguration {

		@Bean
		public SaveableEventStore dbEventStore(
				ContractEventDetailsRepository contractEventRepository,
				LatestBlockRepository latestBlockRepository,
				JdbcTemplate jdbcTemplate) {
			return new SqlEventStore(contractEventRepository, latestBlockRepository, jdbcTemplate);
		}

		@Bean
		public ContractEventListener eventStoreContractEventUpdater(SaveableEventStore eventStore) {
			return new EventStoreContractEventUpdater(eventStore);
		}

		@Bean
		public BlockListener eventStoreLatestBlockUpdater(SaveableEventStore eventStore,
														  BlockDetailsFactory blockDetailsFactory,
														  EventlistenerValueMonitor valueMonitor,
														  ChainServicesContainer chainServiceContainer ) {
			return new EventStoreLatestBlockUpdater(eventStore, blockDetailsFactory, valueMonitor,chainServiceContainer);
		}
	}

	@Configuration
	@ConditionalOnProperty(name = "eventStore.type", havingValue = "REST")
	@ConditionalOnMissingBean(EventStoreFactory.class)
	public static class RESTEventStoreConfiguration {

		@Bean
		public EventStore RESTEventStore(EventStoreClient client) {
			return new RESTEventStore(client);
		}
	}




}
