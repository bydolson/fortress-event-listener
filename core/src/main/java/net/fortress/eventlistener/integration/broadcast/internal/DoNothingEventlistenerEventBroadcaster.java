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
 * A dummy broadcaster that does nothing.
 *
 * (Used in single instance mode)
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public class DoNothingEventlistenerEventBroadcaster implements EventlistenerEventBroadcaster {

    @Override
    public void broadcastEventFilterAdded(ContractEventFilter filter) {
        //DO NOTHING!
    }

    @Override
    public void broadcastEventFilterRemoved(ContractEventFilter filter) {
        //DO NOTHING!
    }

    @Override
    public void broadcastTransactionMonitorAdded(TransactionMonitoringSpec spec) {
        //DO NOTHING!
    }

    @Override
    public void broadcastTransactionMonitorRemoved(TransactionMonitoringSpec spec) {
        //DO NOTHING
    }
}
