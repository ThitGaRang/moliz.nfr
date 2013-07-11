package org.modelexecution.fuml.nfr.qn.arrival;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ArrivalTimeGeneratorFactory {
	private static ArrivalTimeGeneratorFactory instance;
	
	private static String doublePattern = "(-?(\\d*)(\\.?)(\\d*))";
	private static Pattern poissonPattern = Pattern.compile("\\D*[open(]?poisson[(]" + doublePattern + "\\D*[)][)]?");
	private static Pattern periodicPattern = Pattern.compile("\\D*[periodic(]?period=[(]" + doublePattern + "ms\\D*[)][)]?");

	public ArrivalTimeGeneratorFactory() { }
	
	public static ArrivalTimeGeneratorFactory getInstance() {
		if(instance == null)
			instance = new ArrivalTimeGeneratorFactory();
		return instance;
	}
	
	public IArrivalTimeGenerator getGenerator(int simulationTime, String martePattern) {
		Matcher patternMatcher;
		if((patternMatcher = poissonPattern.matcher(martePattern)).matches()) {
			Double lambda = Double.parseDouble(patternMatcher.group(1));
			return new PoissonArrivalTimeGenerator(simulationTime, lambda);
		} else if((patternMatcher = periodicPattern.matcher(martePattern)).matches()) {
			int period = new Double(Double.parseDouble(patternMatcher.group(1))).intValue();
			return new PeriodicArrivalTimeGenerator(simulationTime, period);
		}
		return null;
	}
	
	private void testGenerator(String title, String pattern) {
		IArrivalTimeGenerator generator = ArrivalTimeGeneratorFactory.getInstance().getGenerator(20, pattern);
		System.out.println(title + " with " + pattern);
		for(int time : generator)
			System.out.print(time + " ");
		System.out.println("\n---------------");
	}

	@Test
	public void testPatterns() {
		testGenerator("Poisson", "open(poisson(.2))");
		testGenerator("Poisson", "poisson(0.7)");
		testGenerator("Poisson", "poisson(2.5)");
		testGenerator("Periodic", "periodic(period=(2ms))");
		testGenerator("Periodic", "period=(1ms)");
	}
}