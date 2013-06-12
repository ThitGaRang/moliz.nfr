/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 * Tanja Mayerhofer - implementation
 */
package org.modelexecution.fuml.nfr.debug.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.modelexecution.fuml.nfr.debug.NFRDebugPlugin;
import org.modelexecution.fuml.nfr.debug.internal.process.InternalNFRProcess;

public class NFRLaunchDelegate extends LaunchConfigurationDelegate {

	private static final String NFR_EXEC_LABEL = "NFR Execution Process";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String modelPath = getModelPath(configuration);
		String mainActivityName = getMainActivityName(configuration);

		InternalNFRProcess nfrProcess = new InternalNFRProcess(modelPath,
				mainActivityName);

		DebugPlugin.newProcess(launch, nfrProcess, NFR_EXEC_LABEL);
	}

	private String getModelPath(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(NFRDebugPlugin.ATT_MODEL_PATH,
				(String) null);
	}

	private String getMainActivityName(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(
				NFRDebugPlugin.ATT_MAIN_ACTIVITY_NAME, (String) null);
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return false;
	}

}