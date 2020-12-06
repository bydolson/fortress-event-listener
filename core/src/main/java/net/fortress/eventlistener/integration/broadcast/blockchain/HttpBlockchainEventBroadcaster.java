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

package net.fortress.eventlistener.integration.broadcast.blockchain;

import net.fortress.eventlistener.dto.block.BlockDetails;
import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.integration.broadcast.BroadcastException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * A BlockchainEventBroadcaster that broadcasts the events via a http post.
 *
 * The url to post to for block and contract events can be configured via the
 * broadcast.http.contractEvents and broadcast.http.blockEvents properties.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public class HttpBlockchainEventBroadcaster implements BlockchainEventBroadcaster {

    private HttpBroadcasterSettings settings;

    private RestTemplate restTemplate;

    private RetryTemplate retryTemplate;

    public HttpBlockchainEventBroadcaster(HttpBroadcasterSettings settings, RetryTemplate retryTemplate) {
        this.settings = settings;

        restTemplate = new RestTemplate();
        this.retryTemplate = retryTemplate;
    }

    @Override
    public void broadcastNewBlock(BlockDetails block) {
        retryTemplate.execute((context) -> {
            final ResponseEntity<Void> response =
                    restTemplate.postForEntity(settings.getBlockEventsUrl(), block, Void.class);

            checkForSuccessResponse(response);
            return null;
        });
    }

    @Override
    public void broadcastContractEvent(ContractEventDetails eventDetails) {
        retryTemplate.execute((context) -> {
            final ResponseEntity<Void> response =
                    restTemplate.postForEntity(settings.getContractEventsUrl(), eventDetails, Void.class);

            checkForSuccessResponse(response);
            return null;
        });
    }

    @Override
    public void broadcastTransaction(TransactionDetails transactionDetails) {

    }

    private void checkForSuccessResponse(ResponseEntity<Void> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new BroadcastException(
                    String.format("Received a %s response when broadcasting via http", response.getStatusCode()));
        }
    }
}
