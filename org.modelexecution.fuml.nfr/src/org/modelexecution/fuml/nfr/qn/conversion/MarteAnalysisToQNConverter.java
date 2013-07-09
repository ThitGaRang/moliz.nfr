package org.modelexecution.fuml.nfr.qn.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.eclipse.papyrus.MARTE_Library.GRM_BasicTypes.SchedPolicyKind;
import org.modelexecution.fuml.nfr.qn.MarteAnalysis;
import org.modelexecution.fuml.nfr.qn.MarteService;
import org.modelexecution.fuml.nfr.qn.MarteTrace;
import org.modelexecution.fuml.nfr.qn.MarteTraceStep;
import org.modelexecution.fuml.nfr.qn.MarteUtil;
import org.modelexecution.fuml.nfr.qn.arrival.ArrivalTimeGeneratorFactory;
import org.modelexecution.fuml.nfr.qn.arrival.IArrivalTimeGenerator;

import at.ac.tuwien.big.simpleqn.Job;
import at.ac.tuwien.big.simpleqn.QueuingNet;
import at.ac.tuwien.big.simpleqn.Service;
import at.ac.tuwien.big.simpleqn.FixedBalancer;
import at.ac.tuwien.big.simpleqn.strategies.RoundRobinBalancing;

public class MarteAnalysisToQNConverter {
	private Map<MarteService, Service> serviceMapping = new HashMap<MarteService, Service>();
	
	public MarteAnalysisToQNConverter() { }
	
	private Service createServiceFrom(MarteService resource) {
		if(resource.getMultiplicity() == 1)
			return new Service(resource.getName(), resource.getDefaultServiceTime());
		else {
			if(resource.getSchedulingPolicy() == SchedPolicyKind.ROUND_ROBIN)
				return new FixedBalancer(resource.getName(), resource.getDefaultServiceTime(), new RoundRobinBalancing(0), resource.getMultiplicity());
			return new Service(resource.getName(), resource.getDefaultServiceTime());
		}
	}
	
	public QueuingNet createQueuingNet(MarteAnalysis analysis, int simulationTime) {
		if(analysis == null)
			return null;

		for(MarteService marteService : analysis.getServices())
			serviceMapping.put(marteService, createServiceFrom(marteService));
		
		QueuingNet net = new QueuingNet(new ArrayList<Service>(serviceMapping.values()));
		System.out.println("Services: " + net.services());
		
		Job job;
		Service service;
		for(MarteTrace trace : analysis.getTraces()) {
			System.out.println(trace.getWorkloadEvent().getBase_NamedElement().getLabel() + ": ");
			IArrivalTimeGenerator generator = ArrivalTimeGeneratorFactory.getInstance().getGenerator(simulationTime, trace.getWorkloadEvent().getPattern());
			
			for(int time : generator) {
				job = new Job(time, net);
				System.out.print("  " + time + ", " + trace.getScenario().getBase_NamedElement().getQualifiedName());
				for(MarteTraceStep step : trace.getSteps()) {
					service = serviceMapping.get(step.getService());
					
					if(service != null) {
						int demand = (int)MarteUtil.extractDoubleFromString(step.getStep().getExecTime().get(0));
						if(demand > 0) {
							System.out.print(service.name() + " (" + demand + ")  ");
							job.request(service, demand);
						}
					}
				}
				System.out.println();
			}
			System.out.println();
		}
		
		net.close();
		return net;
	}
	
	public static void printQueuingNet(QueuingNet net) {
		System.out.println("Queuing Net");
		System.out.println("---------------------------");
		System.out.println("Utilization: " + net.utilization());
		System.out.println("Throughput: " + net.throughput());
		System.out.println("Completed Jobs: " + net.completedJobs());
		System.out.println("---------------------------");
		System.out.println("Jobs: " + net.jobs());
		System.out.println("Services: " + net.services());
	}
}
