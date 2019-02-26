package de.uniol.inf.is.odysseus.product.server.starter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}


//	private static Object executor;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		//startBundles(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
//	private static void startBundles(final BundleContext context) {
//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//				for (Bundle bundle : context.getBundles()) {
//					boolean isFragment = bundle.getHeaders().get(
//							Constants.FRAGMENT_HOST) != null;
//					if (bundle != context.getBundle() && !isFragment
//							&& bundle.getState() == Bundle.RESOLVED) {
//						try {
//							bundle.start();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		});
//		t.start();
//	}

	
//	public void bindExecutor(Object ex){
//		synchronized (Activator.class) {
//			executor = ex;			
//			Activator.class.notifyAll();
//		}
//
//	}
//	public static void waitForExecutor() throws InterruptedException {
//		synchronized (Activator.class) {
//			while (executor == null) {
//				Activator.class.wait(1000);
//			}
//		}
//	}
}
