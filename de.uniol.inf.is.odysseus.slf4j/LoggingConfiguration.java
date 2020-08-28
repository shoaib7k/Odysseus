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
/** Copyright 2012 The Odysseus Team
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package de.uniol.inf.is.odysseus.slf4j;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Properties;
import java.util.logging.LogManager;

/**
 * 
 * @author Dennis Geesen
 * Created at: 22.03.2012
 */
public class LoggingConfiguration {

	
	public static void load(){
		loadJavaUtilLogging();
		System.setProperty("log4j2.debug","true");
		System.setProperty("log4j.configurationFile", "log4j.properties");
	}
	
	
	private static void loadJavaUtilLogging() {
		try {
			Properties loggingProperties = new Properties();
			// set log levels
			loggingProperties.put("java.util.logging.com.sun.xml.internal.ws.level", "SEVERE");
			
			// configure log manager
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream(pos);
			loggingProperties.store(pos, "");
			pos.close();
			LogManager.getLogManager().readConfiguration(pis);
			pis.close();
		} catch (Exception e) {

		}
	}
}
