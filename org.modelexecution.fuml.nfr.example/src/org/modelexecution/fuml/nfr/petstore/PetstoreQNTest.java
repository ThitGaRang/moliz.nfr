package org.modelexecution.fuml.nfr.petstore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.modelexecution.fuml.nfr.qn.MarteAnalysis;
import org.modelexecution.fuml.nfr.qn.MarteAnalyzer;
import org.modelexecution.fuml.nfr.qn.MarteTrace;
import org.modelexecution.fuml.nfr.qn.conversion.ConversionCSVPrinter;
import org.modelexecution.fuml.nfr.qn.conversion.ConversionModelAnnotator;
import org.modelexecution.fuml.nfr.qn.conversion.MarteAnalysisToQNConversion;
import org.modelexecution.fuml.nfr.qn.conversion.MarteAnalysisToQNConverter;

import at.ac.tuwien.big.simpleqn.QueuingNet;

public class PetstoreQNTest {	
	private static MarteAnalyzer analyzer = new MarteAnalyzer(PetstoreExample.INPUT_MODEL_PATH);
	private static MarteAnalysis analysis = analyzer.analyzeScenarios();
	private static MarteAnalysisToQNConversion conversion = new MarteAnalysisToQNConverter().convertToQueuingNet(analysis, PetstoreExample.SIMULATION_TIME);
	
	@Test
	public void csvPrinterTest() throws IOException {
		ConversionCSVPrinter printer = new ConversionCSVPrinter(conversion);
		printer.printAllToFiles(PetstoreExample.OUTPUT_BASE_PATH);
		Assert.assertTrue(new File(PetstoreExample.OUTPUT_SERVICES_PATH).exists());
		Assert.assertTrue(new File(PetstoreExample.OUTPUT_NET_PATH).exists());
		Assert.assertTrue(new File(PetstoreExample.OUTPUT_INFO_PATH).exists());
	}
	
	@Test
	public void modelAnnotationTest() {
		ConversionModelAnnotator annotator = new ConversionModelAnnotator(conversion);
		annotator.annotateModel().saveModel(PetstoreExample.OUTPUT_MODEL_PATH);
		Assert.assertTrue(new File(PetstoreExample.OUTPUT_MODEL_PATH).exists());
	}
	
	@Test
	public void conversionTest() {
		QueuingNet net = conversion.getQueuingNet();
		Assert.assertNotNull(net);
		Assert.assertTrue(net.isClosed());
		Assert.assertEquals(analysis.getServices().size(), net.services().size());
		Assert.assertFalse(net.jobs().isEmpty());
		Assert.assertFalse(net.completedJobs().isEmpty());
		Assert.assertTrue(net.completionTime() >= PetstoreExample.SIMULATION_TIME);
	}
	
	@Test
	public void analysisGiveACTest() {
		MarteAnalysis analysis = new MarteAnalyzer(PetstoreExample.INPUT_MODEL_PATH).setAnalysisContext(PetstoreExample.AC_QN).analyzeScenarios();
		
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add("EntityManager");
		serviceNames.add("OrderService");
		serviceNames.add("CustomerService");
		serviceNames.add("ApplicationController");
		serviceNames.add("CatalogService");
		
		Assert.assertEquals(serviceNames.size(), analysis.getServices().size());
		
		List<String> scenarioNames = new ArrayList<String>();
		scenarioNames.add("buyScenario");
		scenarioNames.add("errorLoginScenario");
		
		Assert.assertEquals(scenarioNames.size(), analysis.getTraces().size());
		
		MarteTrace buyScenario = analysis.getTraces().get(0);
		Assert.assertEquals(13, buyScenario.getSteps().size());
		Assert.assertEquals("open(poisson(0.4))", buyScenario.getWorkloadEvent().getPattern());
		Assert.assertEquals("login", buyScenario.getSteps().get(0).getName());
		Assert.assertEquals("ApplicationController", buyScenario.getSteps().get(0).getService().getName());
		
		MarteTrace errorLoginScenario = analysis.getTraces().get(1);
		Assert.assertEquals(3, errorLoginScenario.getSteps().size());
		Assert.assertEquals("open(poisson(0.7))", errorLoginScenario.getWorkloadEvent().getPattern());
		Assert.assertEquals("login", errorLoginScenario.getSteps().get(0).getName());
		Assert.assertEquals("ApplicationController", errorLoginScenario.getSteps().get(0).getService().getName());
		
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
		
		Assert.assertEquals(serviceNames.size(), analysis.getServices().size());
		
		List<String> scenarioNames = new ArrayList<String>();
		scenarioNames.add("buyScenario");
		scenarioNames.add("errorLoginScenario");
		
		Assert.assertEquals(scenarioNames.size(), analysis.getTraces().size());
		
		MarteTrace buyScenario = analysis.getTraces().get(0);
		Assert.assertEquals(13, buyScenario.getSteps().size());
		Assert.assertEquals("open(poisson(0.4))", buyScenario.getWorkloadEvent().getPattern());
		Assert.assertEquals("login", buyScenario.getSteps().get(0).getName());
		Assert.assertEquals("ApplicationController", buyScenario.getSteps().get(0).getService().getName());
		
		MarteTrace errorLoginScenario = analysis.getTraces().get(1);
		Assert.assertEquals(3, errorLoginScenario.getSteps().size());
		Assert.assertEquals("open(poisson(0.7))", errorLoginScenario.getWorkloadEvent().getPattern());
		Assert.assertEquals("login", errorLoginScenario.getSteps().get(0).getName());
		Assert.assertEquals("ApplicationController", errorLoginScenario.getSteps().get(0).getService().getName());
		
		System.out.println(analysis);
	}
}
