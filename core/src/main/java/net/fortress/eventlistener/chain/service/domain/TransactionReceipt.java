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

package net.fortress.eventlistener.chain.service.domain;

import java.math.BigInteger;
import java.util.List;

/**
 * An Ethereum transaction receipt.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
public interface TransactionReceipt {

    String getTransactionHash();

    BigInteger getTransactionIndex();

    String getBlockHash();

    BigInteger getBlockNumber();

    BigInteger getCumulativeGasUsed();

    BigInteger getGasUsed();

    String getContractAddress();

    String getRoot();

    String getFrom();

    String getTo();

    List<Log> getLogs();

    String getLogsBloom();

    String getStatus();
}
