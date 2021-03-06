/********************************************************************************** 
  * Copyright 2011 The Odysseus Team
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
package de.uniol.inf.is.odysseus.product.studio.starter;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.misc.Policy;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.uniol.inf.is.odysseus.rcp.config.OdysseusRCPConfiguartionException;
import de.uniol.inf.is.odysseus.rcp.config.OdysseusRCPConfiguration;

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class OdysseusStudioApplication implements IApplication {

	private static final String RECENT_WORKSPACES_CONFIG_KEY = "recentWorkspaces";
	private static Logger LOG = LoggerFactory.getLogger(OdysseusStudioApplication.class);	
	private static final String DEBUG_SWT_SYS_PROPERTY = "debug.swt";
	
	private Appender appender;
	
	public OdysseusStudioApplication() {
		LoggerFactory.getLogger("Core");
	}
	
	@Override
	public synchronized Object start(IApplicationContext context) {

		Display display = createDisplay();
		
	    
		try {
			if( !chooseWorkspace(display) ) {
				return IApplication.EXIT_OK;
			}

			registerEventHandler(context.getBrandingBundle().getBundleContext());
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					addLoggerToConsole();			
				}
			});
				
			Integer val = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor()) 
					== PlatformUI.RETURN_RESTART ? IApplication.EXIT_RESTART : IApplication.EXIT_OK;		
			removeAppenderFromLoggers(appender);
			return val;
		} catch (Throwable t) {
			LOG.error("Exception during running application", t);
			return null;
		} finally {
			display.dispose();
		}
	}

	private static Display createDisplay() {
		String doDebug = System.getProperty(DEBUG_SWT_SYS_PROPERTY);
		if( doDebug != null && doDebug.equalsIgnoreCase("true")) {
			Policy.DEBUG_SWT_GRAPHICS = true;
			Display display = PlatformUI.createDisplay();
			Sleak sleak = new Sleak();
		    sleak.open();
		    return display;
		}
		
		return PlatformUI.createDisplay();
	}

	@Override
	public void stop() {
		removeAppenderFromLoggers(appender);
		//appender.stop();
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}	

	private static boolean chooseWorkspace(Display display) {
		try {
			File file = new File(System.getProperty("user.home"), "workspace");
			String path = file.getAbsolutePath().replace(File.separatorChar, '/');
			
			URL url = new URL("file", null, path); 
			ChooseWorkspaceData data = new ChooseWorkspaceData(url);
			
			List<String> recentWorkspaces = toStringList(data.getRecentWorkspaces());
			loadRecentOdysseusWorkspaces(recentWorkspaces);
			recentWorkspaces = cleanFromNullsAndDublicates(recentWorkspaces);
			
			if( !recentWorkspaces.isEmpty() ) {
				data.setRecentWorkspaces(recentWorkspaces.toArray(new String[0]));
			}
			
			ChooseWorkspaceDialogExtended dialog = new ChooseWorkspaceDialogExtended(display.getActiveShell(), data, false, true);
			dialog.prompt(false);
			
			if( data.getSelection() != null && !Platform.getInstanceLocation().isSet() ) {
				if( !releaseAndSetLocation(data.getSelection()) ) {
					
					dialog = new ChooseWorkspaceDialogExtended(display.getActiveShell(), data, false, true);
					dialog.prompt(true);
				}
			}
			
			if( dialog.getReturnCode() == Window.OK) { 
				saveNewSelectedRecentOdysseusWorkspace(recentWorkspaces, data.getSelection());
			}
			
			return dialog.getReturnCode() == Window.OK;
			
		} catch (Exception e) {
			LOG.error("Exception during choosing workspace", e);
			return false;
		}
	}

	private static void saveNewSelectedRecentOdysseusWorkspace(List<String> recentWorkspaces, String selection) {
		recentWorkspaces = Lists.newArrayList(recentWorkspaces); // to avoid side effects for caller
		if( recentWorkspaces.contains(selection)) {
			recentWorkspaces.remove(selection);
		}
		recentWorkspaces.add(0, selection); // to make sure that the last selection is on first place
		
		recentWorkspaces = cleanFromNullsAndDublicates(recentWorkspaces);
		OdysseusRCPConfiguration.set(RECENT_WORKSPACES_CONFIG_KEY, fromListToString(recentWorkspaces));
	}

	private static List<String> cleanFromNullsAndDublicates(List<String> recentWorkspaces) {
		List<String> cleanedList = Lists.newArrayList();
		for( String recentWorkspace : recentWorkspaces ) {
			if( !Strings.isNullOrEmpty(recentWorkspace) && !cleanedList.contains(recentWorkspace)) {
				cleanedList.add(recentWorkspace);
			}
		}
		return cleanedList;
	}

	private static void loadRecentOdysseusWorkspaces(List<String> recentWorkspaces) throws OdysseusRCPConfiguartionException {
		if (OdysseusRCPConfiguration.exists(RECENT_WORKSPACES_CONFIG_KEY)) {
			List<String> recentOdysseusWorkspaces = fromStringToList(OdysseusRCPConfiguration.get(RECENT_WORKSPACES_CONFIG_KEY));
			recentWorkspaces.addAll(recentOdysseusWorkspaces);
		} else {
			LOG.error("Odysseus RCP configuration does not contain key for recent workspaces");
		}
	}
	
	private static List<String> fromStringToList(String string) {
		String[] splitted = string.split(";");
		List<String> recentWorkspaces = Lists.newArrayList();
		for( String splitt : splitted ) {
			if( !Strings.isNullOrEmpty(splitt)) {
				recentWorkspaces.add(splitt);
			}
		}
		return recentWorkspaces;
	}

	private static String fromListToString(List<String> recentWorkspaces) {
		StringBuilder sb = new StringBuilder();
		for( String recentWorkspace : recentWorkspaces ) {
			if(!Strings.isNullOrEmpty(recentWorkspace)) {
				sb.append(recentWorkspace).append(";");
			}
		}
		return sb.toString();
	}

	private static List<String> toStringList(String[] recentWorkspaces) {
		List<String> list = Lists.newArrayList();
		
		for( String recentWorkspace : recentWorkspaces ) {
			if( !Strings.isNullOrEmpty(recentWorkspace)) {
				list.add(recentWorkspace);
			}
		}
		
		return list;
	}

	private static boolean releaseAndSetLocation(String selection) {
		try {
			Location instanceLoc = Platform.getInstanceLocation();
			if (instanceLoc.isSet()) {
				instanceLoc.release();
			}
	
			return instanceLoc.set(new URL("file", null, selection), true);
		} catch( Exception ex ) {
			LOG.error("Could not release and set location", ex);
			return false;
		}
	}	
	
	public void registerEventHandler(BundleContext context) {

		String[] topics = new String[] { EventConstants.EVENT_TOPIC, "de/uniol/inf/odysseus/application/*" };
		Dictionary<String, Object> ht = new Hashtable<String, Object>();
		ht.put(EventConstants.EVENT_TOPIC, topics);

		context.registerService(EventHandler.class.getName(), new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				LOG.debug("got odysseus application message: " + event);				
				if (event.getProperty("TYPE").equals("RESTART")) {	
					Display.getDefault().syncExec(new Runnable() {						
						@Override
						public void run() {
							PlatformUI.getWorkbench().restart();
						}
					});

				}
				if (event.getProperty("TYPE").equals("EXIT")) {					
					Display.getDefault().syncExec(new Runnable() {						
						@Override
						public void run() {
							PlatformUI.getWorkbench().close();
						}
					});
				}
			}
		}, ht);

	}
	
	private void addLoggerToConsole() {

		final String CONSOLE_NAME = "OdysseusConsole";

		MessageConsole myConsole = findConsole(CONSOLE_NAME);
		OutputStream outStream = myConsole.newMessageStream();
		String sysReDir = System.getProperty("sysredirect", "false");
		if (Boolean.parseBoolean(sysReDir)) {
			if (Boolean.parseBoolean(OdysseusRCPConfiguration.get("redirectSysOut", "true"))) {
				System.setOut(new PrintStream(outStream));
			}
			if (Boolean.parseBoolean(OdysseusRCPConfiguration.get("redirectSysErr", "true"))) {
				System.setErr(new PrintStream(outStream));
			}
		}
		PrintWriter writer = new PrintWriter(outStream);
		writer.write("Odysseus Console started ... adding log output \n");
		
		addAppender(outStream, CONSOLE_NAME);
	}

	void addAppender(final OutputStream outputStream, final String outputStreamName) {
		final LoggerContext context = LoggerContext.getContext(false);
		final Configuration config = context.getConfiguration();
		final PatternLayout layout = PatternLayout.createDefaultLayout(config);
		appender = OutputStreamAppender.createAppender(layout, null, outputStream, outputStreamName,
				false, true);
		appender.start();
		config.addAppender(appender);
		final Level level = null;
		final Filter filter = null;
		for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
			loggerConfig.addAppender(appender, level, filter);
		}
		config.getRootLogger().addAppender(appender, level, filter);
	}
	
	private void removeAppenderFromLoggers(final Appender appender) {
		final LoggerContext context = LoggerContext.getContext(false);
		final Configuration config = context.getConfiguration();
		config.getRootLogger().removeAppender(appender.getName());
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
