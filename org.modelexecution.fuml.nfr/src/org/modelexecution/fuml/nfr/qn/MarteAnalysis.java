package org.modelexecution.fuml.nfr.qn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarteAnalysis {
	private Set<MarteService> services;
	private List<MarteTrace> traces;
	
	public MarteAnalysis() {
		services = new HashSet<MarteService>();
		traces = new ArrayList<MarteTrace>();
	}
	
	public MarteAnalysis(Set<MarteService> services, List<MarteTrace> scenarios) {
		this.services = services;
		this.traces = scenarios;
	}

	public Set<MarteService> getServices() {
		return services;
	}

	public void setServices(Set<MarteService> services) {
		this.services = services;
	}
	
	public void addService(MarteService services) {
		this.services.add(services);
	}

	public List<MarteTrace> getTraces() {
		return traces;
	}

	public void setScenarios(List<MarteTrace> scenarios) {
		this.traces = scenarios;
	}
	
	public void addScenario(MarteTrace scenario) {
		this.traces.add(scenario);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("MarteAnalysis\n");
		builder.append("-------------\n");
		builder.append("Resources:\n");
		for(MarteService resource : getServices()) 
			builder.append("  " + resource + "\n");
		
		builder.append("\n");
		
		builder.append("Traces:\n");
		for(MarteTrace trace : getTraces()) {
			builder.append("  " + trace.getScenario().getRoot().getBase_NamedElement().getQualifiedName());
			builder.append(" of " + trace.getScenario().getBase_NamedElement().getQualifiedName());
			builder.append(" with " + trace.getScenario().getCause().getPattern() + "\n");
			
			for(MarteTraceStep step : trace.getSteps()) {
				String demand = step.getStep().getExecTime().size() > 0 ? step.getStep().getExecTime().get(0) : "0";
				builder.append("    " + demand + "s " + step.getStep().getBase_NamedElement().getQualifiedName() + " on " + step.getService().getName() + "\n");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
}
