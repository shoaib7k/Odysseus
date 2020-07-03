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

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;


public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}	

	@Override
	public String getInitialWindowPerspectiveId() {
		return "de.uniol.inf.is.odysseus.rcp.perspectives.QueriesPerspective";
	}
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);

		IDE.registerAdapters();
		
		// inserted: register images for rendering explorer view
	    final String ICONS_PATH = "icons/full/";
	    final String PATH_OBJECT = ICONS_PATH + "obj16/";
	    Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);
	    declareWorkbenchImage(configurer, ideBundle,
	        IDE.SharedImages.IMG_OBJ_PROJECT, PATH_OBJECT + "prj_obj.gif", true);
	    declareWorkbenchImage(configurer, ideBundle,
	        IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT + "cprj_obj.gif", true);

	   BrokenIDEImagesLoader.redeclareImages(configurer);
	}
	
	private void declareWorkbenchImage(IWorkbenchConfigurer configurer_p,
	        Bundle ideBundle, String symbolicName, String path, boolean shared) {
	    URL url = ideBundle.getEntry(path);
	    ImageDescriptor desc = ImageDescriptor.createFromURL(url);
	    configurer_p.declareImage(symbolicName, desc, shared);
	}

	@Override
	public void postStartup() {
		super.postStartup();
		NavigatorHelper.reloadNavigator();

	}
	
	
}
