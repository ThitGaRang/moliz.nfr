package org.modelexecution.fuml.nfr.qn.conversion;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaAnalysisContext;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaScenario;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.ResourceUsage;
import org.modelexecution.fuml.nfr.qn.MarteService;
import org.modelexecution.fuml.nfr.qn.MarteTrace;
import org.modelexecution.fuml.nfr.qn.MarteUtil;

import at.ac.tuwien.big.simpleqn.Service;

public class ConversionModelAnnotator {
	
	private static final String RESULT_PREFIX = "$out.";
	
	private MarteAnalysisToQNConversion conversion;
	
	public ConversionModelAnnotator(MarteAnalysisToQNConversion conversion) {
		this.conversion = conversion;
	}
	
	private EList<String> getSingleValueList(Integer value) {
		return getSingleValueList(value.toString());
	}
	
	private EList<String> getSingleValueList(Double value) {
		return getSingleValueList(value.toString());
	}
	
	private EList<String> getSingleValueList(String value) {
		EList<String> list = new BasicEList<String>();
		list.add(value);
		return list;
	}
	
	private EList<String> addResult(EList<String> list, String name, double result) {
		list.add(RESULT_PREFIX + name + "=" + result);
		return list;
	}	
	
	private EList<String> createResultList() {
		return new BasicEList<String>();
	}
	
	private void annotateServices() {
		Service qnService;
		EList<String> result;
		for(MarteService service : conversion.getMarteAnalysis().getServices()) {
			qnService = conversion.getService(service);
			result = new BasicEList<String>();
			addResult(result, "utilization", qnService.utilization());
			addResult(result, "idleTime", qnService.idleTime());
			addResult(result, "serviceTime", qnService.serviceTime());
			addResult(result, "busyTime", qnService.busyTime());
			addResult(result, "maxQueueLength", qnService.maxQueueLength());
			addResult(result, "avgQueueLength", qnService.avgQueueLength());
			
			MarteUtil.setOrUpdateFeature(
				service.getUmlElement(), 
				GaAnalysisContext.class, "context", result);
		}
	}
	
	private void annotateScenarios() {
		
		Double avgWaitingTime, avgResidenceTime, avgServiceTime;
		int maxWaitingTime;
		
		EList<String> result;
		for(MarteTrace trace : conversion.getMarteAnalysis().getTraces()) {
			conversion.getQueuingNet().averageResidenceTimeOfJobCategory(trace.getName());
			result = new BasicEList<String>();
			add
			
			MarteUtil.setOrUpdateFeature(
				trace.getScenarioUmlElement(),
				GaAnalysisContext.class, "context", result);
		}
	}
	
	private void annotateModel() {
		
	}
	
	public void annotate() {
		annotateServices();
		annotateScenarios();
	}
}
