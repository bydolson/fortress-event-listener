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

package net.fortress.eventlistener.integration.broadcast.internal;

import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.model.TransactionMonitoringSpec;

/**
 * An interface for a class that broadcasts Eventlistener internal events to other Eventlistener instances in the wider system.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public interface EventlistenerEventBroadcaster {

    /**
     * Broadcasts the details of a contract event filter that has been added to this Eventlistener instance.
     *
     * @param filter the filter in question.
     */
    void broadcastEventFilterAdded(ContractEventFilter filter);

    /**
     * Broadcasts the details of a contract event filter that has been removed from this Eventlistener instance.
     *
     * @param filter the filter in question.
     */
    void broadcastEventFilterRemoved(ContractEventFilter filter);

    /**
     * Broadcasts the details of a transaction monitoring spec that has been added to this Eventlistener instance.
     *
     * @param spec the transaction monitoring spec in question.
     */
    void broadcastTransactionMonitorAdded(TransactionMonitoringSpec spec);

    /**
     * Broadcasts the details of a transaction monitoring spec that has been removed from this Eventlistener instance.
     *
     * @param spec the transaction monitoring spec in question.
     */
    void broadcastTransactionMonitorRemoved(TransactionMonitoringSpec spec);
}
