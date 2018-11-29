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

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniol.inf.is.odysseus.core.planmanagement.executor.exception.PlanManagementException;
import de.uniol.inf.is.odysseus.core.server.planmanagement.executor.IServerExecutor;
import de.uniol.inf.is.odysseus.core.server.usermanagement.SessionManagement;
import de.uniol.inf.is.odysseus.core.server.usermanagement.UserManagementProvider;
import de.uniol.inf.is.odysseus.core.usermanagement.ISession;
import de.uniol.inf.is.odysseus.test.ExecutorProvider;
import de.uniol.inf.is.odysseus.test.StatusCode;
import de.uniol.inf.is.odysseus.test.context.BasicTestContext;
import de.uniol.inf.is.odysseus.test.context.ITestContext;
import de.uniol.inf.is.odysseus.test.set.ITestSet;

/**
 *
 * @author Christian Kuka
 *
 * @param <T>
 * @param <S>
 */
public abstract class AbstractTestComponent<T extends ITestContext, S extends ITestSet> implements ITestComponent<T> {
    protected static Logger LOG = LoggerFactory.getLogger(AbstractTestComponent.class);

	protected IServerExecutor executor;
	protected List<S> testsets;
	protected ISession session;

	@Override
	public void setupTest(T context) {
		executor = ExecutorProvider.getExeuctor();
		session = SessionManagement.instance.login(context.getUsername(), context.getPassword().getBytes(), UserManagementProvider.instance.getDefaultTenant());
		if (context.getDataRootPath() == null){
			LOG.error("Error in context "+context);
		}
		testsets = createTestSets(context);
	}

	public abstract List<S> createTestSets(T context);

	@Override
	@SuppressWarnings("unchecked")
	public T createTestContext() {
		return (T) createBasicTestContext();
	}

	public BasicTestContext createBasicTestContext() {
		BasicTestContext testcontext = new BasicTestContext();
		testcontext.setPassword("manager");
		testcontext.setUsername("System");
		URL bundleentry = FrameworkUtil.getBundle(getClass()).getEntry("/");

		URL rootPath;
		try {
			rootPath = FileLocator.toFileURL(bundleentry);
			testcontext.setDataRootPath(rootPath);
		} catch (IOException | NullPointerException e) {
			LOG.debug("Bundleentry was: " + bundleentry);
			e.printStackTrace();
		}
		return testcontext;
	}

	/**
	 * Taken from de.uniol.inf.is.odysseus.test.TestComponent
	 *
	 * @param executor
	 */
	private static void tryStartExecutor(IServerExecutor executor, ISession session) {
		try {
			LOG.debug("Starting executor...");

			executor.startExecution(session);
		} catch (PlanManagementException e1) {
			throw new RuntimeException(e1);
		}
	}


	/**
	 * Taken from de.uniol.inf.is.odysseus.test.TestComponent
	 *
	 * @param executor
	 */
	private static void tryStopExecutor(IServerExecutor executor, ISession session) {
		try {
			LOG.debug("Stopping executor");
			executor.stopExecution(session);
		} catch (PlanManagementException e) {
			LOG.error("Exception during stopping executor", e);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 */
	@Override
	public TestReport runTest(T context) {
		int i = 0;
		tryStartExecutor(executor, session);
		TestReport report = new TestReport(getName(), testsets.size());
		for (S set : testsets) {
		    report.set(i, set.getName(), set.getQuery());
			LOG.debug((new Date()) + "Running sub test " + (i+1) + " of " + testsets.size() + ": \"" + set.getName() + "\" ....");
			LOG.debug(set.getQuery());
			long start = System.nanoTime();
            StatusCode code = null;
            try {
                code = executeTestSet(set);
            }
            catch (Throwable e) {
                report.setError(i, e);
                e.printStackTrace();
                //throw e;
            }
            report.setDuration(i, System.nanoTime()-start);
            report.setStatusCode(i, code);
			LOG.debug("Sub test \"" + set.getName() + "\" ended with code: " + code);
			LOG.debug("***************************************************************************************");
			i++;
		}
		tryStopExecutor(executor, session);
		return report;
	}

	protected abstract StatusCode executeTestSet(S set);

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean isActivated() {
		return true;
	}
}
