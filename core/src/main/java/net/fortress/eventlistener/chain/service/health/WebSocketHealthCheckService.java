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

package net.fortress.eventlistener.chain.service.health;

import net.fortress.eventlistener.chain.service.BlockchainException;
import net.fortress.eventlistener.chain.service.BlockchainService;
import net.fortress.eventlistener.chain.service.health.strategy.ReconnectionStrategy;
import net.fortress.eventlistener.chain.service.strategy.BlockSubscriptionStrategy;
import net.fortress.eventlistener.monitoring.EventlistenerValueMonitor;
import net.fortress.eventlistener.service.EventStoreService;
import net.fortress.eventlistener.service.SubscriptionService;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.websocket.EventlistenerWebSocketService;
import org.web3j.protocol.websocket.WebSocketClient;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class WebSocketHealthCheckService extends NodeHealthCheckService {

    private WebSocketClient webSocketClient;

    public WebSocketHealthCheckService(Web3jService web3jService,
                                       BlockchainService blockchainService,
                                       BlockSubscriptionStrategy blockSubscription,
                                       ReconnectionStrategy failureListener,
                                       SubscriptionService subscriptionService,
                                       EventlistenerValueMonitor valueMonitor,
                                       EventStoreService eventStoreService,
                                       Integer syncingThreshold,
                                       ScheduledThreadPoolExecutor taskScheduler,
                                       Long healthCheckPollInterval
    ) {
        super(blockchainService, blockSubscription, failureListener, subscriptionService,
                valueMonitor, eventStoreService, syncingThreshold, taskScheduler, healthCheckPollInterval);

        if (web3jService instanceof EventlistenerWebSocketService) {
            this.webSocketClient = ((EventlistenerWebSocketService)web3jService).getWebSocketClient();
        } else {
            throw new BlockchainException(
                    "Non web socket service passed to WebSocketHealthCheckService");
        }

    }

    @Override
    protected boolean isSubscribed() {
        return super.isSubscribed() && webSocketClient.isOpen();
    }
}
