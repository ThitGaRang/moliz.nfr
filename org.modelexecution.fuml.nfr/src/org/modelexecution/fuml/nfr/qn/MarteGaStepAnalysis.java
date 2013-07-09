package org.modelexecution.fuml.nfr.qn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaStep;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.Resource;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.ResourceUsage;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.modelexecution.fuml.convert.IConversionResult;
import org.modelexecution.fuml.nfr.CompoundResourceUsage;
import org.modelexecution.fuml.nfr.IResourceUsage;
import org.modelexecution.fuml.nfr.internal.BasicResourceUsage;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityNodeExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.CallActionExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.Trace;

import fUML.Syntax.Actions.BasicActions.CallOperationAction;

public class MarteGaStepAnalysis {
	private Trace trace;
	private IConversionResult mapping;
	private Collection<IResourceUsage> resourceUsages;

	public MarteGaStepAnalysis(Trace trace, IConversionResult mapping) {
		this.trace = trace;
		this.mapping = mapping;
		computeResourceUsages();
	}

	private void computeResourceUsages() {
		resourceUsages = new ArrayList<IResourceUsage>();
		ActivityExecution rootActivityExecution = getRootActivityExecution();
		if(rootActivityExecution != null)
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
		NamedElement activity = getMappedElement(activityExecution.getActivity(), NamedElement.class);
		
		CompoundResourceUsage usage = new CompoundResourceUsage(activity,
				rawResourceUsage, subUsages);
		usages.add(usage);
		return usages;
	}

	private Collection<IResourceUsage> createSubUsages(
			ActivityExecution activityExecution) {
		return createResourceUsages(activityExecution.getNodeExecutions());
	}

	private Collection<IResourceUsage> createResourceUsages(
			List<ActivityNodeExecution> nodeExecutions) {
		Collection<IResourceUsage> usages = new ArrayList<IResourceUsage>();
		for (ActivityNodeExecution nodeExecution : nodeExecutions) {
			usages.add(createResourceUsage(nodeExecution));
		}
		return usages;
	}

	private IResourceUsage createResourceUsage(
			ActivityNodeExecution nodeExecution) {
		ResourceUsage rawResourceUsage = getRawResourceUsage(nodeExecution
				.getNode());
		NamedElement namedElement = getMappedElement(nodeExecution.getNode(), NamedElement.class);
		IResourceUsage resourceUsage = null;
		
		if (nodeExecution.getNode() instanceof CallOperationAction) {
			CallOperationAction callOperationAction = (CallOperationAction)nodeExecution.getNode();
			if(callOperationAction.operation != null) {
				System.out.println("Called Operation: " + callOperationAction.operation.qualifiedName + " - " + 
						MarteUtil.getFirstStereotype(getMappedElement(callOperationAction.operation, Operation.class), GaStep.class) + " - " +
						MarteUtil.getFirstStereotype(getMappedElement(callOperationAction, org.eclipse.uml2.uml.CallOperationAction.class), GaStep.class));
				rawResourceUsage = getRawResourceUsage(callOperationAction);
				resourceUsage = new BasicResourceUsage(namedElement, rawResourceUsage);
			}
		}
		
		if (nodeExecution instanceof CallActionExecution) {
			CallActionExecution callActionExecution = (CallActionExecution) nodeExecution;
			ActivityExecution calledExecution = callActionExecution.getCallee();
			if(calledExecution != null) {
				Collection<IResourceUsage> subUsages = createResourceUsages(calledExecution);
				resourceUsage = new CompoundResourceUsage(namedElement,
						rawResourceUsage, subUsages);
			} else {
				resourceUsage = new BasicResourceUsage(namedElement,
						rawResourceUsage);
			}
		} else {
			resourceUsage = new BasicResourceUsage(namedElement,
					rawResourceUsage);
		}
		
		return resourceUsage;
	}
	
	private <T extends Element> T getMappedElement(fUML.Syntax.Classes.Kernel.Element element, Class<T> clazz) {
		return clazz.cast(mapping.getInputObject(element));
	}

	private ResourceUsage getRawResourceUsage(
			ActivityExecution activityExecution) {
		ResourceUsage rawResourceUsage = getRawResourceUsage(activityExecution
				.getActivity());		
		if(rawResourceUsage == null && activityExecution.getCaller() != null) {
			rawResourceUsage = getResourceUsageOfCallingCallAction(activityExecution);
		}

		return rawResourceUsage;
	}
	
	private ResourceUsage getResourceUsageOfSpecification(ActivityExecution activityExecution) {
		return getRawGaStep(getMappedElement(activityExecution.getActivity(), Activity.class).getSpecification());
	}
	
	private ResourceUsage getResourceUsageOfOperation(CallOperationAction callOperationAction) {
		return getRawGaStep(getMappedElement(callOperationAction.operation, Operation.class));
	}

	private ResourceUsage getResourceUsageOfCallingCallAction(
			ActivityExecution activityExecution) {
		ResourceUsage rawResourceUsage;
		CallActionExecution caller = activityExecution.getCaller();
		rawResourceUsage = getRawResourceUsage(caller.getNode());
		return rawResourceUsage;
	}

	private GaStep getRawResourceUsage(
			fUML.Syntax.Activities.IntermediateActivities.ActivityNode node) {
		return getRawGaStep(getMappedElement(node, NamedElement.class));
	}

	private GaStep getRawResourceUsage(
			fUML.Syntax.Activities.IntermediateActivities.Activity activity) {
		return getRawGaStep(getMappedElement(activity, NamedElement.class));
	}

	private GaStep getRawGaStep(Element umlElement) {
		return MarteUtil.getFirstStereotype(umlElement, GaStep.class);
	}

	public Collection<IResourceUsage> getResourceUsages() {
		return Collections.unmodifiableCollection(resourceUsages);
	}
	
	public static void debugPrint(IResourceUsage usage, String prefix) {
		if(usage.getUsedResources().size() > 0) {
		System.out.println(prefix + " " + usage.getElement().getQualifiedName());
		for (Resource resource : usage.getUsedResources()) {
//			System.out.println(prefix + "  " + resource.getBase_Classifier().getName());
//			System.out.println(prefix + "    Allocated Memory=" + usage.getAllocatedMemory(resource));
//			System.out.println(prefix + "    Energy=" + usage.getEnergy(resource));
			System.out.println(prefix + " - ExecTime=" + usage.getExecTime(resource));
//			System.out.println(prefix + "    MsgSize=" + usage.getMsgSize(resource));
//			System.out.println(prefix + "    PowerPeak=" + usage.getPowerPeak(resource));
//			System.out.println(prefix + "    UsedMemory=" + usage.getUsedMemory(resource));
		}
		}
		debugPrintChildren(usage, prefix);
	}

	public static void debugPrintChildren(IResourceUsage usage, String prefix) {
		if (usage instanceof CompoundResourceUsage) {
			CompoundResourceUsage compoundResourceUsage = (CompoundResourceUsage) usage;
			for (IResourceUsage subUsage : compoundResourceUsage.getSubUsages()) {
				debugPrint(subUsage, prefix + prefix);
			}
		}
	}
}
