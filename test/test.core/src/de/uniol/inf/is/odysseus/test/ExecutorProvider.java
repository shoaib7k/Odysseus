package de.uniol.inf.is.odysseus.test;

import org.slf4j.LoggerFactory;

import de.uniol.inf.is.odysseus.core.planmanagement.executor.IExecutor;
import de.uniol.inf.is.odysseus.core.server.planmanagement.executor.IServerExecutor;

public class ExecutorProvider {

	private static IServerExecutor executor;

	public static IServerExecutor getExeuctor() {
		synchronized (ExecutorProvider.class) {
			while (executor == null) {
				LoggerFactory.getLogger(ExecutorProvider.class).debug("No executor yet, waiting for it...");
				try {
					ExecutorProvider.class.wait(1000);
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}
			}
		}
		return executor;
	}

	public void bindExecutor(IExecutor ex) {
		synchronized (ExecutorProvider.class) {
			executor = (IServerExecutor) ex;
			ExecutorProvider.class.notifyAll();
		}
		
	}

	public void unbindExecutor(IExecutor ex) {
		executor = null;
	}
}
