package org.modelexecution.fuml.nfr.simulation.printer;

import scala.collection.immutable.Range;

public interface IStaticResultPrinter {
	void printStatic(StringBuilder builer, Range range);
}
