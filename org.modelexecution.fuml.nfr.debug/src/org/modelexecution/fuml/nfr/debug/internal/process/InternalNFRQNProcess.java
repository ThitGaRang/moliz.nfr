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

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.modelexecution.fuml.nfr.debug.logger.ConsoleLogger;
import org.modelexecution.fuml.nfr.simulation.WorkloadSimulation;
import org.modelexecution.fuml.nfr.simulation.WorkloadSimulator;
import org.modelexecution.fuml.nfr.simulation.result.ModelAnnotator;
import org.modelexecution.fuml.nfr.simulation.result.ModelWriter;
import org.modelexecution.fuml.nfr.simulation.result.SimulationCSVFilePrinter;
import org.modelexecution.fuml.nfr.simulation.workload.Workload;
import org.modelexecution.fuml.nfr.simulation.workload.WorkloadExtractor;

public class InternalNFRQNProcess extends Process {

	public static final int EXIT_VALUE = 0;
	private WorkloadExtractor extractor;
	
	private int simulationTime;
	private String analysisContext;
	private String resultPath;
	

	public InternalNFRQNProcess(String modelPath, String resultPath, String analysisContext, int simulationTime) {
		this.resultPath = resultPath;
		this.analysisContext = analysisContext;
		this.simulationTime = simulationTime;
		extractor = new WorkloadExtractor(modelPath);		
	}

	public void run(ConsoleLogger consoleLogger) throws IOException {
		String filePath = CommonPlugin.resolve(URI.createPlatformResourceURI(resultPath, false)).toFileString() + "\\";
		
		consoleLogger.write("Start analyzing workload...");
		extractor.setAnalysisContext(analysisContext);
		Workload workload = extractor.extractWorkload();
		consoleLogger.write("done.\n");
		
		consoleLogger.write("Start conversion to queueing network...");
		WorkloadSimulator simulator = new WorkloadSimulator();
		WorkloadSimulation simulation = simulator.simulateWorkload(workload, simulationTime);			
		consoleLogger.write("done.\n");
		
		String umlOutputFile = filePath + workload.getModelName() + ".uml";
		consoleLogger.write("Save result model as '" + umlOutputFile + "'...");
		new ModelAnnotator(simulation).annotateModel();
		ModelWriter modelWriter = new ModelWriter(simulation);
		modelWriter.writeModel(umlOutputFile);
		consoleLogger.write("done.\n");
		
		consoleLogger.write("Save result in files...");
		SimulationCSVFilePrinter printer = new SimulationCSVFilePrinter(simulation);
		printer.setFileDirectory(filePath).printAll();
		consoleLogger.write("done.\n");
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
