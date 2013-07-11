package org.modelexecution.fuml.nfr.qn.conversion;

import at.ac.tuwien.big.simpleqn.QueuingNet;

public class ConversionCSVPrinter {
	
	public ConversionCSVPrinter() {
		
	}
	
	public static void printQueuingNet(QueuingNet net) {
		System.out.println("Queuing Net");
		System.out.println("---------------------------");
		System.out.println("Utilization: " + net.utilization());
		System.out.println("Throughput: " + net.throughput());
		System.out.println("Completed Jobs: " + net.completedJobs());
		System.out.println("---------------------------");
		System.out.println("Jobs: " + net.jobs());
		System.out.println("Services: " + net.services());
	}
}
