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

import net.fortress.eventlistener.constant.Constants;
import net.fortress.eventlistener.dto.block.BlockDetails;
import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.event.ContractEventStatus;
import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.dto.transaction.TransactionStatus;
import net.fortress.eventlistener.model.TransactionIdentifierType;
import net.fortress.eventlistener.model.TransactionMonitoringSpec;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public abstract class BroadcasterSmokeTest extends BaseIntegrationTest {

    @Test
    public void testBroadcastBlock() throws Exception {
        triggerBlocks(1);

        waitForBlockMessages(1);

        assertTrue("No blocks received", getBroadcastBlockMessages().size() >= 1);

        BlockDetails blockDetails = getBroadcastBlockMessages().get(0);
        assertEquals(1, blockDetails.getNumber().compareTo(BigInteger.ZERO));
        assertNotNull(blockDetails.getHash());
    }

    @Test
    public void testBroadcastContractEvent() throws Exception {

        final EventEmitter emitter = deployEventEmitterContract();

        final ContractEventFilter registeredFilter = registerDummyEventFilter(emitter.getContractAddress());
        emitter.emitEvent(stringToBytes("BytesValue"), BigInteger.TEN, "StringValue").send();

        waitForContractEventMessages(1);

        assertEquals(1, getBroadcastContractEvents().size());

        final ContractEventDetails eventDetails = getBroadcastContractEvents().get(0);
        verifyDummyEventDetails(registeredFilter, eventDetails, ContractEventStatus.CONFIRMED);
    }

    @Test
    public void testBroadcastTransactionEvent() throws Exception {

        final String txHash = sendTransaction();
        TransactionMonitoringSpec monitorSpec = new TransactionMonitoringSpec(TransactionIdentifierType.HASH, txHash, Constants.DEFAULT_NODE_NAME);

        monitorTransaction(monitorSpec);

        waitForTransactionMessages(1);

        assertEquals(1, getBroadcastTransactionMessages().size());

        final TransactionDetails txDetails = getBroadcastTransactionMessages().get(0);
        assertEquals(txHash, txDetails.getHash());
        assertEquals(TransactionStatus.CONFIRMED, txDetails.getStatus());
    }

    protected void onBlockMessageReceived(BlockDetails block) {
        getBroadcastBlockMessages().add(block);
    }

    protected void onContractEventMessageReceived(ContractEventDetails event) {
        getBroadcastContractEvents().add(event);
    }

    protected void onTransactionMessageReceived(TransactionDetails tx) {
        getBroadcastTransactionMessages().add(tx);
    }
}
