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
package org.modelexecution.fuml.nfr.debug.ui.launch;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.modelexecution.fuml.nfr.debug.NFRDebugPlugin;

public class AnalysisTab extends AbstractLaunchConfigurationTab {

	protected Button browseWorkspaceButton;
	protected Text uriText;
	protected Text simulationTimeText;

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH
				| GridData.GRAB_VERTICAL));

		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 8;
		composite.setLayout(layout);

		createURIControls(composite);
		setControl(composite);
	}

	protected void createURIControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData (new GridData(SWT.FILL, SWT.FILL, true, false));
		composite.setLayout(new GridLayout(3, false));
		
		Label uriLabel = new Label(composite, SWT.LEFT);
		uriLabel.setText("Output directory:");
		uriLabel.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT));
		
		uriText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		uriText.setEnabled(false);
		setURIText("");
		uriText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		
		browseWorkspaceButton = new Button(composite, SWT.NONE);
		browseWorkspaceButton.setText("Browse Workspace");		
		browseWorkspaceButton.setFocus();		
		
		Label simulationTimeLabel = new Label(composite, SWT.LEFT);
		simulationTimeLabel.setText("Simulation time:");
		simulationTimeLabel.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT));
		
		simulationTimeText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		simulationTimeText.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, true, false));	
		
		uriText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				uriTextModified(uriText.getText().trim());
			}
		});
		
		simulationTimeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				simulationTimeTextModified(simulationTimeText.getText().trim());
			}
		});
		
		browseWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseWorkspace();
			}
		});
	}

	private void uriTextModified(String text) {
		setErrorMessage(null);
		setMessage(null);
		updateLaunchConfigurationDialog();
	}
	
	private void simulationTimeTextModified(String text) {
		setErrorMessage(null);		
		setMessage(null);
		updateLaunchConfigurationDialog();
	}

	private boolean browseWorkspace() {
		IContainer[] folderContainer = WorkspaceResourceDialog.openFolderSelection(getShell(),
				"Select directory", "Select a directory for saving analysis results",
				false, null, null);

		if (folderContainer != null && folderContainer[0] != null) {
			StringBuffer text = new StringBuffer();
			text.append(URI.createPlatformResourceURI(folderContainer[0].getFullPath()
					.toString(), true));
			text.append("  ");
			setURIText(URI.decode(text.toString()));
			return true;
		}
		return false;
	}

	private void setURIText(String uri) {
		uri = uri.trim();
		StringBuffer text = new StringBuffer(uriText.getText());
		if (!uri.equals(text)) {
			uriText.setText(uri.trim());
		}
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (!haveSimulationTime()) {
			setErrorMessage("Provide a simulation time.");
			return false;
		} else if (!haveOutputResource()) {
			setErrorMessage("Selected a output resource.");
			return false;
		} else {
			setErrorMessage(null);
			setMessage(null);
			return super.isValid(launchConfig);
		}
	}

	private boolean haveOutputResource() {
		return !uriText.getText().isEmpty();
	}

	private boolean haveSimulationTime() {
		try {
			int simulationTime = Integer.parseInt(simulationTimeText.getText());
			if (simulationTime > 0)
				return true;
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(NFRDebugPlugin.ATT_QN_RESULT_PATH, uriText
				.getText().trim());
		configuration.setAttribute(NFRDebugPlugin.ATT_QN_SIMULATION_TIME,
				""+getSimulationTime());
	}

	/**
	 * @return
	 */
	private int getSimulationTime() {
		int simulationTime = 0;
		try {
			simulationTime = Integer.parseInt(simulationTimeText.getText());
		} catch (NumberFormatException e) {
		}
		return simulationTime;
	}

	@Override
	public String getName() {
		return "Analysis Parameters";
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(NFRDebugPlugin.ATT_QN_SIMULATION_TIME, "1000");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String outputResource = "";
		String simulationTime = "";

		try {
			outputResource = configuration.getAttribute(
					NFRDebugPlugin.ATT_QN_RESULT_PATH, "");
			simulationTime = configuration.getAttribute(
					NFRDebugPlugin.ATT_QN_SIMULATION_TIME, "");
		} catch (CoreException e) {
		}
		uriText.setText(outputResource);
		simulationTimeText.setText(simulationTime);
	}	

}
