package org.modelexecution.fuml.nfr.petstore;

import org.junit.Test;
import org.modelexecution.fuml.nfr.qn.MarteAnalysis;
import org.modelexecution.fuml.nfr.qn.MarteAnalyzer;
import org.modelexecution.fuml.nfr.qn.conversion.MarteAnalysisToQNConverter;

import at.ac.tuwien.big.simpleqn.QueuingNet;

public class PetstoreQNTest {
	@Test
	public void simpleExecutionTest() {
		MarteAnalyzer analyzer = new MarteAnalyzer().setModel(PetstoreExample.DI_MODEL_PATH);
		MarteAnalysis analysis = analyzer.analyzeScenarios();
		System.out.println(analysis);
		MarteAnalysisToQNConverter converter = new MarteAnalysisToQNConverter();
		QueuingNet net = converter.createQueuingNet(analysis, 5);
	}
	
	@Test
	public void simpleAnalysisTest() {
		MarteAnalyzer analyzer = new MarteAnalyzer().setModel(PetstoreExample.DI_MODEL_PATH);
		MarteAnalysis analysis = analyzer.analyzeScenarios();
		System.out.println(analysis);
	}
}
