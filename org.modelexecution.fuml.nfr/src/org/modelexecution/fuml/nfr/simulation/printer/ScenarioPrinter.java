/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Martin Fleck - initial version
 */
package org.modelexecution.fuml.nfr.simulation.printer;

import org.modelexecution.fuml.nfr.simulation.result.ScenarioResult;
import org.modelexecution.fuml.nfr.simulation.workload.WorkloadScenario;

import scala.collection.immutable.Range;
import at.ac.tuwien.big.simpleqn.QueuingNet;

public class ScenarioPrinter extends AbstractPrinter implements IStaticResultPrinter {

	private QueuingNet net;
	private WorkloadScenario scenario;
	
	public ScenarioPrinter(char separator, QueuingNet net, WorkloadScenario scenario) {
		super(',');
		setQueuingNet(net);
	}
	
	public WorkloadScenario getWorkloadScenario() {
		return scenario;
	}
	
	public ScenarioPrinter setWorkloadScenario(WorkloadScenario scenario) {
		this.scenario = scenario;
		return this;
	}
	
	public QueuingNet getQueuingNet() {
		return net;
	}
	
	public ScenarioPrinter setQueuingNet(QueuingNet net) {
		this.net = net;
		return this;
	}
	
	public void printStatic(StringBuilder builer, Range range) {
		String scenarioName = getWorkloadScenario().getName();
		printLine(builer, ScenarioResult.Name.getTitle() + ": " + scenarioName);
		printLine(builer, ScenarioResult.AvgResidenceTime.getTitle() + "  : " + net.averageResidenceTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.MinResidenceTime.getTitle() + "  : " + net.minResidenceTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.MaxResidenceTime.getTitle() + "  : " + net.maxResidenceTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.AvgServiceTime.getTitle() + "  :   " + net.averageServiceTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.MinServiceTime.getTitle() + "  :   " + net.minServiceTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.MaxServiceTime.getTitle() + "  :   " + net.maxServiceTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.AvgWaitingTime.getTitle() + "  :   " + net.averageWaitingTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.MinWaitingTime.getTitle() + "  :   " + net.minWaitingTimeOfJobCategory(scenarioName));
		printLine(builer, ScenarioResult.MaxWaitingTime.getTitle() + "  :   " + net.maxWaitingTimeOfJobCategory(scenarioName));
		printNewLine(builer);
	}
}
