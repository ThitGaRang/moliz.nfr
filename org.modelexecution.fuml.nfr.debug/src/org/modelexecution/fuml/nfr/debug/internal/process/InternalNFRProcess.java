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

import org.modelexecution.fuml.nfr.ResourceUsageAnalysis;
import org.modelexecution.fuml.nfr.ResourceUsageAnalyzer;
import org.modelexecution.fuml.nfr.ResourceUsageCSVPrinter;

public class InternalNFRProcess extends Process {

	private String mainActivityName;
	private ResourceUsageAnalyzer analyzer;

	public static final int EXIT_VALUE = 0;

	private StringBuffer analysisResult = new StringBuffer();

	public InternalNFRProcess(String modelPath, String mainActivityName) {
		analyzer = new ResourceUsageAnalyzer(modelPath);
		this.mainActivityName = mainActivityName;
	}

	public void run() {
		ResourceUsageAnalysis analysis = analyzer.runAnalysis(mainActivityName);
		ResourceUsageCSVPrinter printer = new ResourceUsageCSVPrinter(analysis);
		try {
			printer.printTo(System.out);
			printer.printTo(analysisResult);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public StringBuffer getAnalysisResult() {		
		return analysisResult;
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

	public boolean isTerminated() {
		analyzer.isRunning();
		return false;
	}

}
