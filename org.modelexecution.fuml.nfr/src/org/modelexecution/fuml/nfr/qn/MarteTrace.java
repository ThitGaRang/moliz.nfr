package org.modelexecution.fuml.nfr.qn;

import java.util.List;

import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaScenario;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaWorkloadEvent;

public class MarteTrace {
	
	private GaWorkloadEvent workloadEvent;
	private GaScenario scenario;
	private List<MarteTraceStep> steps;

	public MarteTrace(GaWorkloadEvent workloadEvent, List<MarteTraceStep> steps) {
		this.workloadEvent = workloadEvent;
		this.scenario = workloadEvent.getEffect();
		this.steps = steps;
	}
	
	public MarteTrace(GaScenario scenario, List<MarteTraceStep> steps) {
		this.workloadEvent = scenario.getCause();
		this.scenario = scenario;
		this.steps = steps;
	}
	
	public GaWorkloadEvent getWorkloadEvent() {
		return workloadEvent;
	}

	public void setWorkloadEvent(GaWorkloadEvent workloadEvent) {
		this.workloadEvent = workloadEvent;
	}

	public GaScenario getScenario() {
		return scenario;
	}

	public void setScenario(GaScenario scenario) {
		this.scenario = scenario;
	}

	public List<MarteTraceStep> getSteps() {
		return steps;
	}

	public void setSteps(List<MarteTraceStep> steps) {
		this.steps = steps;
	}
}
