/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 */
package org.modelexecution.fuml.nfr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.ResourceUsage;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Stereotype;
import org.modelexecution.fuml.convert.IConversionResult;
import org.modelexecution.fuml.nfr.internal.BasicResourceUsage;
import org.modelexecution.fuml.nfr.internal.CompoundResourceUsage;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityNodeExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.CallActionExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.Trace;

public class ResourceUsageAnalysis {

	private static final String RESOURCE_USAGE_QUALIFIED_NAME = "MARTE::MARTE_Foundations::GRM::ResourceUsage"; //$NON-NLS-1$

	private Trace trace;
	private IConversionResult mapping;
	private Collection<IResourceUsage> resourceUsages;

	public ResourceUsageAnalysis(Trace trace, IConversionResult mapping) {
		this.trace = trace;
		this.mapping = mapping;
		computeResourceUsages();
	}

	private void computeResourceUsages() {
		resourceUsages = new ArrayList<IResourceUsage>();
		ActivityExecution rootActivityExecution = getRootActivityExecution();
		resourceUsages.addAll(createResourceUsages(rootActivityExecution));
	}

	private ActivityExecution getRootActivityExecution() {
		for (ActivityExecution execution : trace.getActivityExecutions())
			if (execution.getCaller() == null)
				return execution;
		return null;
	}

	private Collection<IResourceUsage> createResourceUsages(
			ActivityExecution activityExecution) {
		Collection<IResourceUsage> usages = new ArrayList<IResourceUsage>();

		Collection<IResourceUsage> subUsages = createSubUsages(activityExecution);
		ResourceUsage rawResourceUsage = getRawResourceUsage(activityExecution);

		if (rawResourceUsage != null) {
			CompoundResourceUsage usage = new CompoundResourceUsage(
					rawResourceUsage, subUsages);
			usages.add(usage);
		} else {
			usages.addAll(subUsages);
		}

		return usages;
	}

	private ResourceUsage getRawResourceUsage(
			ActivityExecution activityExecution) {
		ResourceUsage rawResourceUsage = getRawResourceUsage(activityExecution
				.getActivity());
		if (rawResourceUsage == null && activityExecution.getCaller() != null) {
			CallActionExecution caller = activityExecution.getCaller();
			rawResourceUsage = getRawResourceUsage(caller.getNode());
		}
		return rawResourceUsage;
	}

	private Collection<IResourceUsage> createSubUsages(
			ActivityExecution activityExecution) {
		return createResourceUsages(activityExecution.getNodeExecutions());
	}

	private Collection<IResourceUsage> createResourceUsages(
			List<ActivityNodeExecution> nodeExecutions) {
		Collection<IResourceUsage> usages = new ArrayList<IResourceUsage>();
		for (ActivityNodeExecution nodeExecution : nodeExecutions) {
			usages.addAll(createResourceUsages(nodeExecution));
		}
		return usages;
	}

	private Collection<IResourceUsage> createResourceUsages(
			ActivityNodeExecution nodeExecution) {
		Collection<IResourceUsage> usages = new ArrayList<IResourceUsage>();
		ResourceUsage rawResourceUsage = getRawResourceUsage(nodeExecution
				.getNode());

		if (nodeExecution instanceof CallActionExecution) {
			CallActionExecution callActionExecution = (CallActionExecution) nodeExecution;
			ActivityExecution calledExecution = callActionExecution.getCallee();
			usages.addAll(createResourceUsages(calledExecution));
		} else {
			if (rawResourceUsage != null) {
				usages.add(new BasicResourceUsage(rawResourceUsage));
			}
		}

		return usages;
	}

	private ResourceUsage getRawResourceUsage(
			fUML.Syntax.Activities.IntermediateActivities.ActivityNode node) {
		ActivityNode umlActivityNode = (ActivityNode) mapping
				.getInputObject(node);
		return getRawResourceUsage(umlActivityNode);
	}

	private ResourceUsage getRawResourceUsage(
			fUML.Syntax.Activities.IntermediateActivities.Activity activity) {
		Activity umlActivity = (Activity) mapping.getInputObject(activity);
		return getRawResourceUsage(umlActivity);
	}

	private ResourceUsage getRawResourceUsage(Element umlElement) {
		Stereotype appliedStereotype = umlElement
				.getAppliedStereotype(RESOURCE_USAGE_QUALIFIED_NAME);
		return appliedStereotype != null ? (ResourceUsage) umlElement
				.getStereotypeApplication(appliedStereotype) : null;
	}

	public Collection<IResourceUsage> getResourceUsages() {
		return Collections.unmodifiableCollection(resourceUsages);
	}

}
