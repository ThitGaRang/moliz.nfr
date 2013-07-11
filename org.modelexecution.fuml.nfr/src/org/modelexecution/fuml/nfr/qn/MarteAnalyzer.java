package org.modelexecution.fuml.nfr.qn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaAnalysisContext;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaResourcesPlatform;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaScenario;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaStep;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaWorkloadBehavior;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaWorkloadEvent;
import org.eclipse.papyrus.MARTE.MARTE_DesignModel.HLAM.RtUnit;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.Resource;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityNodeExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.CallActionExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.Trace;
import org.modelexecution.fumldebug.debugger.uml.UMLModelExecutor;
import org.modelexecution.fumldebug.debugger.uml.UMLModelLoader;

public class MarteAnalyzer {	
	private UMLModelLoader loader;
	private GaAnalysisContext context;
	private String modelPath;
	private Set<MarteService> services;
	private List<GaWorkloadEvent> workloadEvents;
	
	public MarteAnalyzer(String modelPath) {
		setModel(modelPath);
		loader = new UMLModelLoader().setModel(modelPath);
	}
	
	public MarteAnalyzer setModel(String modelPath) {
		this.modelPath = modelPath;
		return this;
	}		
	
	public MarteAnalyzer setAnalysisContext(String analysisContextQN) {
		return setAnalysisContext(extractAnalysisContext(analysisContextQN));
	}
	
	public MarteAnalyzer setAnalysisContext(GaAnalysisContext context) {
		this.context = context;
		return this;
	}
	

	private GaAnalysisContext getAnalysisContext() {
		return context;
	}
	
	private MarteService getServiceFrom(Resource resource) {
		RtUnit rtUnit = MarteUtil.getFirstStereotype(resource.getBase_Classifier(), RtUnit.class); 
		if(rtUnit != null)
			return getServiceFrom(rtUnit);
		
		return new MarteService()
			.setMultiplicity(Integer.parseInt(resource.getResMult()))
			.setUmlElement(resource.getBase_Classifier());
	}
	
	private MarteService getServiceFrom(RtUnit rtUnit) {
		if(rtUnit == null)
			return null;
		
		return new MarteService()
			.setUmlElement(rtUnit.getBase_BehavioredClassifier())
			.setMultiplicity(rtUnit.getSrPoolSize())
			.setSchedulingPolicy(rtUnit.getQueueSchedPolicy());
	}
	
	private Set<MarteService> extractServices(GaAnalysisContext context) {
		Set<MarteService> resources = new HashSet<MarteService>();
		for(GaResourcesPlatform platform : context.getPlatform())
			for(Resource resource : platform.getResources()) 
				resources.add(getServiceFrom(resource));
				
		return resources;
	}
	
	private List<GaWorkloadEvent> extractWorkloadEvents(GaAnalysisContext context) {
		List<GaWorkloadEvent> workloadEvents = new ArrayList<GaWorkloadEvent>();
		for(GaWorkloadBehavior workload : context.getWorkload())
			workloadEvents.addAll(workload.getDemand());
		return workloadEvents;
	}
	
	private Set<MarteService> extractServices(NamedElement element, Set<MarteService> services) {
		if(element == null)
			return services;
		
		RtUnit rtUnit = MarteUtil.getFirstStereotype(element, RtUnit.class);
		if(rtUnit != null)
			services.add(getServiceFrom(rtUnit));
		
		for(Element child : element.getOwnedElements())
			if(child instanceof NamedElement)
				extractServices((NamedElement) child, services);
		
		return services;
	}
	
	private List<GaWorkloadEvent> extractWorkloadEvents(Element element, List<GaWorkloadEvent> workloadEvents) {
		if(element == null)
			return workloadEvents;
		
		GaWorkloadEvent event = MarteUtil.getFirstStereotype(element, GaWorkloadEvent.class);
		if(event != null)
			workloadEvents.add(event);
		
		for(Element child : element.getOwnedElements())
			extractWorkloadEvents(child, workloadEvents);
		
		return workloadEvents;
	}

	private List<GaWorkloadEvent> getWorkloadEvents() {
		return workloadEvents;
	}
	
	private Set<MarteService> getServices() {
		return services;
	}
	
	private UMLModelLoader getLoader() {
		return loader;
	}
	
	private void extractServicesAndWorkloadEvents() {
		getLoader().setModel(modelPath).loadModel();
		GaAnalysisContext analysisContext = getAnalysisContext();
		if(analysisContext == null) {
			// no valid analysis context was given, try to extract
			analysisContext = extractAnalysisContext();
		}
		if(analysisContext != null) {
			// get services and workload events referenced by context
			services = extractServices(analysisContext);
			workloadEvents = extractWorkloadEvents(analysisContext);
		} else {
			// extract services and workload events from model 
			NamedElement rootElement = getLoader().obtainFirstNamedElement();
			services = extractServices(rootElement, new HashSet<MarteService>());
			workloadEvents = extractWorkloadEvents(rootElement, new ArrayList<GaWorkloadEvent>());
		}
	}
	
	public MarteAnalysis analyzeScenarios() {
		extractServicesAndWorkloadEvents();
		
		MarteAnalysis analyis = new MarteAnalysis(getLoader().getUMLModelResource());
		analyis.setServices(getServices());
		
		UMLModelExecutor executor = new UMLModelExecutor(getLoader());
		for(GaWorkloadEvent event : getWorkloadEvents())
			if(event.getEffect() != null) {				
				analyis.addTrace(new MarteTrace(event, 
						extractSteps(event.getEffect(), 
								executor.executeActivity(event.getEffect().getBase_NamedElement().getQualifiedName()))));
				
			}
		
		return analyis;
	}
	
