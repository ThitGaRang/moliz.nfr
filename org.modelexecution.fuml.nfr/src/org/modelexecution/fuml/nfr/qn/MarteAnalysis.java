package org.modelexecution.fuml.nfr.qn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;

public class MarteAnalysis {
	private Resource modelResource;
	private Set<MarteService> services;
	private List<MarteTrace> traces;
	
	public MarteAnalysis(Resource modelResource) {
		this.modelResource = modelResource;
		services = new HashSet<MarteService>();
		traces = new ArrayList<MarteTrace>();
	}
	
	public MarteAnalysis(Resource modelResource, Set<MarteService> services, List<MarteTrace> scenarios) {
		this.modelResource = modelResource;
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

	public void setTraces(List<MarteTrace> traces) {
		this.traces = traces;
	}
	
	public void addTrace(MarteTrace scenario) {
		this.traces.add(scenario);
	}
	
	public Resource getModelResource() {
		return modelResource;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("MarteAnalysis\n");
		builder.append("-------------\n");
		
		builder.append("Services:\n");
		for(MarteService service : getServices())
			builder.append("  " + service + "\n");
		
		builder.append("\n");
		
		builder.append("Traces:\n");
		for(MarteTrace trace : getTraces()) {
			builder.append("  " + trace + "\n");			
			for(MarteTraceStep step : trace.getSteps())
				builder.append("    " + step  + "\n");				
				
			builder.append("\n");
		}
		return builder.toString();
	}
	
}
