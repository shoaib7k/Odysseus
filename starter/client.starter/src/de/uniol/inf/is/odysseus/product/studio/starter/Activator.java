package de.uniol.inf.is.odysseus.product.studio.starter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uniol.inf.is.odysseus.product.studio.starter"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static Logger logger = LoggerFactory.getLogger(Activator.class);

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				for (Bundle bundle : context.getBundles()) {
					boolean isFragment = bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;				
					
					if (bundle != context.getBundle() && !isFragment && bundle.getState() == Bundle.RESOLVED) {
					 
						// Just a test
						if (bundle.getSymbolicName().equals("org.eclipse.core.resources")) {
							continue;
						}
						
						logger.trace("Try to start bundle " + bundle.getSymbolicName()+" in Version "+bundle.getVersion());
						try {
							bundle.start();
						}catch (IllegalStateException e){
							if (!e.getMessage().startsWith("Workbench has not been created")){
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