	private <T extends Element> T getMappedElement(fUML.Syntax.Classes.Kernel.Element element, Class<T> clazz) {
		return clazz.cast(loader.getConversionResult().getInputObject(element));
	}
	
	private void addStep(GaStep step, List<MarteTraceStep> steps) {
		if(step == null)
			return;
		MarteService resource = findResource(step);
		if(resource != null)
			steps.add(new MarteTraceStep(resource, step));
	}
	
	private void extractSteps(ActivityExecution activityExecution, List<MarteTraceStep> steps) {
		if(activityExecution == null)
			return;
	
		GaStep step = MarteUtil.getFirstStereotype(
				getMappedElement(activityExecution.getActivity(), Activity.class),
				GaStep.class);
		if(step == null)
			step = MarteUtil.getFirstStereotype(
					getMappedElement(activityExecution.getActivity(), Activity.class).getSpecification(), 
					GaStep.class);
		
		addStep(step, steps);
		
		for (ActivityNodeExecution nodeExecution : activityExecution.getNodeExecutions()) 
			extractSteps(nodeExecution, steps);	
	}

	private void extractSteps(ActivityNodeExecution nodeExecution, List<MarteTraceStep> steps) {
		if(nodeExecution == null)
			return;
		
		GaStep step = MarteUtil.getFirstStereotype(
				getMappedElement(nodeExecution.getNode(), ActivityNode.class),
				GaStep.class);
		
		addStep(step, steps);
		
		if (nodeExecution instanceof CallActionExecution) {
			CallActionExecution callActionExecution = (CallActionExecution) nodeExecution;			
			ActivityExecution calledExecution = callActionExecution.getCallee();
			extractSteps(calledExecution, steps);
		}
	}
	
	private MarteService findResource(GaStep step) {
		for(MarteService resource : getServices()) {
			if(step.getBase_NamedElement().getQualifiedName().startsWith(resource.getUmlElement().getQualifiedName()))
				return resource;
		}
		return null;
	}
	
	private List<MarteTraceStep> extractSteps(GaScenario scenario, Trace trace) {
		List<MarteTraceStep> steps = new ArrayList<MarteTraceStep>();
		ActivityExecution rootActivityExecution = extractRootActivityExecution(scenario, trace);
		extractSteps(rootActivityExecution, steps);		
		return steps;
	}
	
	private ActivityExecution extractRootActivityExecution(Trace trace) {
		for (ActivityExecution execution : trace.getActivityExecutions())
			if (execution.getCaller() == null) {
				return execution;
			}
		return null;
	}
	
	private ActivityNodeExecution extractRootNodeExecution(Trace trace) {
		for(ActivityNodeExecution execution : extractRootActivityExecution(trace).getNodeExecutions())
			if(execution.getChronologicalPredecessor() == null)
				return execution;
		return null;
	}
	
	private ActivityExecution extractRootActivityExecution(GaScenario scenario, Trace trace) {
		if(scenario == null || scenario.getRoot() == null)
			return extractRootActivityExecution(trace);
		
		for(ActivityExecution execution : trace.getActivityExecutions()) {
			if(scenario.getBase_NamedElement().getQualifiedName().equals(execution.getActivity().qualifiedName)) {
				return execution;
			}
		}
				
		return null;
	}
	
	private ActivityNodeExecution extractRootNodeExecution(GaScenario scenario, Trace trace) {
		if(scenario == null || scenario.getRoot() == null)
			return extractRootNodeExecution(trace);

		ActivityExecution rootActivityExecution = extractRootActivityExecution(scenario, trace);
		if(rootActivityExecution != null) {
			for(ActivityNodeExecution nodeExecution : rootActivityExecution.getNodeExecutions()) 
				if(scenario.getRoot().getBase_NamedElement().getQualifiedName().equals(nodeExecution.getNode().qualifiedName))
					return nodeExecution;
		}
		
		return null;
	}
	
	private GaAnalysisContext extractAnalysisContext() {
		TreeIterator<EObject> modelContents = getLoader().loadModel().getUMLModelResource().getAllContents();
		while(modelContents.hasNext()) {
			EObject eObject = modelContents.next();
			if(eObject instanceof Element) {
				Element element = (Element)eObject;
				GaAnalysisContext context = MarteUtil.getExactStereotype(element, GaAnalysisContext.class);
				if (context != null && context.getPlatform() != null && context.getWorkload() != null) {
					return context;
				}
			}			
		}		
		return null;
	}
	
	private GaAnalysisContext extractAnalysisContext(String analysisContextQN) {
		TreeIterator<EObject> modelContents = getLoader().loadModel().getUMLModelResource().getAllContents();
		while(modelContents.hasNext()) {
			EObject eObject = modelContents.next();			
			if(eObject instanceof NamedElement) {
				NamedElement element = (NamedElement)eObject;
				GaAnalysisContext context = MarteUtil.getExactStereotype(element, GaAnalysisContext.class);
				if (context != null && element.getQualifiedName().equals(analysisContextQN)) {
					return context;
				}
			}			
		}		
		return null;
	}
}