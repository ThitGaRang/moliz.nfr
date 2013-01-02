/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 */

package org.modelexecution.fuml.nfr;

import org.junit.Test;

public class MarteNFRAnalyzerTest {

	private static final String SIMPLE_MODEL_MAIN_ACTIVITY_NAME = "main"; //$NON-NLS-1$
	private static final String SIMPLE_MODEL_PATH = "model/simple001.di"; //$NON-NLS-1$

	private static final String EHS_MODEL_MAIN_ACTIVITY_NAME = "main"; //$NON-NLS-1$
	private static final String EHS_MODEL_PATH = "model/eHS.di"; //$NON-NLS-1$

	@Test
	public void runAnalysisOnSimpleModel() {
		MarteNFRAnalyzer nfrAnalyzer = new MarteNFRAnalyzer(SIMPLE_MODEL_PATH);
		nfrAnalyzer.runAnalysis(SIMPLE_MODEL_MAIN_ACTIVITY_NAME);

	}

	@Test
	public void runAnalysisOnEHSModel() {
		MarteNFRAnalyzer nfrAnalyzer = new MarteNFRAnalyzer(EHS_MODEL_PATH);
		nfrAnalyzer.runAnalysis(EHS_MODEL_MAIN_ACTIVITY_NAME);
	}

}
