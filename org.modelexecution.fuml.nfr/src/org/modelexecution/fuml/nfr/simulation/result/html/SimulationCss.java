/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Martin Fleck - initial version
 */
package org.modelexecution.fuml.nfr.simulation.result.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.modelexecution.fuml.nfr.simulation.result.AbstractPrinter;

public class SimulationCss extends AbstractPrinter {

	private String cssString;
	
	public void writeFile(File file) throws FileNotFoundException, IOException {
		FileOutputStream jsFile = new FileOutputStream(file);
		jsFile.write(getCssString().getBytes());
		jsFile.close();
	}
	
	private String getCssString() {
		if(cssString == null) {
			StringBuilder builder = new StringBuilder();
			printLine(builder, "body {");
			printLine(builder, "  font: 10px sans-serif;");
			printLine(builder, "}");
			printLine(builder, "");
			printLine(builder, ".axis path,");
			printLine(builder, ".axis line {");
			printLine(builder, "  fill: none;");
			printLine(builder, "  stroke: #000;");
			printLine(builder, "  shape-rendering: crispEdges;");
			printLine(builder, "}");
			printLine(builder, "");
			printLine(builder, ".x.axis path {");
//			printLine(builder, "  display: none;"); show x-axis line :)
			printLine(builder, "}");
			printLine(builder, "");
			printLine(builder, ".line {");
			printLine(builder, "  fill: none;");
			printLine(builder, "  stroke: steelblue;");
			printLine(builder, "  stroke-width: 1.5px;");
			printLine(builder, "}");
			cssString = builder.toString();
		}
		return cssString;
	}

}
