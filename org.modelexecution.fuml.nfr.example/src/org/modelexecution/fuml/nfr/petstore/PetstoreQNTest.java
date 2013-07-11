package org.modelexecution.fuml.nfr.petstore;

import org.junit.Test;
import org.modelexecution.fuml.nfr.qn.MarteAnalysis;
import org.modelexecution.fuml.nfr.qn.MarteAnalyzer;
import org.modelexecution.fuml.nfr.qn.conversion.ConversionCSVPrinter;
import org.modelexecution.fuml.nfr.qn.conversion.ConversionModelAnnotator;
import org.modelexecution.fuml.nfr.qn.conversion.MarteAnalysisToQNConversion;
import org.modelexecution.fuml.nfr.qn.conversion.MarteAnalysisToQNConverter;

import at.ac.tuwien.big.simpleqn.QueuingNet;

public class PetstoreQNTest {
	@Test
	public void simpleQNTest() {
		MarteAnalyzer analyzer = new MarteAnalyzer().setModel(PetstoreExample.DI_MODEL_PATH);
		MarteAnalysis analysis = analyzer.analyzeScenarios();
		System.out.println(analysis);
		MarteAnalysisToQNConverter converter = new MarteAnalysisToQNConverter();
		MarteAnalysisToQNConversion conversion = converter.convertToQueuingNet(analysis, 10);
		//conversion.getQueuingNet().debugPrint();
		//ConversionModelAnnotator annotator = new ConversionModelAnnotator(conversion);
		//annotator.annotate();
		ConversionCSVPrinter.printQueuingNet(conversion.getQueuingNet());
	}
	
	//@Test
	public void simpleAnalysisTest() {
		MarteAnalyzer analyzer = new MarteAnalyzer().setModel(PetstoreExample.DI_MODEL_PATH);
		MarteAnalysis analysis = analyzer.analyzeScenarios();
		System.out.println(analysis);		
	}
}
