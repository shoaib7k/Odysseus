/*******************************************************************************
 * Copyright 2012 The Odysseus Team
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
package de.uniol.inf.is.odysseus.test.sinks.physicaloperator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniol.inf.is.odysseus.keyvalue.datatype.KeyValueObject;
import de.uniol.inf.is.odysseus.core.collection.Pair;
import de.uniol.inf.is.odysseus.core.datahandler.DataHandlerRegistry;
import de.uniol.inf.is.odysseus.core.datahandler.IDataHandler;
import de.uniol.inf.is.odysseus.core.metadata.ITimeInterval;
import de.uniol.inf.is.odysseus.core.metadata.TimeInterval;
import de.uniol.inf.is.odysseus.core.physicaloperator.OpenFailedException;

/**
 * Sink, which compares calculated key value object with predefined json data. Used
 * for tests only. Use callbacks to inform listener if one calculated key value object
 * differs or all key value objects are correct.
 * 
 * @author Jan Soeren Schwarz
 *
 */
public class KeyValueTICompareSink<T extends KeyValueObject<? extends ITimeInterval>> extends AbstractCompareSink<T> {

	Logger logger = LoggerFactory.getLogger(KeyValueTICompareSink.class);
	
	public KeyValueTICompareSink(List<Pair<String, String>> expected, String dataHandler) {
		super(expected, dataHandler);
	}

	public KeyValueTICompareSink(KeyValueTICompareSink<T> s) {
		super(s);
	}

	@Override
	public KeyValueTICompareSink<T> clone() {
		return new KeyValueTICompareSink<T>(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void process_open() throws OpenFailedException {
		synchronized (expected) {
			this.expected.clear();
			this.inputdata.clear();
			IDataHandler<T> dh = (IDataHandler<T>) DataHandlerRegistry.instance.getDataHandler(this.dataHandler, getOutputSchema());
			for (Pair<String, String> json : expectedOriginals) {
				T kvo = dh.readData(json.getE1());
				TimeInterval ti = TimeInterval.parseTimeInterval(json.getE2().replace(';', '|'));
				((KeyValueObject)kvo).setMetadata(ti);
				this.expected.insert(kvo);
				if(tracing){
					System.out.println("load expected tuple: "+kvo);
				}
			}
			logger.debug("expected data loaded");
		}
	}

}
