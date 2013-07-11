package org.modelexecution.fuml.nfr.qn;

import java.util.List;

import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaScenario;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaWorkloadEvent;
import org.eclipse.uml2.uml.NamedElement;

public class MarteTrace {
	
	private GaWorkloadEvent workloadEvent;
	private GaScenario scenario;
	private List<MarteTraceStep> steps;

	public MarteTrace(GaWorkloadEvent workloadEvent, List<MarteTraceStep> steps) {
		this.workloadEvent = workloadEvent;
		this.scenario = workloadEvent.getEffect();
		setSteps(steps);
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
		for(MarteTraceStep step : steps)
			step.setTrace(this);
		this.steps = steps;
	}
	
	public NamedElement getScenarioUmlElement() {
		if(getScenario() == null)
			return null;
		return getScenario().getBase_NamedElement();
	}
	
	public String getName() {
		if(getScenario() == null)
			return null;
		return getScenario().getBase_NamedElement().getName();
	}
	
	@Override
	public String toString() {
		return getScenario().getRoot().getBase_NamedElement().getQualifiedName() + " of " + 
				getScenario().getBase_NamedElement().getQualifiedName() + " with '" + 
				getScenario().getCause().getPattern() + "' containing " + 
				getSteps().size() + " steps";
	}
}
