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
package de.uniol.inf.is.odysseus.product.studio.starter;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
public class ChooseWorkspaceDialogExtended extends ChooseWorkspaceDialog {

	private static Logger LOG = LoggerFactory.getLogger(ChooseWorkspaceDialogExtended.class);
	private final ChooseWorkspaceData launchData;

	public ChooseWorkspaceDialogExtended(Shell parentShell, ChooseWorkspaceData launchData, boolean suppressAskAgain, boolean centerOnMonitor) {
		super(parentShell, launchData, suppressAskAgain, centerOnMonitor);

		this.launchData = launchData;
	}

	@Override
	protected void okPressed() {

		String workspaceSelection = TextProcessor.deprocess(getWorkspaceLocation());

		if (workspaceSelection != null) {
			launchData.workspaceSelected(workspaceSelection);
			launchData.writePersistedData();

			if (!releaseAndSetLocation(workspaceSelection)) {
				setErrorMessage("Could not set workspace " + workspaceSelection + ".\nPlease choose a different one.");
				return;
			}
		}

		setReturnCode(OK);
		close();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	private static boolean releaseAndSetLocation(String selection) {
		try {
			Location instanceLoc = Platform.getInstanceLocation();
			if (!instanceLoc.isSet()) {
				return instanceLoc.set(new URL("file", null, selection), true);
			} else {
				return true;
			}
		} catch (Exception ex) {
			LOG.error("Could not release and set location", ex);
			return false;
		}
	}
}
