package org.modelexecution.fuml.nfr.petstore;

import java.io.IOException;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.modelexecution.fuml.nfr.simulation.WorkloadSimulation;
import org.modelexecution.fuml.nfr.simulation.WorkloadSimulator;
import org.modelexecution.fuml.nfr.simulation.result.SimulationCSVFilePrinter;
import org.modelexecution.fuml.nfr.simulation.result.SimulationCSVPrinter;
import org.modelexecution.fuml.nfr.simulation.workload.ServiceCenter;
import org.modelexecution.fuml.nfr.simulation.workload.Workload;
import org.modelexecution.fuml.nfr.simulation.workload.WorkloadExtractor;

import scala.collection.JavaConversions;

import at.ac.tuwien.big.simpleqn.Service;

public class PetstoreAnalysisTest {
	private static String BASE = "model/petstore/output/";
	private static String NAME = "petstore";
	private static int simulationTime = 50000;
	private Integer jobSize = null;
	
	private static WorkloadExtractor analyzer = new WorkloadExtractor(PetstoreExample.INPUT_MODEL_PATH);
	private static Workload analysis = analyzer.extractWorkload();
	private static ServiceCenter entityManager;
	
	@Before
	public void setUp() {
		for(ServiceCenter service : analysis.getServiceCenters())
			if(service.getName().equals("EntityManager")) {
				entityManager = service;
				break;
			}
	}
	
	private void executeTest(int srPoolSize, boolean isDynamic, String subDir) throws IOException {
		long startTime = System.currentTimeMillis();

		entityManager.setMultiplicity(srPoolSize).setDynamic(isDynamic);
		WorkloadSimulation conversion = new WorkloadSimulator().simulateWorkload(analysis, simulationTime);
		if(jobSize == null)
			jobSize = conversion.getQueuingNet().completedJobs().size();
		while(conversion.getQueuingNet().completedJobs().size() != jobSize)
			conversion = new WorkloadSimulator().simulateWorkload(analysis, simulationTime);
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Analysis finished: " + estimatedTime + "ms");
		SimulationCSVFilePrinter printer = new SimulationCSVFilePrinter(conversion);
		printer.setFileDirectory(BASE + subDir);
		printer.printStaticInformation(NAME + "_overview.txt");
	}
	
	//@Test
	public void createAnalysisData() throws IOException {
		simulationTime = 100000;
		executeTest(1, false, (simulationTime / 1000) + "_EM_1/");
		executeTest(2, false, (simulationTime / 1000) + "_EM_2/");
		executeTest(3, false, (simulationTime / 1000) + "_EM_3/");
		executeTest(1, true,  (simulationTime / 1000) + "_EM_d/");
	}
}
