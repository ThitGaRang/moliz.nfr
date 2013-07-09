package org.modelexecution.fuml.nfr.qn.arrival;

import java.util.Iterator;

public interface IArrivalTimeGenerator extends Iterator<Integer>, Iterable<Integer> {
	IArrivalTimeGenerator setSimulationTime(int simulationTime);
	int getSimulationTime();
}
