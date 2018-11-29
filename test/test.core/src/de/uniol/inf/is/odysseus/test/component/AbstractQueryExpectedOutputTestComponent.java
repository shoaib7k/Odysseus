/*******************************************************************************
 * Copyright 2015 The Odysseus Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.uniol.inf.is.odysseus.test.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniol.inf.is.odysseus.keyvalue.datatype.KeyValueObject;
import de.uniol.inf.is.odysseus.core.collection.Pair;
import de.uniol.inf.is.odysseus.core.collection.Tuple;
import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.core.metadata.ITimeInterval;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.physicaloperator.ISource;
import de.uniol.inf.is.odysseus.core.planmanagement.query.QueryState;
import de.uniol.inf.is.odysseus.core.server.planmanagement.query.IPhysicalQuery;
import de.uniol.inf.is.odysseus.test.StatusCode;
import de.uniol.inf.is.odysseus.test.context.ITestContext;
import de.uniol.inf.is.odysseus.test.set.ExpectedOutputTestSet;
import de.uniol.inf.is.odysseus.test.sinks.physicaloperator.AbstractCompareSink;
import de.uniol.inf.is.odysseus.test.sinks.physicaloperator.KeyValueTICompareSink;
import de.uniol.inf.is.odysseus.test.sinks.physicaloperator.TICompareSink;

/**
 *
 * @author Christian Kuka
 *
 * @param <T>
 * @param <S>
 */
public abstract class AbstractQueryExpectedOutputTestComponent<T extends ITestContext, S extends ExpectedOutputTestSet> extends AbstractQueryTestComponent<T, S> {
    protected static Logger LOG = LoggerFactory.getLogger(AbstractQueryExpectedOutputTestComponent.class);

	public AbstractQueryExpectedOutputTestComponent() {
		super(true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected StatusCode prepareQueries(Collection<Integer> ids, S set) {
		try {
			for (Integer queryId : ids) {
				IPhysicalQuery physicalQuery = executor.getExecutionPlan(session).getQueryById(queryId, session);
				if (physicalQuery.getState() == QueryState.RUNNING){
					executor.stopQuery(queryId, session);
					LOG.warn("Query manually stopped! Test component queries are not allowed to start!");
				}
				List<IPhysicalOperator> roots = new ArrayList<>();
				for (IPhysicalOperator operator : physicalQuery.getRoots()) {
					// TODO: this assumes same output for all sinks -> maybe
					// there are multiple sinks with different outputs
					List<Pair<String, String>> expected = set.getExpectedOutput();
					String dataHandler = set.getDataHandler();
					AbstractCompareSink sink;
					if(dataHandler.equalsIgnoreCase("KEYVALUEOBJECT") || dataHandler.equalsIgnoreCase("NESTEDKEYVALUEOBJECT")) {
						sink = new KeyValueTICompareSink<KeyValueObject<? extends ITimeInterval>>(expected, dataHandler);
					} else {
						sink = new TICompareSink<Tuple<ITimeInterval>>(expected, dataHandler);
					}
//					if(set.getName().equalsIgnoreCase("aggregate_time.qry")){
//						sink.setTracing(true);
//					}
					sink.addListener(this);
					roots.add(sink);
					ISource<? extends IStreamObject<? extends ITimeInterval>> oldSink = (ISource<? extends IStreamObject<? extends ITimeInterval>>) operator;
					oldSink.subscribeSink(sink, 0, 0, operator.getOutputSchema());
				}
				physicalQuery.initializePhysicalRoots(roots);
			}

        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return StatusCode.ERROR_EXCEPTION_DURING_TEST;
        }
		return StatusCode.OK;

	}
}
