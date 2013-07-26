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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.NamedElement;
import org.modelexecution.fuml.nfr.debug.NFRDebugPlugin;

public class EGModelSelectionTab extends UMLModelSelectionTab {	

	/* (non-Javadoc)
	 * @see org.modelexecution.fuml.nfr.debug.ui.launch.UMLModelSelectionTab#obtainSelectableModelElements(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	protected List<EObject> obtainSelectableModelElements(Resource umlResource) {
		List<EObject> obtainedActivities = new ArrayList<EObject>();
		TreeIterator<EObject> allContents = umlResource.getAllContents();
		while(allContents.hasNext()) {
			EObject eObject = allContents.next();
			if(eObject instanceof Activity) {
				obtainedActivities.add(eObject);
			}
		}
		return obtainedActivities;
	}
	
	/* (non-Javadoc)
	 * @see org.modelexecution.fuml.nfr.debug.ui.launch.UMLModelSelectionTab#loadModelElementFromSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	protected EObject loadModelElementFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			structuredSelection.size();
			for (Iterator<?> iter = structuredSelection.iterator(); iter
					.hasNext();) {
				Object next = iter.next();
				if (next instanceof Activity) {					
					return (Activity)next;
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.modelexecution.fuml.nfr.debug.ui.launch.UMLModelSelectionTab#initializeSelectedModelElement(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	protected NamedElement initializeSelectedModelElement(
			ILaunchConfiguration configuration) {
		String activityName = "";
		try {
			activityName = configuration.getAttribute(
					NFRDebugPlugin.ATT_EG_MAIN_ACTIVITY_NAME, "");
		} catch (CoreException e) {
		}
		
		for (EObject eObject : getSelectableModelElements()) {
			if (eObject instanceof Activity) {
				Activity activity = (Activity) eObject;
				if (activity.getQualifiedName().equals(activityName)) {
					return activity;
				}
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID,
				NFRDebugPlugin.EG_PROCESS_FACTORY_ID);
		configuration.setAttribute(NFRDebugPlugin.ATT_MODEL_PATH, uriText
				.getText().trim());
		configuration.setAttribute(NFRDebugPlugin.ATT_EG_MAIN_ACTIVITY_NAME,
				getMainActivityName());
		
	}

	private String getMainActivityName() {
		EObject selectedModelElement = getSelectedModelElement();
		if (selectedModelElement == null)
			return "";
		if (selectedModelElement instanceof Activity) {
			Activity activity = (Activity)selectedModelElement;
			return activity.getQualifiedName();
		}
		return "";
	}
	
	@Override
	protected String getModelElementLabelText() {
		return "Select a main activity";
	}
}
