/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 * Tanja Mayerhofer - implementation
 */
package org.modelexecution.fuml.nfr.debug.internal.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.modelexecution.fuml.nfr.qn.MarteAnalysis;
import org.modelexecution.fuml.nfr.qn.MarteAnalyzer;
import org.modelexecution.fuml.nfr.qn.conversion.MarteAnalysisToQNConversion;
import org.modelexecution.fuml.nfr.qn.conversion.MarteAnalysisToQNConverter;

public class InternalNFRQNProcess extends Process {

	public static final int EXIT_VALUE = 0;
	
	private MarteAnalyzer analyzer;
	
	private int simulationTime;
	private String analysisContext;
	@SuppressWarnings("unused")
	private String resultPath;
	

	public InternalNFRQNProcess(String modelPath, String resultPath, String analysisContext, int simulationTime) {
		this.resultPath = resultPath;
		this.analysisContext = analysisContext;
		analyzer = new MarteAnalyzer(modelPath);		
	}

	public void run() {
		MarteAnalysis analysis = analyzer.analyzeScenarios();
		analyzer.setAnalysisContext(analysisContext);
		
		MarteAnalysisToQNConverter converter = new MarteAnalysisToQNConverter();
		MarteAnalysisToQNConversion conversion = converter.convertToQueuingNet(analysis, simulationTime);			

		// TODO result has to be stored in given path
	}

	public boolean isInRunMode() {
		return true;
	}

	@Override
	public OutputStream getOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// we don't need anything from outside using this stream
				// as communication is done directly using commands
			}
		};
	}

	@Override
	public InputStream getInputStream() {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				// we don't communicate via input stream
				return 0;
			}
		};
	}

	@Override
	public InputStream getErrorStream() {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				// we don't communicate via input stream
				return 0;
			}
		};
	}

	@Override
	public int waitFor() throws InterruptedException {
		return EXIT_VALUE;
	}

	@Override
	public int exitValue() {
		return EXIT_VALUE;
	}

	@Override
	public void destroy() {

	}

}
