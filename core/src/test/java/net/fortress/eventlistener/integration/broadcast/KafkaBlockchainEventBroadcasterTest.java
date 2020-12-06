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

package net.fortress.eventlistener.integration.broadcast;

import net.fortress.eventlistener.dto.block.BlockDetails;
import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.dto.event.filter.correlationId.CorrelationIdStrategy;
import net.fortress.eventlistener.dto.message.BlockEvent;
import net.fortress.eventlistener.dto.message.ContractEvent;
import net.fortress.eventlistener.dto.message.EventlistenerMessage;
import net.fortress.eventlistener.integration.KafkaSettings;
import net.fortress.eventlistener.integration.broadcast.blockchain.KafkaBlockchainEventBroadcaster;
import net.fortress.eventlistener.repository.ContractEventFilterRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class KafkaBlockchainEventBroadcasterTest {

    private static final String BLOCK_EVENTS_TOPIC = "ThisIsABlockTopic";

    private static final String CONTRACT_EVENTS_TOPIC = "ThisIsAnEventTopic";

    private static final String FILTER_ID = "filter-id";

    private KafkaBlockchainEventBroadcaster underTest;

    private KafkaTemplate<String, EventlistenerMessage> mockKafkaTemplate;

    private KafkaSettings mockKafkaSettings;

    private ContractEventFilterRepository mockFilterRepository;

    @Before
    public void init() {
        mockKafkaTemplate = mock(KafkaTemplate.class);
        mockKafkaSettings = mock(KafkaSettings.class);
        mockFilterRepository = mock(ContractEventFilterRepository.class);

        when(mockKafkaSettings.getBlockEventsTopic()).thenReturn(BLOCK_EVENTS_TOPIC);
        when(mockKafkaSettings.getContractEventsTopic()).thenReturn(CONTRACT_EVENTS_TOPIC);

        underTest = new KafkaBlockchainEventBroadcaster(mockKafkaTemplate, mockKafkaSettings, mockFilterRepository);
    }

    @Test
    public void testBroadcastNewBlock() {
        final BlockDetails blockDetails = createBlockDetails();

        underTest.broadcastNewBlock(blockDetails);

        final ArgumentCaptor<EventlistenerMessage> eventCaptor = ArgumentCaptor.forClass(EventlistenerMessage.class);
        final ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockKafkaTemplate).send(eq(BLOCK_EVENTS_TOPIC), idCaptor.capture(), eventCaptor.capture());

        assertEquals(BlockEvent.TYPE, eventCaptor.getValue().getType());
        assertEquals(blockDetails, eventCaptor.getValue().getDetails());
        assertNotNull(idCaptor.getValue());
        assertEquals(eventCaptor.getValue().getId(), idCaptor.getValue());
    }

    @Test
    public void testBroadcastContractEvent() {
        final ContractEventDetails eventDetails = createContractEventDetails();

        underTest.broadcastContractEvent(eventDetails);

        final ArgumentCaptor<EventlistenerMessage> eventCaptor = ArgumentCaptor.forClass(EventlistenerMessage.class);
        verify(mockKafkaTemplate).send(eq(CONTRACT_EVENTS_TOPIC), anyString(), eventCaptor.capture());

        assertEquals(ContractEvent.TYPE, eventCaptor.getValue().getType());
        assertEquals(eventDetails, eventCaptor.getValue().getDetails());
    }

    @Test
    public void testBroadcastContractEventDefaultCorrelationId() {
        final ContractEventDetails eventDetails = createContractEventDetails();

        underTest.broadcastContractEvent(eventDetails);

        final ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockKafkaTemplate).send(eq(CONTRACT_EVENTS_TOPIC), idCaptor.capture(), any(EventlistenerMessage.class));

        assertEquals(eventDetails.getId(), idCaptor.getValue());
    }

    @Test
    public void testCorrelationIdWhenStrategySetOnFilter() {

        final ContractEventDetails eventDetails = createContractEventDetails();

        final CorrelationIdStrategy mockIdStrategy = mock(CorrelationIdStrategy.class);
        when(mockIdStrategy.getCorrelationId(eventDetails)).thenReturn("12-34");

        final ContractEventFilter mockFilter = mock(ContractEventFilter.class);
        when(mockFilter.getCorrelationIdStrategy()).thenReturn(mockIdStrategy);

        when(mockFilterRepository.findById(FILTER_ID)).thenReturn(Optional.of(mockFilter));

        underTest.broadcastContractEvent(eventDetails);

        final ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockKafkaTemplate).send(eq(CONTRACT_EVENTS_TOPIC), idCaptor.capture(), any(EventlistenerMessage.class));

        assertNotNull(idCaptor.getValue());
        assertEquals("12-34", idCaptor.getValue());
    }

    private BlockDetails createBlockDetails() {
        final BlockDetails blockDetails = new BlockDetails();
        blockDetails.setHash("0x86e01e667d3e9a0c624ca2e30b1b452973b7ba2802bb2f2c30ce399dd6131741");

        return blockDetails;
    }

    private ContractEventDetails createContractEventDetails() {
        final ContractEventDetails contractEventDetails = new ContractEventDetails();
        contractEventDetails.setBlockHash("0x86e01e667d3e9a0c624ca2e30b1b452973b7ba2802bb2f2c30ce399dd6131741");
        contractEventDetails.setTransactionHash("0x7ba0d5bf4dd88d9bca44957460a7e69fffbf9604288a7d4e4a9d6c7e75c627b4");
        contractEventDetails.setLogIndex(BigInteger.ONE);
        contractEventDetails.setFilterId(FILTER_ID);

        return contractEventDetails;
    }
}