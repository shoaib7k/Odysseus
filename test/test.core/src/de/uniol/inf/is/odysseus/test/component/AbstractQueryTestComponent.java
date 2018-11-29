package de.uniol.inf.is.odysseus.test.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uniol.inf.is.odysseus.core.collection.Context;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.planmanagement.SinkInformation;
import de.uniol.inf.is.odysseus.core.planmanagement.ViewInformation;
import de.uniol.inf.is.odysseus.core.planmanagement.executor.exception.PlanManagementException;
import de.uniol.inf.is.odysseus.core.util.SimplePlanPrinter;
import de.uniol.inf.is.odysseus.test.StatusCode;
import de.uniol.inf.is.odysseus.test.context.ITestContext;
import de.uniol.inf.is.odysseus.test.set.QueryTestSet;
import de.uniol.inf.is.odysseus.test.sinks.physicaloperator.AbstractCompareSink;
import de.uniol.inf.is.odysseus.test.sinks.physicaloperator.ICompareSinkListener;

public abstract class AbstractQueryTestComponent<T extends ITestContext, S extends QueryTestSet>
		extends AbstractTestComponent<T, S> implements ICompareSinkListener {

	private static final long PROCESSING_WAIT_TIME = 1000;
	// after 10 Minutes: abort!
	private static final long ABORT_PROCESSING_AFTER = 1000 * 60 * 10;

	private StatusCode processingResult = null;

	private boolean waitforprocessing = true;
	private long startedAt;

	public AbstractQueryTestComponent() {
		this(true);
	}

	public AbstractQueryTestComponent(boolean waitForProcessing) {
		this.waitforprocessing = waitForProcessing;
	}

	@Override
	protected StatusCode executeTestSet(S set) {
		Collection<Integer> ids = new ArrayList<>();
		try {
			LOG.debug("starting component test...");
			LOG.debug("adding query...");
			ids = executor.addQuery(set.getQuery(), "OdysseusScript", session, Context.empty());
		} catch (PlanManagementException e) {
			e.printStackTrace();
			return StatusCode.ERROR_QUERY_NOT_INSTALLABLE;
		}
		try {
			LOG.debug("adding " + ids.size() + " queries done.");
			StatusCode result = prepareQueries(ids, set);
			if (result == StatusCode.OK && waitforprocessing) {
				for (int id : ids) {
					LOG.debug("starting query with ID " + id + "...");
					executor.startQuery(id, session);
				}

				processingResult = null;
				LOG.debug("query started, waiting until data is processed...");
				result = waitProcessing();
				if (result == StatusCode.ERROR_DEADLOCK_POSSIBLE) {
					for (int id : ids) {
						SimplePlanPrinter<IPhysicalOperator> planPrinter = new SimplePlanPrinter<IPhysicalOperator>(
								true);
						for (IPhysicalOperator op : executor.getExecutionPlan(session).getQueryById(id, session)
								.getRoots()) {
							String output = planPrinter.createString(op);
							LOG.debug(output);
						}
					}
				}
				LOG.debug("processing done.");
				// Wait one second ... currently some deadlock problems
				Thread.sleep(1000);
			} else {
				processingResult = result;
			}
			LOG.debug("result: " + result);
			LOG.debug("sub test done.");
		} catch (PlanManagementException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			LOG.debug("Try to remove all queries ");
			Thread t = new Thread() {
				@Override
				public void run() {
					executor.removeAllQueries(session);

					List<ViewInformation> views = executor.getStreamsAndViewsInformation(session);
					for (ViewInformation vi : views) {
						try {
							executor.removeViewOrStream(vi.getName(), session);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					List<SinkInformation> sinks = executor.getSinks(session);
					for (SinkInformation si:sinks) {
						try {
							executor.removeSink(si.getName(), session);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						
					}
					
				};
			};
			t.start();
			try {
				t.join(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not remove all queries in time");
			}

			LOG.debug("done");
		}
		return this.processingResult;

	}

	protected StatusCode prepareQueries(Collection<Integer> ids, S set) {
		return StatusCode.OK;
	}

	@Override
	public abstract List<S> createTestSets(T context);

	protected StatusCode waitProcessing() throws InterruptedException {
		startedAt = System.currentTimeMillis();
		synchronized (this) {
			while (processingResult == null) {
				this.wait(PROCESSING_WAIT_TIME);
				if (System.currentTimeMillis() - startedAt > ABORT_PROCESSING_AFTER) {
					this.processingResult = StatusCode.ERROR_DEADLOCK_POSSIBLE;
				}
			}
			return processingResult;
		}
	}

	@Override
	public void compareSinkProcessingDone(AbstractCompareSink<?> sink, boolean done, StatusCode result) {
		synchronized (this) {
			if (this.processingResult == null) {
				this.processingResult = result;
			}
		}
	}

}
