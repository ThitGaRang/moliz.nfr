package org.modelexecution.fuml.nfr.qn;

import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaStep;

public class MarteTraceStep {
	private MarteService service;
	
	private GaStep step;
	
	public MarteTraceStep(MarteService service, GaStep step) {
		this.service = service;
		this.step = step;
	}

	public MarteService getService() {
		return service;
	}

	public MarteTraceStep setResource(MarteService resource) {
		this.service = resource;
		return this;
	}

	public GaStep getStep() {
		return step;
	}

	public MarteTraceStep setStep(GaStep step) {
		this.step = step;
		return this;
	}
}
