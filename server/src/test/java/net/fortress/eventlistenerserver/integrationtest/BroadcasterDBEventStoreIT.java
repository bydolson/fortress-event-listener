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

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import net.fortress.eventlistener.dto.block.BlockDetails;
import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.dto.transaction.TransactionStatus;
import net.fortress.eventlistener.integration.eventstore.EventStore;
import net.fortress.eventlistener.model.LatestBlock;
import net.fortress.eventlistener.utils.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.crypto.Keys;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations="classpath:application-test-db.properties")
public class BroadcasterDBEventStoreIT extends MainBroadcasterTests {

    @Autowired
    private EventStore eventStore;

    @Test
    public void testBroadcastsUnconfirmedEventAfterInitialEmit() throws Exception {
        doTestBroadcastsUnconfirmedEventAfterInitialEmit();
    }

    @Test
    public void testBroadcastNotOrderedEvent() throws Exception {
        doTestBroadcastsNotOrderedEvent();
    }

    @Test
    public void testBroadcastsConfirmedEventAfterBlockThresholdReached() throws Exception {
        doTestBroadcastsConfirmedEventAfterBlockThresholdReached();
    }

    @Test
    public void testContractEventForUnregisteredEventFilterNotBroadcast() throws Exception {
        doTestContractEventForUnregisteredEventFilterNotBroadcast();
    }

    @Test
    public void testBroadcastBlock() throws Exception {
        doTestBroadcastBlock();
    }

    @Test
    public void testBroadcastsUnconfirmedTransactionAfterInitialMining() throws Exception {
        doTestBroadcastsUnconfirmedTransactionAfterInitialMining();
    }

    @Test
    public void testBroadcastsConfirmedTransactionAfterBlockThresholdReached() throws Exception {
        doTestBroadcastsConfirmedTransactionAfterBlockThresholdReached();
    }

    @Test
    public void testBroadcastFailedTransactionFilteredByHash() throws Exception {
        doTestBroadcastFailedTransactionFilteredByHash();
    }

    @Test
    public void testBroadcastFailedTransactionFilteredByTo() throws Exception {
        doTestBroadcastFailedTransactionFilteredByTo();
    }

    @Test
    public void testBroadcastFailedTransactionFilteredByFrom() throws Exception {
        doTestBroadcastFailedTransactionFilteredByFrom();
    }

    @Test
    public void testBroadcastEventAddedToEventStore() throws Exception {

        final EventEmitter emitter = deployEventEmitterContract();

        final ContractEventFilter registeredFilter = registerDummyEventFilter(emitter.getContractAddress());
        emitter.emitEvent(stringToBytes("BytesValue"), BigInteger.TEN, "StringValue").send();

        waitForContractEventMessages(1);

        assertEquals("***** " + JSON.stringify(getBroadcastContractEvents()),1, getBroadcastContractEvents().size());

        final ContractEventDetails eventDetails = getBroadcastContractEvents().get(0);

        Thread.sleep(1000);

        List<ContractEventDetails> savedEvents = eventStore.getContractEventsForSignature(
            eventDetails.getEventSpecificationSignature(), Keys.toChecksumAddress(emitter.getContractAddress()), PageRequest.of(0, 100000)).getContent();

        assertEquals(1, savedEvents.size());
        assertEquals(eventDetails, savedEvents.get(0));
    }

    @Test
    public void testBroadcastBlockAddedToEventStore() throws Exception {
        doTestBroadcastBlock();

        Thread.sleep(1000);

        final Optional<LatestBlock> latestBlock = eventStore.getLatestBlockForNode("default");

        assertEquals(true, latestBlock.isPresent());

        final List<BlockDetails> broadcastBlocks = getBroadcastBlockMessages();
        assertEquals(broadcastBlocks.get(broadcastBlocks.size() - 1).getNumber(),
                latestBlock.get().getNumber());
    }
}
