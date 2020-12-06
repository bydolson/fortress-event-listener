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

package net.fortress.eventlistener.chain.contract;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortress.eventlistener.chain.block.BlockListener;
import net.fortress.eventlistener.chain.block.EventConfirmationBlockListener;
import net.fortress.eventlistener.chain.service.BlockchainService;
import net.fortress.eventlistener.chain.service.container.ChainServicesContainer;
import net.fortress.eventlistener.chain.service.domain.TransactionReceipt;
import net.fortress.eventlistener.chain.service.strategy.BlockSubscriptionStrategy;
import net.fortress.eventlistener.chain.settings.Node;
import net.fortress.eventlistener.chain.settings.NodeSettings;
import net.fortress.eventlistener.dto.event.ContractEventDetails;
import net.fortress.eventlistener.dto.event.ContractEventStatus;
import net.fortress.eventlistener.integration.broadcast.blockchain.BlockchainEventBroadcaster;
import net.fortress.eventlistener.service.AsyncTaskService;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * A contract event listener that initialises a block listener after being passed an unconfirmed event.
 *
 * This created block listener counts blocks since the event was first fired and broadcasts a CONFIRMED
 * event once the configured number of blocks have passed.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
@Component
@AllArgsConstructor
@Slf4j
public class BroadcastAndInitialiseConfirmationListener implements ContractEventListener {

    private ChainServicesContainer chainServicesContainer;
    private BlockchainEventBroadcaster eventBroadcaster;
    private NodeSettings nodeSettings;

    @Override
    public void onEvent(ContractEventDetails eventDetails) {
        if (eventDetails.getStatus() == ContractEventStatus.UNCONFIRMED) {

            final BlockSubscriptionStrategy blockSubscription = getBlockSubscriptionStrategy(eventDetails);
            final Node node = nodeSettings.getNode(eventDetails.getNodeName());

            if (shouldInstantlyConfirm(eventDetails)) {
                eventDetails.setStatus(ContractEventStatus.CONFIRMED);
                eventBroadcaster.broadcastContractEvent(eventDetails);

                return;
            }

            log.info("Registering an EventConfirmationBlockListener for event: {}", eventDetails.getId());
            blockSubscription.addBlockListener(createEventConfirmationBlockListener(eventDetails, node));
        }

        eventBroadcaster.broadcastContractEvent(eventDetails);
    }

    protected BlockListener createEventConfirmationBlockListener(ContractEventDetails eventDetails,Node node) {
        return new EventConfirmationBlockListener(eventDetails,
                getBlockchainService(eventDetails), getBlockSubscriptionStrategy(eventDetails), eventBroadcaster, node);
    }

    private BlockchainService getBlockchainService(ContractEventDetails eventDetails) {
        return chainServicesContainer.getNodeServices(
                eventDetails.getNodeName()).getBlockchainService();
    }

    private BlockSubscriptionStrategy getBlockSubscriptionStrategy(ContractEventDetails eventDetails) {
        return chainServicesContainer.getNodeServices(
                eventDetails.getNodeName()).getBlockSubscriptionStrategy();
    }

    private boolean shouldInstantlyConfirm(ContractEventDetails eventDetails) {
        final BlockchainService blockchainService = getBlockchainService(eventDetails);
        final Node node = nodeSettings.getNode(blockchainService.getNodeName());
        BigInteger currentBlock = blockchainService.getCurrentBlockNumber();
        BigInteger waitBlocks = node.getBlocksToWaitForConfirmation();

        return currentBlock.compareTo(eventDetails.getBlockNumber().add(waitBlocks)) >= 0
            && isTransactionStillInBlock(
                    eventDetails.getTransactionHash(), eventDetails.getBlockHash(), blockchainService);
    }

    private boolean isTransactionStillInBlock(String txHash, String blockHash, BlockchainService blockchainService) {
        final TransactionReceipt receipt = blockchainService.getTransactionReceipt(txHash);

        return receipt != null && receipt.getBlockHash().equals(blockHash);
    }
}
