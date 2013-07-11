package org.modelexecution.fuml.nfr.qn.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.papyrus.MARTE_Library.GRM_BasicTypes.SchedPolicyKind;
import org.modelexecution.fuml.nfr.qn.MarteAnalysis;
import org.modelexecution.fuml.nfr.qn.MarteService;
import org.modelexecution.fuml.nfr.qn.MarteTrace;
import org.modelexecution.fuml.nfr.qn.MarteTraceStep;
import org.modelexecution.fuml.nfr.qn.arrival.ArrivalTimeGeneratorFactory;
import org.modelexecution.fuml.nfr.qn.arrival.IArrivalTimeGenerator;

import at.ac.tuwien.big.simpleqn.FixedBalancer;
import at.ac.tuwien.big.simpleqn.Job;
import at.ac.tuwien.big.simpleqn.QueuingNet;
import at.ac.tuwien.big.simpleqn.Request;
import at.ac.tuwien.big.simpleqn.Service;
import at.ac.tuwien.big.simpleqn.strategies.RandomBalancing;
import at.ac.tuwien.big.simpleqn.strategies.RoundRobinBalancing;

public class MarteAnalysisToQNConverter {
	
	public MarteAnalysisToQNConverter() { }
	
	private Service createServiceFrom(MarteService resource) {
		if(resource.getMultiplicity() == 1 || resource.getSchedulingPolicy() == null)
			return new Service(resource.getName(), resource.getDefaultServiceTime());
		else if(resource.getSchedulingPolicy() == SchedPolicyKind.ROUND_ROBIN)
			return new FixedBalancer(resource.getName(), resource.getDefaultServiceTime(), new RoundRobinBalancing(0), resource.getMultiplicity());
		else if(resource.getSchedulingPolicy() == SchedPolicyKind.OTHER)
			return new FixedBalancer(resource.getName(), resource.getDefaultServiceTime(), new RandomBalancing(0), resource.getMultiplicity());
		
		return new Service(resource.getName(), resource.getDefaultServiceTime());
	}
	
	public MarteAnalysisToQNConversion convertToQueuingNet(MarteAnalysis analysis, int simulationTime) {
		if(analysis == null)
			return null;

		Map<MarteService, Service> marteToQNService = new HashMap<MarteService, Service>();
		Map<Service, MarteService> qnToMarteService = new HashMap<Service, MarteService>();

		for(MarteService marteService : analysis.getServices()) {
			Service service = createServiceFrom(marteService);
			marteToQNService.put(marteService, service);
			qnToMarteService.put(service, marteService);
		}
		
		
		QueuingNet net = new QueuingNet(new ArrayList<Service>(marteToQNService.values()));
		System.out.println("Services: " + net.services());
		
		Map<MarteTraceStep, Request> traceStepToRequest = new HashMap<MarteTraceStep, Request>();
		Map<Request, MarteTraceStep> requestToTraceStep = new HashMap<Request, MarteTraceStep>();
		
		Job job;
		Service service;
		Request request;
		for(MarteTrace trace : analysis.getTraces()) {
			System.out.println(trace.getWorkloadEvent().getBase_NamedElement().getLabel() + ": ");
			IArrivalTimeGenerator generator = ArrivalTimeGeneratorFactory.getInstance().getGenerator(simulationTime, trace.getWorkloadEvent().getPattern());
			
			for(int time : generator) {
				job = new Job(time, trace.getName(), net);
				System.out.print("  " + time + ", " + trace.getName());
				for(MarteTraceStep step : trace.getSteps()) {
					service = marteToQNService.get(step.getService());
					if(service != null) {
						int demand = (int)step.getResourceUsage().getExecTimeSum();
						if(demand > 0) {
							request = job.request(service, demand);
							traceStepToRequest.put(step, request);
							requestToTraceStep.put(request, step);
						}
					}
				}
				System.out.println();
			}
			System.out.println();
		}
		net.close();
		return new MarteAnalysisToQNConversion(analysis, net, simulationTime, marteToQNService, qnToMarteService, traceStepToRequest, requestToTraceStep);
	}
}
