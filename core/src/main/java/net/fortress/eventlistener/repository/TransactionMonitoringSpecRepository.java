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

package net.fortress.eventlistener.repository;

import net.fortress.eventlistener.dto.event.filter.ContractEventFilter;
import net.fortress.eventlistener.factory.ContractEventFilterRepositoryFactory;
import net.fortress.eventlistener.model.TransactionMonitoringSpec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring repository for storing active TransactionMonitoringSpec(s) in DB.
 *
 * @author Craig Williams <craig.williams@fortress.net>
 */
@Repository
@ConditionalOnMissingBean(ContractEventFilterRepositoryFactory.class)
public interface TransactionMonitoringSpecRepository extends CrudRepository<TransactionMonitoringSpec, String> {
}
