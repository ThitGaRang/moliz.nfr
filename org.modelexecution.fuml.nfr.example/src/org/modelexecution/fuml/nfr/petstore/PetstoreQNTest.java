package org.modelexecution.fuml.nfr.petstore;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.modelexecution.fuml.nfr.simulation.WorkloadSimulation;
import org.modelexecution.fuml.nfr.simulation.WorkloadSimulator;
import org.modelexecution.fuml.nfr.simulation.result.ModelAnnotator;
import org.modelexecution.fuml.nfr.simulation.result.ModelWriter;
import org.modelexecution.fuml.nfr.simulation.result.SimulationCSVFilePrinter;
import org.modelexecution.fuml.nfr.simulation.workload.Workload;
import org.modelexecution.fuml.nfr.simulation.workload.WorkloadExtractor;
import org.modelexecution.fuml.nfr.simulation.workload.WorkloadScenario;

import at.ac.tuwien.big.simpleqn.QueuingNet;

public class PetstoreQNTest {	
	private static WorkloadExtractor extractor = new WorkloadExtractor(PetstoreExample.INPUT_MODEL_PATH);
	private static Workload workload = extractor.extractWorkload();
	private static WorkloadSimulation simulation = new WorkloadSimulator().simulateWorkload(workload, PetstoreExample.SIMULATION_TIME);
	
	@Test
	public void csvPrinterTest() throws IOException {
		SimulationCSVFilePrinter printer = new SimulationCSVFilePrinter(simulation);
		printer.setFileDirectory(PetstoreExample.OUTPUT_BASE_PATH);
		printer.printAll();
	}
	
	@Test
	public void modelAnnotationTest() {
		ModelAnnotator annotator = new ModelAnnotator(simulation);
		annotator.annotateModel();
		ModelWriter writer = new ModelWriter(simulation);
		writer.writeModel(PetstoreExample.OUTPUT_MODEL_PATH);
		assertTrue(new File(PetstoreExample.OUTPUT_MODEL_PATH).exists());
	}
	
	@Test
	public void conversionTest() {
		QueuingNet net = simulation.getQueuingNet();
		assertNotNull(net);
		assertTrue(net.isClosed());
		assertEquals(workload.getServiceCenters().size(), net.services().size());
		assertFalse(net.jobs().isEmpty());
		assertFalse(net.completedJobs().isEmpty());
		assertTrue(net.completionTime() >= PetstoreExample.SIMULATION_TIME / 1000);
	}
	
	@Test
	public void analysisGiveACTest() {
		Workload analysis = new WorkloadExtractor(PetstoreExample.INPUT_MODEL_PATH).setAnalysisContext(PetstoreExample.AC_QN).extractWorkload();
		
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add("EntityManager");
		serviceNames.add("OrderService");
		serviceNames.add("CustomerService");
		serviceNames.add("ApplicationController");
		serviceNames.add("CatalogService");
		
		assertEquals(serviceNames.size(), analysis.getServiceCenters().size());
		
		List<String> scenarioNames = new ArrayList<String>();
		scenarioNames.add("buyScenario");
		scenarioNames.add("errorLoginScenario");
		
		assertEquals(scenarioNames.size(), analysis.getScenarios().size());
		
		WorkloadScenario buyScenario = analysis.getScenarios().get(0);
		assertEquals(13, buyScenario.getSteps().size());
		assertEquals("open(exp(0.0005))", buyScenario.getWorkloadEvent().getPattern());
		assertEquals("login", buyScenario.getSteps().get(0).getName());
		assertEquals("ApplicationController", buyScenario.getSteps().get(0).getServiceCenter().getName());
		
		WorkloadScenario errorLoginScenario = analysis.getScenarios().get(1);
		assertEquals(3, errorLoginScenario.getSteps().size());
		assertEquals("open(exp(0.00002))", errorLoginScenario.getWorkloadEvent().getPattern());
		assertEquals("login", errorLoginScenario.getSteps().get(0).getName());
		assertEquals("ApplicationController", errorLoginScenario.getSteps().get(0).getServiceCenter().getName());
		
		System.out.println(analysis);
	}
	
	@Test
	public void analysisTest() {
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add("EntityManager");
		serviceNames.add("OrderService");
		serviceNames.add("CustomerService");
		serviceNames.add("ApplicationController");
		serviceNames.add("CatalogService");
		
		assertEquals(serviceNames.size(), workload.getServiceCenters().size());
		
		List<String> scenarioNames = new ArrayList<String>();
		scenarioNames.add("buyScenario");
		scenarioNames.add("errorLoginScenario");
		
		assertEquals(scenarioNames.size(), workload.getScenarios().size());
		
		WorkloadScenario buyScenario = workload.getScenarios().get(0);
		assertEquals(13, buyScenario.getSteps().size());
		assertEquals("open(exp(0.0005))", buyScenario.getWorkloadEvent().getPattern());
		assertEquals("login", buyScenario.getSteps().get(0).getName());
		assertEquals("ApplicationController", buyScenario.getSteps().get(0).getServiceCenter().getName());
		
		WorkloadScenario errorLoginScenario = workload.getScenarios().get(1);
		assertEquals(3, errorLoginScenario.getSteps().size());
		assertEquals("open(exp(0.00002))", errorLoginScenario.getWorkloadEvent().getPattern());
		assertEquals("login", errorLoginScenario.getSteps().get(0).getName());
		assertEquals("ApplicationController", errorLoginScenario.getSteps().get(0).getServiceCenter().getName());
		
		System.out.println(workload);
	}
}
