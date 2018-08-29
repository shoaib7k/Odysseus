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

import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(600, 600));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle("Odysseus Studio 2");
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(true);
		org.eclipse.ui.ide.IDE.registerAdapters();
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);		 
	}
	
	@Override
	public void postWindowCreate() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideActionSet("org.eclipse.ui.externaltools.ExternalToolsSet");
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideActionSet("org.eclipse.ui.edit.text.actionSet.navigation");
	}
	
}
