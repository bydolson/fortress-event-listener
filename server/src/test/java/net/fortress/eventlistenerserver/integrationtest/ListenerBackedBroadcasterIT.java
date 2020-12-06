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

package net.fortress.eventlistenerserver.integrationtest;

import net.fortress.eventlistener.dto.block.BlockDetails;
import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.integration.broadcast.blockchain.BlockchainEventBroadcaster;
import net.fortress.eventlistener.integration.broadcast.blockchain.ListenerInvokingBlockchainEventBroadcaster;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test-db-listener.properties")
@Import(ListenerBackedBroadcasterIT.ListenerConfig.class)
public class ListenerBackedBroadcasterIT extends BroadcasterSmokeTest {

    @Override
    protected List<BlockDetails> getBroadcastBlockMessages() {
        return ListenerConfig.broadcastBlockMessages;
    }

    @Override
    protected List<ContractEventDetails> getBroadcastContractEvents() {
        return ListenerConfig.broadcastContractEvents;
    }

    @Override
    protected List<TransactionDetails> getBroadcastTransactionMessages() {
        return ListenerConfig.broadcastTransactionMessages;
    }

    @TestConfiguration
    static class ListenerConfig {

        private static List<BlockDetails> broadcastBlockMessages = new ArrayList<>();

        private static List<ContractEventDetails> broadcastContractEvents = new ArrayList<>();

        private static List<TransactionDetails> broadcastTransactionMessages = new ArrayList<>();

        @Bean
        public BlockchainEventBroadcaster listenerBroadcaster() {

            return new ListenerInvokingBlockchainEventBroadcaster(new ListenerInvokingBlockchainEventBroadcaster.OnBlockchainEventListener() {
                @Override
                public void onNewBlock(BlockDetails block) {
                    broadcastBlockMessages.add(block);
                }

                @Override
                public void onContractEvent(ContractEventDetails eventDetails) {
                    broadcastContractEvents.add(eventDetails);
                }

                @Override
                public void onTransactionEvent(TransactionDetails transactionDetails) {
                    broadcastTransactionMessages.add(transactionDetails);
                }
            });
        }
    }
}
