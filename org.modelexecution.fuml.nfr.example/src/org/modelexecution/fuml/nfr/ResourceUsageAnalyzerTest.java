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

import static junit.framework.Assert.*;

import java.io.IOException;

import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.Resource;
import org.junit.Test;

public class ResourceUsageAnalyzerTest {

	private static final String SIMPLE_MODEL_MAIN_ACTIVITY_NAME = "main"; //$NON-NLS-1$
	private static final String SIMPLE_MODEL_PATH = "model/simple001.di"; //$NON-NLS-1$

	private static final String EHS_MODEL_MAIN_ACTIVITY_NAME = "eHS::main"; //$NON-NLS-1$
	private static final String EHS_MODEL_PATH = "model/ehs.di"; //$NON-NLS-1$

	@Test
	public void runAnalysisOnSimpleModel() {
		ResourceUsageAnalyzer analyzer = new ResourceUsageAnalyzer(SIMPLE_MODEL_PATH);
		ResourceUsageAnalysis analysis = analyzer.runAnalysis(SIMPLE_MODEL_MAIN_ACTIVITY_NAME);
		
		assertEquals(1, analysis.getResourceUsages().size());
		IResourceUsage resourceUsage = analysis.getResourceUsages().iterator().next();
		
		assertEquals(1, resourceUsage.getUsedResources().size());
		Resource resource = resourceUsage.getUsedResources().get(0);
		
		assertEquals(15f, Float.valueOf(resourceUsage.getExecTime(resource)));
		assertEquals(0f, Float.valueOf(resourceUsage.getAllocatedMemory(resource)));
	}

	@Test
	public void runAnalysisOnEHSModel() throws IOException {
		ResourceUsageAnalyzer analyzer = new ResourceUsageAnalyzer(EHS_MODEL_PATH);
		ResourceUsageAnalysis analysis = analyzer.runAnalysis(EHS_MODEL_MAIN_ACTIVITY_NAME);
		System.out.println("================== EHS ==================");
		
		// TODO implement asserts
		
		ResourceUsageCSVPrinter printer = new ResourceUsageCSVPrinter(analysis);
		printer.printTo(System.out);
	}
	
	private void debugPrint(IResourceUsage usage, String prefix) {			
		System.out.println(prefix + usage.getElement().getQualifiedName());
		for (Resource resource : usage.getUsedResources()) {
			System.out.println(prefix + "  " + resource.getBase_Property().getName());
			System.out.println(prefix + "    Allocated Memory=" + usage.getAllocatedMemory(resource));
			System.out.println(prefix + "    Energy=" + usage.getEnergy(resource));
			System.out.println(prefix + "    ExecTime=" + usage.getExecTime(resource));
			System.out.println(prefix + "    MsgSize=" + usage.getMsgSize(resource));
			System.out.println(prefix + "    PowerPeak=" + usage.getPowerPeak(resource));
			System.out.println(prefix + "    UsedMemory=" + usage.getUsedMemory(resource));
		}
		debugPrintChildren(usage, prefix);
	}

	private void debugPrintChildren(IResourceUsage usage, String prefix) {
		if (usage instanceof CompoundResourceUsage) {
			CompoundResourceUsage compoundResourceUsage = (CompoundResourceUsage) usage;
			for (IResourceUsage subUsage : compoundResourceUsage.getSubUsages()) {
				debugPrint(subUsage, prefix + ">");
			}
		}
	}

}
