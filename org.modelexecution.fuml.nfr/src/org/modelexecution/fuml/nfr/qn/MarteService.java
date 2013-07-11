package org.modelexecution.fuml.nfr.qn;

import org.eclipse.papyrus.MARTE_Library.GRM_BasicTypes.SchedPolicyKind;
import org.eclipse.uml2.uml.NamedElement;

public class MarteService {
	private NamedElement umlElement;
	private String name;
	private int multiplicity;
	private SchedPolicyKind schedulingPolicy;
	private int defaultServiceTime = 1;

	public MarteService() {
	}
	
	public MarteService(NamedElement umlElement, int multiplicity, SchedPolicyKind schedulingPolicy) {
		setUmlElement(umlElement);
		setMultiplicity(multiplicity);
		setSchedulingPolicy(schedulingPolicy);
	}
	
	public NamedElement getUmlElement() {
		return umlElement;
	}

	public MarteService setUmlElement(NamedElement umlElement) {
		this.umlElement = umlElement;
		return this;
	}

	public String getName() {
		if(getUmlElement() == null)
			return "";
		return getUmlElement().getName();
	}

	public int getMultiplicity() {
		return multiplicity;
	}

	public MarteService setMultiplicity(int multiplicity) {
		this.multiplicity = multiplicity;
		return this;
	}

	public SchedPolicyKind getSchedulingPolicy() {
		return schedulingPolicy;
	}

	public MarteService setSchedulingPolicy(SchedPolicyKind schedulingPolicy) {
		this.schedulingPolicy = schedulingPolicy;
		return this;
	}
	
	public int getDefaultServiceTime() {
		return defaultServiceTime;
	}

	public MarteService setDefaultServiceTime(int defaultServiceTime) {
		this.defaultServiceTime = defaultServiceTime;
		return this;
	}
	
	@Override
	public String toString() {
		return getMultiplicity() + " x " + getName() + " from " + getUmlElement().getQualifiedName() + " with " + getSchedulingPolicy() + " (default service time: " + getDefaultServiceTime() + ")";
	}
}
