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
import org.modelexecution.fuml.nfr.debug.internal.process.InternalNFRQNProcess;

public class NFRQNLaunchDelegate extends LaunchConfigurationDelegate {

	private static final String NFR_QN_EXEC_LABEL = "NFR QN Execution Process";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String modelPath = getModelPath(configuration);
		String resultPath = getResultPath(configuration);
		String analysisContext = getAnalysisContext(configuration);
		int simulationTime = getSimulationTime(configuration);

		InternalNFRQNProcess process = new InternalNFRQNProcess(modelPath,
				resultPath, analysisContext, simulationTime);

		DebugPlugin.newProcess(launch, process, NFR_QN_EXEC_LABEL);
	}

	private String getModelPath(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(NFRDebugPlugin.ATT_MODEL_PATH,
				(String) null);
	}

	private String getAnalysisContext(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(
				NFRDebugPlugin.ATT_QN_ANALYIS_CONTEXT, (String) null);
	}

	private String getResultPath(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(NFRDebugPlugin.ATT_QN_RESULT_PATH,
				(String) null);
	}

	private int getSimulationTime(ILaunchConfiguration configuration)
			throws CoreException {
		int simulationTime = 0;
		String simulationTimeString = configuration.getAttribute(
				NFRDebugPlugin.ATT_QN_SIMULATION_TIME, "");
		try {
			simulationTime = Integer.parseInt(simulationTimeString);
		} catch (NumberFormatException e) {			
		}
		return simulationTime;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return false;
	}

}