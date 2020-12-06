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

package net.fortress.eventlistener.dto.event;

/**
 * The status of a contract event being broadcast from Eventlistener.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public enum ContractEventStatus {
    //The transaction that triggered the event has been mined
    UNCONFIRMED,

    //The configured number of blocks since the event transaction has been reached
    //without a fork in the chain.
    CONFIRMED,

    //The chain has been forked and the event is no longer valid
    INVALIDATED;
}
