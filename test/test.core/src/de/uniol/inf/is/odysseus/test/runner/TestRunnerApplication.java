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
package de.uniol.inf.is.odysseus.test.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import de.uniol.inf.is.odysseus.core.server.usermanagement.SessionManagement;
import de.uniol.inf.is.odysseus.core.server.usermanagement.UserManagementProvider;
import de.uniol.inf.is.odysseus.core.usermanagement.ISession;
import de.uniol.inf.is.odysseus.report.IReport;
import de.uniol.inf.is.odysseus.report.IReportGenerator;
import de.uniol.inf.is.odysseus.test.StatusCode;
import de.uniol.inf.is.odysseus.test.cheatsheet.CheatSheetGenerator;
import de.uniol.inf.is.odysseus.test.component.ITestComponent;
import de.uniol.inf.is.odysseus.test.component.TestReport;
import de.uniol.inf.is.odysseus.test.component.TestReport.Report;
import de.uniol.inf.is.odysseus.test.context.BasicTestContext;

/**
 * Application for running all TestComponents. Recieves all registered
 * {@code TestComponents} over Declarative Services and executes them.
 * 
 * @author Timo Michelsen, Dennis Geesen, Marco Grawunder
 * 
 */
public class TestRunnerApplication implements IApplication {

    private static List<ITestComponent<BasicTestContext>> components = Lists.newArrayList();
    private final Object lock = new Object();
    private long lastadded = System.currentTimeMillis();

    private static final Logger LOG = LoggerFactory.getLogger(TestRunnerApplication.class);

	private static IReportGenerator reportGenerator;
    
	// called by OSGi-DS
	public static void bindReportGenerator(IReportGenerator serv) {
		reportGenerator = serv;
	}

	// called by OSGi-DS
	public static void unbindReportGenerator(IReportGenerator serv) {
		if (reportGenerator == serv) {
			reportGenerator = null;
		}
	}
    
    @Override
    public Object start(IApplicationContext context) throws Exception {
        LOG.debug("Starting Odysseus...");
        //startBundles(context.getBrandingBundle().getBundleContext());
   
        LOG.debug("Odysseus is up and running!");
        
        if (System.getProperty("cheatsheet") != null || System.getenv("cheatsheet") != null) {
            String file = System.getProperty("cheatsheet");
            if (file == null){
            	file = System.getenv("cheatsheet");
            }
            CheatSheetGenerator.execute(file);
        }
        else {

            LOG.debug("Starting component tests...");

            List<TestReport> reports = executeComponents(context);
            LOG.debug("All tests were run.");
            boolean oneFailed = false;
            for (TestReport report : reports) {
                if (report.statusCode != StatusCode.OK) {
                    oneFailed = true;
                }
            }
            if (oneFailed) {
                printAdditionalInfos();
            }
            
            printReports(reports);

            if (oneFailed) {
                LOG.debug("At least one test failed!");
                
        		
                return -1;
            }
            LOG.debug("All tests finished with no errors.");
        }
        return IApplication.EXIT_OK;
    }

	private void printAdditionalInfos() {
		// print infos about current system state (e.g. bundles etc.)
		ISession session = SessionManagement.instance.login("System", "manager".getBytes(), UserManagementProvider.instance.getDefaultTenant());
		IReport rep = reportGenerator.generateReport(session);
		LOG.debug("##########################Extended Information ");
		for (String key: rep.getTitles()) {
			LOG.debug("++++"+key+"++++");
			Optional<String> repText = rep.getReportText(key);
			if (repText.isPresent()) {
				LOG.debug(repText.get());
			}
		}
	}

    private List<TestReport> executeComponents(IApplicationContext context) {
        boolean oneFailed = false;
        long current = System.currentTimeMillis();
        List<TestReport> reports = new ArrayList<>();
        while ((current - lastadded) < 10000) {
            synchronized (components) {
                for (ITestComponent<BasicTestContext> component : components) {
                    TestComponentRunner<BasicTestContext> runner = new TestComponentRunner<>(component);
                    LOG.debug("Starting a new component test: " + component.getName());
                    LOG.debug("#######################################################################################");
                    TestReport report = runner.run();
                    LOG.debug("Total results for component \"" + component.getName() + "\"");
                    int i = 1;
                    for (Report subReport : report.getReports()) {
                        if (subReport != null) {
                            LOG.debug("Sub test " + i + ": " + subReport.name);
                            if (subReport.statusCode != StatusCode.OK) {
                                oneFailed = true;
                                report.statusCode = StatusCode.FAILED;
                            }
                        }
                        i++;
                    }
                    reports.add(report);
                    if (oneFailed)
                        LOG.debug("#######################################################################################");
                }
                components.clear();
            }
            synchronized (lock) {
                LOG.debug("no components were added since " + (current - lastadded) + "ms. Waiting... " + Thread.currentThread().getName());
                try {
                    lock.wait(1000);
                    current = System.currentTimeMillis();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return reports;
    }

    @Override
    public void stop() {

    }

    public void addTestComponent(ITestComponent<BasicTestContext> component) {
        synchronized (components) {
            components.add(component);
            lastadded = System.currentTimeMillis();
        }
        synchronized (lock) {
            lock.notify();
        }

    }

    public void removeTestComponent(ITestComponent<BasicTestContext> component) {
        components.remove(component);
    }

//    private void startBundles(final BundleContext context) {
//    	Thread runner = new Thread() {
//			
//			@Override
//			public void run() {
//		        for (Bundle bundle : context.getBundles()) {
//		            boolean isFragment = bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
//		            if (bundle != context.getBundle() && !isFragment && bundle.getState() == Bundle.RESOLVED) {
//		                try {
//	                        bundle.start();
//		                }
//		                catch (BundleException e) {
//		                    e.printStackTrace();
//		                }
//		            }
//
//		        }
//				
//			}
//		};
//		runner.start();
//
//    }

    /**
     * @param reports
     *            List of test reports
     */
    private void printReports(Collection<TestReport> reports) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%7s %62s %9s %s\n", "Status", "Component", "Time", "Error"));
        for (int i = 0; i < 86; i++) {
            sb.append("-");
        }
        sb.append("\n");
        boolean failed = false;
        long time = 0l;
        for (TestReport report : reports) {
            if (report != null) {
                sb.append(report.toString());
                time += report.time;
                if (report.statusCode != StatusCode.OK) {
                    failed = true;
                }
            }
        }
        for (int i = 0; i < 86; i++) {
            sb.append("-");
        }
        sb.append("\n");
        if (failed) {
            sb.append(String.format("%7s %62s %7dms %s\n", StatusCode.FAILED, "", time / 1000000, ""));
        }
        else {
            sb.append(String.format("%7s %62s %7dms %s\n", StatusCode.OK, "", time / 1000000, ""));
        }
        LOG.debug(sb.toString());
    }

}
