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

package net.fortress.eventlistener.chain.block.tx.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.fortress.eventlistener.dto.transaction.TransactionDetails;
import net.fortress.eventlistener.dto.transaction.TransactionStatus;

import java.util.List;

@Data
@AllArgsConstructor
public abstract class SingleValueMatchingCriteria<T> implements TransactionMatchingCriteria {

    private String nodeName;

    private T valueToMatch;

    private List<TransactionStatus> statuses;

    @Override
    public boolean isAMatch(TransactionDetails tx) {
        return valueToMatch.equals(getValueFromTx(tx));
    }

    protected abstract T getValueFromTx(TransactionDetails tx);
}
