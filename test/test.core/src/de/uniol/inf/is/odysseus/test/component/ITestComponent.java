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
package de.uniol.inf.is.odysseus.test.component;

import de.uniol.inf.is.odysseus.test.context.ITestContext;

/**
 * Interface for Classes with tests, which should be
 * executed during Odysseus Test. The Plugin test.runner
 * executes them, when they are registered over Declarative Services.
 * 
 * @author Timo Michelsen, Dennis Geesen, Christian Kuka
 *
 */
public interface ITestComponent<T extends ITestContext> {
	public void setupTest(T context);
	public TestReport runTest(T context);	
	public T createTestContext();
	public String getName();
	public boolean isActivated();
}
