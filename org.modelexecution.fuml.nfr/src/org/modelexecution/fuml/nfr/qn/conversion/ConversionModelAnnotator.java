package org.modelexecution.fuml.nfr.qn.conversion;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaAnalysisContext;
import org.eclipse.uml2.uml.Model;
import org.modelexecution.fuml.nfr.qn.MarteService;
import org.modelexecution.fuml.nfr.qn.MarteTrace;
import org.modelexecution.fuml.nfr.qn.MarteUtil;

import scala.collection.immutable.Range.Inclusive;

import at.ac.tuwien.big.simpleqn.QueuingNet;
import at.ac.tuwien.big.simpleqn.Service;

public class ConversionModelAnnotator {
	
	private static final String RESULT_PREFIX = "$out.";
	private static final String LONGRUN_RANGE_PREFIX = "untilLastArrival.";
	private static final String COMPLETE_RANGE_PREFIX = "untilLastCompletion.";
	
	private MarteAnalysisToQNConversion conversion;
	private boolean modelIsAnnotated = false;
	
	public ConversionModelAnnotator(MarteAnalysisToQNConversion conversion) {
		this.conversion = conversion;
	}
	
	private EList<String> addResult(EList<String> list, String name, Object result) {
		list.add(RESULT_PREFIX + name + "=" + result.toString());
		return list;
	}
	
	private EList<String> addCompleteResult(EList<String> list, String name, Object result) {
		list.add(RESULT_PREFIX + COMPLETE_RANGE_PREFIX + name + "=" + result.toString());
		return list;
	}
	
	private EList<String> addLongrunResult(EList<String> list, String name, Object result) {
		list.add(RESULT_PREFIX + LONGRUN_RANGE_PREFIX + name + "=" + result.toString());
		return list;
	}
	
	private EList<String> createResultList() {
		return new BasicEList<String>();
	}
	
	private void annotateServices() {
		Service qnService;
		EList<String> result;
		QueuingNet net = conversion.getQueuingNet();
		
		for(MarteService service : getConversion().getMarteAnalysis().getServices()) {
			qnService = getConversion().getService(service);
			if(qnService == null)
				break;
			
			result = createResultList();
			
			addResult(result, "serviceTime", qnService.serviceTime());
			
			addCompleteResult(result, "utilization", qnService.utilization(net.completeRange()));
			addCompleteResult(result, "idleTime", qnService.idleTime(net.completeRange()));
			addCompleteResult(result, "busyTime", qnService.busyTime(net.completeRange()));
			addCompleteResult(result, "maxQueueLength", qnService.maxQueueLength(net.completeRange()));
			addCompleteResult(result, "avgQueueLength", qnService.avgQueueLength(net.completeRange()));
			
			addLongrunResult(result, "utilization", qnService.utilization(net.estimatedLongRunRange()));
			addLongrunResult(result, "idleTime", qnService.idleTime(net.estimatedLongRunRange()));
			addLongrunResult(result, "busyTime", qnService.busyTime(net.estimatedLongRunRange()));
			addLongrunResult(result, "maxQueueLength", qnService.maxQueueLength(net.estimatedLongRunRange()));
			addLongrunResult(result, "avgQueueLength", qnService.avgQueueLength(net.estimatedLongRunRange()));
			
			MarteUtil.setOrUpdateFeature(
					service.getUmlElement(),
					GaAnalysisContext.class, "context", result);
		}
	}
	
	private void annotateScenarios() {		
		EList<String> result;
		QueuingNet net = conversion.getQueuingNet();
		String traceName;
		for(MarteTrace trace : conversion.getMarteAnalysis().getTraces()) {
			traceName = trace.getName();
			result = createResultList();
			addResult(result, "avgResidenceTime", net.averageResidenceTimeOfJobCategory(traceName));
			addResult(result, "minResidenceTime", net.minResidenceTimeOfJobCategory(traceName));
			addResult(result, "maxResidenceTime", net.maxResidenceTimeOfJobCategory(trace.getName()));
			addResult(result, "avgServiceTime", net.averageServiceTimeOfJobCategory(traceName));
			addResult(result, "minServiceTime", net.minServiceTimeOfJobCategory(trace.getName()));
			addResult(result, "maxServiceTime", net.maxServiceTimeOfJobCategory(trace.getName()));
			addResult(result, "avgWaitingTime", net.averageWaitingTimeOfJobCategory(traceName));
			addResult(result, "minWaitingTime", net.minWaitingTimeOfJobCategory(trace.getName()));
			addResult(result, "maxWaitingTime", net.maxWaitingTimeOfJobCategory(trace.getName()));
			
			MarteUtil.setOrUpdateFeature(
					trace.getScenarioUmlElement(),
					GaAnalysisContext.class, "context", result);
		}
	}
	
	private Model getModelElement() {
		TreeIterator<EObject> elements = getConversion().getMarteAnalysis().getUMLModelResource().getAllContents();
		while(elements.hasNext()) {
			EObject element = elements.next();
			if(element instanceof Model)
				return (Model) element;
		}
		return null;
	}
	
	private void annotateModelElement() {
		Model model = getModelElement();
		if(model == null)
			return;
		QueuingNet net = conversion.getQueuingNet();
		
		EList<String> result = createResultList();
		addResult(result, "completionTime", net.completionTime() + "s");
		
		
		addLongrunResult(result, "throughput", net.throughput(net.estimatedLongRunRange()) + " jobs/s");
		addLongrunResult(result, "utilization", net.utilization(net.estimatedLongRunRange()));
		addLongrunResult(result, "completedJobs", net.completedJobs(net.estimatedLongRunRange()).size());
		
		addCompleteResult(result, "throughput", net.throughput(net.completeRange()) + " jobs/s");
		addCompleteResult(result, "utilization", net.utilization(net.completeRange()));
		addCompleteResult(result, "completedJobs", net.completedJobs(net.completeRange()).size());

		MarteUtil.setOrUpdateFeature(
			model,
			GaAnalysisContext.class, "context", result);
	}
	
	public MarteAnalysisToQNConversion getConversion() {
		return conversion;
	}
	
	public ConversionModelAnnotator annotateModel() {
		if(modelIsAnnotated)
			return this;
		
		annotateServices();
		annotateScenarios();
		annotateModelElement();
		
		return this;
	}
	
	public Resource saveModel(String umlModelOutputPath) {		
		Resource modelResource = getConversion().getMarteAnalysis().getUMLModelResource();
	    Resource saveResource = modelResource.getResourceSet().createResource(
	    		URI.createURI(umlModelOutputPath));
	    saveResource.getContents().addAll(modelResource.getContents());

	    try {
	      saveResource.save(Collections.EMPTY_MAP);
	    } catch (IOException e) {
	      return null;
	    }
		return saveResource;
	}
}
