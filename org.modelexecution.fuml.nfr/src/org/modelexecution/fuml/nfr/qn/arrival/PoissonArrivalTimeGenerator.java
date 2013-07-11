package org.modelexecution.fuml.nfr.qn.arrival;

import java.util.Iterator;

public class PoissonArrivalTimeGenerator extends AbstractArrivalTimeGenerator implements IArrivalTimeGenerator, Iterator<Integer> {

	private double lambda;
	private int next;
	
	public PoissonArrivalTimeGenerator() {
	}
	
	public PoissonArrivalTimeGenerator(int simulationTime, double lambda) {
		super(simulationTime);
		this.lambda = lambda;
	}
	
	private double nextPoisson() {
		double y = getRandom().nextDouble();
		double x = Math.log(1-y);
		double a = 1/y;
		return (-a) * x;
    }

	public double getLambda() {
		return lambda;
	}
	
	public PoissonArrivalTimeGenerator setLambda(double lambda) {
		this.lambda = lambda;
		return this;
	}
	
	@Override
	public boolean hasNext() {
		return next <= getSimulationTime();
	}

	@Override
	public Integer next() {
		int curNext = next;
		next += nextPoisson();
		return curNext;
	}

	@Override
	public void remove() {
		; // do nothing
	}
}
