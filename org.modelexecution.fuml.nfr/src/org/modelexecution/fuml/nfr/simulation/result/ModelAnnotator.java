/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Martin Fleck - initial version
 */
package org.modelexecution.fuml.nfr.simulation.result;


import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaAnalysisContext;
import org.eclipse.uml2.uml.Model;
import org.modelexecution.fuml.nfr.MarteUtil;
import org.modelexecution.fuml.nfr.simulation.WorkloadSimulation;
import org.modelexecution.fuml.nfr.simulation.workload.ServiceCenter;
import org.modelexecution.fuml.nfr.simulation.workload.WorkloadScenario;

import scala.collection.immutable.Range.Inclusive;


import at.ac.tuwien.big.simpleqn.QueuingNet;
import at.ac.tuwien.big.simpleqn.Service;

public class ModelAnnotator {
	
	private enum ResultType {
		
		Normal(""),
		Longrun("untilLastArrival"),
		Complete("untilLastCompletion");
		
		private final String prefix;
		
		private ResultType(String prefix) {
			this.prefix = prefix;
		}
		
		@Override
		public String toString() {
			return prefix;
		}
	}
	
	private static final String RESULT_PREFIX = "$out.";
	private static final String RESULT_SEPARATOR = ".";
	
	private WorkloadSimulation simulation;
	private boolean modelIsAnnotated = false;
	
	public ModelAnnotator(WorkloadSimulation simulation) {
		this.simulation = simulation;
	}
	
	private String getResultString(ResultType resultType, String subCategory, ISimulationResult result, Object value) {
		String subCategoryString = "";
		if(subCategory != null && !subCategory.isEmpty())
			subCategoryString = subCategory + RESULT_SEPARATOR;
		
		return RESULT_PREFIX + RESULT_SEPARATOR + 
				resultType + RESULT_SEPARATOR + 
				subCategoryString +
				result + "=" + value.toString();
	}
	
	private String getResultString(ResultType resultType, ISimulationResult result, Object value) {
		return getResultString(resultType, "", result, value);
	}
	
	private String getResultString(String subCategory, ISimulationResult result, Object value) {
		return getResultString(ResultType.Normal, subCategory, result, value);
	}
	
	private String getResultString(ISimulationResult result, Object value) {
		return getResultString(ResultType.Normal, "", result, value);
	}
	
	private EList<String> createResultList() {
		return new BasicEList<String>();
	}
	
	private Inclusive getRange(ResultType resultType) {
		if(resultType == ResultType.Complete)
			return getQueuingNet().completeRange();
		if(resultType == ResultType.Longrun)
			return getQueuingNet().estimatedLongRunRange();
		return null;
	}
	
	private void addServiceCenterResults(EList<String> result, Service service, ResultType resultType) {
		Inclusive range = getRange(resultType);
		if(range == null)
			return;
		String serviceName = getSimulation().getServiceName(service);
		result.add(getResultString(resultType, serviceName, ServiceCenterResult.Utilization, service.utilization(range)));
		result.add(getResultString(resultType, serviceName, ServiceCenterResult.IdleTime, service.idleTime(range)));
		result.add(getResultString(resultType, serviceName, ServiceCenterResult.BusyTime, service.busyTime(range)));
		result.add(getResultString(resultType, serviceName, ServiceCenterResult.MaxQueueLength, service.maxQueueLength(range)));
		result.add(getResultString(resultType, serviceName, ServiceCenterResult.AvgQueueLength, service.avgQueueLength(range)));
	}
	
	private void annotateServiceCenter(ServiceCenter serviceCenter) {
		if(serviceCenter == null)
			return;
		
		EList<String> results = createResultList();
		
		for(Service service : getSimulation().getAllServices(serviceCenter)) {
			results.add(getResultString(getSimulation().getServiceName(service), ServiceCenterResult.DefaultServiceTime, service.serviceTime()));
			addServiceCenterResults(results, service, ResultType.Complete);
			addServiceCenterResults(results, service, ResultType.Longrun);
		}
		
		MarteUtil.setOrUpdateFeature(
				serviceCenter.getUmlElement(),
				GaAnalysisContext.class, "context", results);
	}
	
	private void annotateServiceCenters() {
		for(ServiceCenter serviceCenter : getSimulation().getWorkload().getServiceCenters())
			annotateServiceCenter(serviceCenter);
	}
	
	private void annotateScenario(WorkloadScenario scenario) {
		String scenarioName = scenario.getName();
		EList<String> result = createResultList();
		result.add(getResultString(ScenarioResult.AvgResidenceTime, getQueuingNet().averageResidenceTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.MinResidenceTime, getQueuingNet().minResidenceTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.MaxResidenceTime, getQueuingNet().maxResidenceTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.AvgServiceTime, getQueuingNet().averageServiceTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.MinServiceTime, getQueuingNet().minServiceTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.MaxServiceTime, getQueuingNet().maxServiceTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.AvgWaitingTime, getQueuingNet().averageWaitingTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.MinWaitingTime, getQueuingNet().minWaitingTimeOfJobCategory(scenarioName)));
		result.add(getResultString(ScenarioResult.MaxWaitingTime, getQueuingNet().maxWaitingTimeOfJobCategory(scenarioName)));
		
		MarteUtil.setOrUpdateFeature(
				scenario.getScenarioUmlElement(),
				GaAnalysisContext.class, "context", result);
	}
	
	private void annotateScenarios() {		
		for(WorkloadScenario scenario : simulation.getWorkload().getScenarios()) 
			annotateScenario(scenario);
		
	}
	
	private void addWorkloadResults(EList<String> result, ResultType resultType) {
		Inclusive range = getRange(resultType);
		if(range == null)
			return;
		
		result.add(getResultString(resultType, WorkloadResult.Throughput, getQueuingNet().throughput(range)));
		result.add(getResultString(resultType, WorkloadResult.Utilization, getQueuingNet().utilization(range)));
		result.add(getResultString(resultType, WorkloadResult.CompletedJobs, getQueuingNet().completedJobs(range)));
	}
	
	private void annotateWorkload() {
		Model model = getSimulation().getWorkload().getModelElement();
		if(model == null)
			return;
		QueuingNet net = getQueuingNet();
		
		EList<String> result = createResultList();
		result.add(getResultString(WorkloadResult.CompletionTime, net.completionTime()));
		addWorkloadResults(result, ResultType.Complete);
		addWorkloadResults(result, ResultType.Longrun);

		MarteUtil.setOrUpdateFeature(
			model,
			GaAnalysisContext.class, "context", result);
	}
	
	private QueuingNet getQueuingNet() {
		if(getSimulation() == null)
			return null;
		return getSimulation().getQueuingNet();
	}
	
	public WorkloadSimulation getSimulation() {
		return simulation;
	}
	
	public ModelAnnotator annotateModel() {
		if(modelIsAnnotated)
			return this;
		
		annotateServiceCenters();
		annotateScenarios();
		annotateWorkload();
		modelIsAnnotated = true;
		
		return this;
	}
}
