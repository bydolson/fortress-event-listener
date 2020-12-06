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

package net.fortress.eventlistener.chain.block.tx;

import net.fortress.eventlistener.chain.block.AbstractConfirmationBlockListener;
import net.fortress.eventlistener.chain.service.BlockchainService;
import net.fortress.eventlistener.chain.service.strategy.BlockSubscriptionStrategy;
import net.fortress.eventlistener.chain.settings.Node;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.dto.transaction.TransactionStatus;
import net.fortress.eventlistener.integration.broadcast.blockchain.BlockchainEventBroadcaster;

import java.util.List;

public class TransactionConfirmationBlockListener extends AbstractConfirmationBlockListener<TransactionDetails> {

    private BlockchainEventBroadcaster eventBroadcaster;
    private OnConfirmedCallback onConfirmedCallback;
    private List<TransactionStatus> statusesToFilter;

    public TransactionConfirmationBlockListener(TransactionDetails transactionDetails,
                                                BlockchainService blockchainService,
                                                BlockSubscriptionStrategy blockSubscription,
                                                BlockchainEventBroadcaster eventBroadcaster,
                                                Node node,
                                                List<TransactionStatus> statusesToFilter,
                                                OnConfirmedCallback onConfirmedCallback) {
        super(transactionDetails, blockchainService, blockSubscription, node);
        this.eventBroadcaster = eventBroadcaster;
        this.onConfirmedCallback = onConfirmedCallback;
        this.statusesToFilter = statusesToFilter;
    }

    @Override
    protected void broadcastEventConfirmed() {
        super.broadcastEventConfirmed();

        onConfirmedCallback.onConfirmed();
    }

    @Override
    protected String getEventIdentifier(TransactionDetails transactionDetails) {
        return transactionDetails.getHash() + transactionDetails.getBlockHash();
    }

    @Override
    protected void setStatus(TransactionDetails transactionDetails, String status) {
        transactionDetails.setStatus(TransactionStatus.valueOf(status));
    }

    @Override
    protected void broadcast(TransactionDetails transactionDetails) {
        if (statusesToFilter.contains(transactionDetails.getStatus())) {
            eventBroadcaster.broadcastTransaction(transactionDetails);
        }
    }

    public interface OnConfirmedCallback {
        void onConfirmed();
    }
}
