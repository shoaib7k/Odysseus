package de.uniol.inf.is.odysseus.product.server.starter;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdysseusServerApplication implements IApplication {

	private static Logger logger = LoggerFactory.getLogger(OdysseusServerApplication.class);

	private enum StopRequest {
		NO, RESTART, EXIT
	};

	private StopRequest stopRequested = StopRequest.NO;

	@Override
	public Object start(IApplicationContext context) throws Exception {

		synchronized (OdysseusServerApplication.class) {
//			Activator.waitForExecutor();					
			registerEventHandler(Activator.getContext());
			logger.info("Odysseus application is fully started!");
			while (stopRequested == StopRequest.NO) {
				OdysseusServerApplication.class.wait(5000);
			}
		}
		
		if (stopRequested == StopRequest.RESTART) {
			logger.info("Restarting Odysseus...");
			return IApplication.EXIT_RESTART;
		} else {
			logger.info("Shuting Odysseus down!");
			return IApplication.EXIT_OK;
		}
	}

	@Override
	public void stop() {
		// nothing to do
	}

	public void registerEventHandler(BundleContext context) {

		String[] topics = new String[] { EventConstants.EVENT_TOPIC, "de/uniol/inf/odysseus/application/*" };
		Dictionary<String, Object> ht = new Hashtable<String, Object>();
		ht.put(EventConstants.EVENT_TOPIC, topics);

		// hier registrieren wir einen entsprechenden EventHandler
		context.registerService(EventHandler.class.getName(), new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				logger.debug("got odysseus application message: " + event);
				if (event.getProperty("TYPE").equals("RESTART")) {					
					stopRequested = StopRequest.RESTART;
				}
				if (event.getProperty("TYPE").equals("EXIT")) {					
					stopRequested = StopRequest.EXIT;
				}
			}
		}, ht);

	}

}
