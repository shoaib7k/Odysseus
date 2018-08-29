package de.uniol.inf.is.odysseus.product.studio.starter;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

/**
 * This class is needed for an eclipse bug to handle some lost RCP IDE images 
 * 
 * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=234252
 * 
 * @author DGeesen
 *
 */
@SuppressWarnings("restriction")
public class BrokenIDEImagesLoader {

	
	public static void redeclareImages(IWorkbenchConfigurer configurer){
		 final String ICONS_PATH = "icons/full/";
		    
			Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);

		    declareWorkbenchImage(
		            configurer, 
		            ideBundle,
		            IDE.SharedImages.IMG_OBJ_PROJECT, 
		            ICONS_PATH + "obj16/prj_obj.gif",
		            true);

		    declareWorkbenchImage(
		            configurer, 
		            ideBundle,
		            IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, 
		            ICONS_PATH + "obj16/cprj_obj.gif", 
		            true);

		    declareWorkbenchImage(
		            configurer, 
		            ideBundle, 
		            IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW, 
		            ICONS_PATH + "eview16/problems_view.gif", 
		            true);

		    declareWorkbenchImage(
		            configurer, 
		            ideBundle, 
		            IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_ERROR, 
		            ICONS_PATH + "eview16/problems_view_error.gif", 
		            true);


		    declareWorkbenchImage(
		            configurer, 
		            ideBundle, 
		            IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_WARNING, 
		            ICONS_PATH + "eview16/problems_view_warning.gif", 
		            true);

		    declareWorkbenchImage(
		            configurer, 
		            ideBundle, 
		            IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH, 
		            ICONS_PATH + "obj16/error_tsk.gif", 
		            true);

		    declareWorkbenchImage(
		            configurer, 
		            ideBundle, 
		            IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH, 
		            ICONS_PATH + "obj16/warn_tsk.gif", 
		            true);		  
	}
	
	private static void declareWorkbenchImage(IWorkbenchConfigurer configurer_p, Bundle ideBundle, String symbolicName, String path, boolean shared)  
	{
	    URL url = ideBundle.getEntry(path);
	    ImageDescriptor desc = ImageDescriptor.createFromURL(url);
	    configurer_p.declareImage(symbolicName, desc, shared);
	}
}
