package org.modelexecution.fuml.nfr.qn;

import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaStep;
import org.eclipse.uml2.uml.NamedElement;
import org.modelexecution.fuml.nfr.qn.usage.BasicResourceUsageSum;
import org.modelexecution.fuml.nfr.qn.usage.IResourceUsageSum;

public class MarteTraceStep {
	private MarteTrace trace;
	private MarteService service;
	private GaStep step;
	private IResourceUsageSum resourceUsage;
	
	public MarteTraceStep(MarteService service, GaStep step) {
		setService(service);
		setStep(step);
	}

	public MarteService getService() {
		return service;
	}

	public MarteTraceStep setService(MarteService service) {
		this.service = service;
		return this;
	}

	public GaStep getStep() {
		return step;
	}

	public MarteTraceStep setStep(GaStep step) {
		this.step = step;
		resourceUsage = new BasicResourceUsageSum(step.getBase_NamedElement(), step);
		return this;
	}
	
	public IResourceUsageSum getResourceUsage() {
		return resourceUsage;
	}
	
	public NamedElement getUmlElement() {
		if(step != null)
			return step.getBase_NamedElement();
		return null;
	}
	
	public MarteTraceStep reComputeSums() {
		getResourceUsage().reComputeSums();
		return this;
	}
	
	public MarteTrace getTrace() {
		return trace;
	}
	
	public MarteTraceStep setTrace(MarteTrace trace) {
		this.trace = trace;
		return this;
	}
	
	public String getName() {
		if(getStep() == null)
			return "";
		return getStep().getBase_NamedElement().getName();
	}
	
	@Override
	public String toString() {
		return getResourceUsage().getExecTimeSum() + "ms " + getName() + " on " + getService().getName();
	}
}
