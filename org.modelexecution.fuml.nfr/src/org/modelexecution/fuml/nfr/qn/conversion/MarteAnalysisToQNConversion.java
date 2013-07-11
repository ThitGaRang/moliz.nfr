package org.modelexecution.fuml.nfr.qn.conversion;

import java.util.Map;

import org.modelexecution.fuml.nfr.qn.MarteAnalysis;
import org.modelexecution.fuml.nfr.qn.MarteService;
import org.modelexecution.fuml.nfr.qn.MarteTraceStep;

import at.ac.tuwien.big.simpleqn.QueuingNet;
import at.ac.tuwien.big.simpleqn.Request;
import at.ac.tuwien.big.simpleqn.Service;

public class MarteAnalysisToQNConversion {
	private MarteAnalysis marteAnalysis;
	private QueuingNet queuingNet;
	
	private int simulationTime;
	
	private Map<MarteService, Service> marteToQNService;
	private Map<Service, MarteService> qnToMarteService;
	
	private Map<MarteTraceStep, Request> traceStepToRequest;
	private Map<Request, MarteTraceStep> requestToTraceStep;
	
	public MarteAnalysisToQNConversion(MarteAnalysis marteAnalysis,
			QueuingNet queuingNet, int simulationTime,
			Map<MarteService, Service> marteToQNService,
			Map<Service, MarteService> qnToMarteService,
			Map<MarteTraceStep, Request> traceStepToRequest,
			Map<Request, MarteTraceStep> requestToTraceStep) {
		
		this.marteAnalysis = marteAnalysis;
		this.queuingNet = queuingNet;
		this.simulationTime = simulationTime;
		this.marteToQNService = marteToQNService;
		this.qnToMarteService = qnToMarteService;
		this.traceStepToRequest = traceStepToRequest;
		this.requestToTraceStep = requestToTraceStep;
	}

	public MarteAnalysis getMarteAnalysis() {
		return marteAnalysis;
	}

	public QueuingNet getQueuingNet() {
		return queuingNet;
	}

	public MarteAnalysisToQNConversion setQueuingNet(QueuingNet queuingNet) {
		this.queuingNet = queuingNet;
		return this;
	}

	public int getSimulationTime() {
		return simulationTime;
	}

	public Map<MarteService, Service> getMarteToQNService() {
		return marteToQNService;
	}

	public Map<Service, MarteService> getQnToMarteService() {
		return qnToMarteService;
	}

	public Map<MarteTraceStep, Request> getTraceStepToRequest() {
		return traceStepToRequest;
	}

	public Map<Request, MarteTraceStep> getRequestToTraceStep() {
		return requestToTraceStep;
	}

	public MarteTraceStep getTraceStep(Request request) {
		if(getRequestToTraceStep() != null)
			return getRequestToTraceStep().get(request);
		return null;
	}
	
	public Request getRequest(MarteTraceStep step) {
		if(getTraceStepToRequest() != null)
			return getTraceStepToRequest().get(step);
		return null;
	}
	
	public Service getService(MarteService service) {
		if(getMarteToQNService() != null)
			return getMarteToQNService().get(service);
		return null;
	}
	
	public MarteService getService(Service service) {
		if(getQnToMarteService() != null)
			return getQnToMarteService().get(service);
		return null;
	}
}