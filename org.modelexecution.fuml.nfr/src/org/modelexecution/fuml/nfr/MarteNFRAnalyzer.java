/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 */
package org.modelexecution.fuml.nfr;

import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.modelexecution.fumldebug.core.ExecutionEventListener;
import org.modelexecution.fumldebug.core.event.ActivityExitEvent;
import org.modelexecution.fumldebug.core.event.ActivityNodeExitEvent;
import org.modelexecution.fumldebug.core.event.Event;
import org.modelexecution.fumldebug.papyrus.PapyrusModelExecutor;

public class MarteNFRAnalyzer implements ExecutionEventListener {

	private PapyrusModelExecutor executor;

	public MarteNFRAnalyzer(String modelPath) {
		executor = createPapyrusModelExecutor(modelPath);
	}

	private PapyrusModelExecutor createPapyrusModelExecutor(String modelPath) {
		return new PapyrusModelExecutor(modelPath);
	}

	public void runAnalysis(String activityName) {
		reset();
		executor.addEventListener(this);
		executor.executeActivity(activityName);
		executor.removeEventListener(this);
	}

	private void reset() {
		// TODO needed?
	}

	@Override
	public void notify(Event event) {
		if (event instanceof ActivityExitEvent) {
			ActivityExitEvent activityExitEvent = (ActivityExitEvent) event;
			Activity activity = getActivity(activityExitEvent.getActivity());
			processActivity(activity);
		} else if (event instanceof ActivityNodeExitEvent) {
			ActivityNodeExitEvent activityNodeExitEvent = (ActivityNodeExitEvent) event;
			ActivityNode node = getActivityNode(activityNodeExitEvent.getNode());
			processActivityNode(node);
		}
	}

	private ActivityNode getActivityNode(
			fUML.Syntax.Activities.IntermediateActivities.ActivityNode node) {
		return (ActivityNode) executor.getConversionResult().getInputObject(
				node);
	}

	private Activity getActivity(
			fUML.Syntax.Activities.IntermediateActivities.Activity activity) {
		return (Activity) executor.getConversionResult().getInputObject(
				activity);
	}

	private void processActivity(Activity activity) {
		// TODO Auto-generated method stub

	}

	private void processActivityNode(ActivityNode node) {
		for (Stereotype stereotype : node.getAppliedStereotypes()) {
			for (Property property : stereotype.getAllAttributes()) {
				System.out.println(node.getName() + "@" + stereotype.getName()
						+ "/" + property.getName() + ": "
						+ node.getValue(stereotype, property.getName()));
			}
		}
	}

}
