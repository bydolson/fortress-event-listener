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
import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.dto.message.EventlistenerMessage;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;


import java.util.ArrayList;
import java.util.List;

public class BaseRabbitIntegrationTest extends BaseIntegrationTest {

    private List<EventlistenerMessage<ContractEventFilter>> broadcastFiltersEventMessages = new ArrayList<>();

    public List<EventlistenerMessage<ContractEventFilter>> getBroadcastFilterEventMessages() {
        return broadcastFiltersEventMessages;
    }

    protected void clearMessages() {
        super.clearMessages();
        broadcastFiltersEventMessages.clear();
    }

    @RabbitListener(bindings = @QueueBinding(
            key = "thisIsRoutingKey.*",
            value = @Queue("ThisIsAEventsQueue"),
            exchange = @Exchange(value = "ThisIsAExchange", type = ExchangeTypes.TOPIC)
    ))
    public void onEvent(EventlistenerMessage message) {
        if(message.getDetails() instanceof ContractEventDetails){
            getBroadcastContractEvents().add((ContractEventDetails) message.getDetails());
        }
        else if(message.getDetails() instanceof BlockDetails){
            getBroadcastBlockMessages().add((BlockDetails) message.getDetails());
        }

    }
}
