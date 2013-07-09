package org.modelexecution.fuml.nfr.qn.arrival;

public class PeriodicArrivalTimeGenerator extends AbstractArrivalTimeGenerator {

	private int start;
	private int period;
	private int next;
	
	public PeriodicArrivalTimeGenerator(int simulationTime, int period) {
		super(simulationTime);
		this.start = 0;
		this.period = period;
		this.next = start + period;
	}
	
	@Override
	public boolean hasNext() {
		return next <= getSimulationTime();
	}

	@Override
	public Integer next() {
		int curNext = next;
		this.next += period;
		return curNext;
	}

	@Override
	public void remove() {
		; // do nothing
	}

}
