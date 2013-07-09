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
	
	private int nextPoisson() {
        // using algorithm given by Knuth
        // see http://en.wikipedia.org/wiki/Poisson_distribution
        int k = 0;
        double p = 1.0;
        double L = Math.exp(-lambda);
        do {
            k++;
            p *= getRandom().nextDouble();
        } while (p >= L);
        return k-1;
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
