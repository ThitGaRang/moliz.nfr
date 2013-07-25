/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Martin Fleck - initial version
 */
package org.modelexecution.fuml.nfr.simulation.result;

import java.io.IOException;
import java.io.OutputStream;

import org.modelexecution.fuml.nfr.simulation.WorkloadSimulation;
import org.modelexecution.fuml.nfr.simulation.printer.AbstractPrinter;
import org.modelexecution.fuml.nfr.simulation.printer.IEvolutionResultPrinter;
import org.modelexecution.fuml.nfr.simulation.printer.ScenarioPrinter;
import org.modelexecution.fuml.nfr.simulation.printer.ServicePrinter;
import org.modelexecution.fuml.nfr.simulation.printer.ServicesAvgQueueLengthPrinter;
import org.modelexecution.fuml.nfr.simulation.printer.ServicesUtilizationPrinter;
import org.modelexecution.fuml.nfr.simulation.printer.WorkloadPrinter;
import org.modelexecution.fuml.nfr.simulation.workload.WorkloadScenario;

import scala.collection.immutable.Range.Inclusive;
import at.ac.tuwien.big.simpleqn.Service;

public class SimulationCSVPrinter extends AbstractPrinter {

	private static final int ONE_SECOND = 1000;
	private static final char SEPARATOR = ',';
	
	private WorkloadSimulation simulation;
	private WorkloadPrinter netPrinter;
	private int finalTime;
	private int timeStepSize;
	
	public SimulationCSVPrinter(WorkloadSimulation simulation) {
		super(SEPARATOR);
		this.simulation = simulation;
		this.finalTime = simulation.getQueuingNet().completionTime();
		this.timeStepSize = ONE_SECOND;
	}
	
	public int getTimeStepSize() {
		return timeStepSize;
	}
	
	public SimulationCSVPrinter setTimeStepSize(int timeStepSize) {
		this.timeStepSize = timeStepSize;
		return this;
	}
	
	public int getFinalTime() {
		return finalTime;
	}

	public SimulationCSVPrinter setFinalTime(int finalTime) {
		this.finalTime = finalTime;
		return this;
	}
	
	public WorkloadSimulation getSimulation() {
		return simulation;
	}
	
	/********** Helper methods ***********/

	private WorkloadPrinter getNetPrinter() {
		if(netPrinter == null)
			netPrinter = new WorkloadPrinter(getSeparator(), getSimulation().getQueuingNet());
		return netPrinter;
	}
	
	/*********** Printing All  ***********/
	
	public void printAllTo(OutputStream out) throws IOException {
		printStaticInformation(out);
		printNetEvolution(out);
		printAllServicesEvolution(out);
		printServicesUtilizationEvolution(out);
	}
	
	/*********** Static Printing ***********/
	
	public void printStaticInformation(OutputStream out) throws IOException {
		StringBuilder buffer = new StringBuilder();
		Inclusive range = new Inclusive(0, getFinalTime(), 1);
		
		printLine(buffer, "Analysis Data for time 0 - " + getFinalTime());
		printNewLine(buffer);
		out.write(buffer.toString().getBytes());
		out.flush();
		buffer = new StringBuilder();
		
		printLine(buffer, "Overall Values");
		printLine(buffer, "--------------");
		getNetPrinter().printStatic(buffer, range);
		
		printNewLine(buffer);
		out.write(buffer.toString().getBytes());
		out.flush();
		buffer = new StringBuilder();
		
		printLine(buffer, "-------------------------------------------------");
		printNewLine(buffer);
		
		printLine(buffer, "Services");
		printLine(buffer, "--------");
		printStaticInformationServices(buffer, range);
		printNewLine(buffer);
		out.write(buffer.toString().getBytes());
		out.flush();
		buffer = new StringBuilder();
		
		printLine(buffer, "-------------------------------------------------");
		printNewLine(buffer);
		
		printLine(buffer, "Scenarios");
		printLine(buffer, "---------");
		printStaticInformationScenarios(buffer, range);
		out.write(buffer.toString().getBytes());		
		out.flush();
	}
	
	private void printStaticInformationServices(StringBuilder buffer, Inclusive range) {
		for(Service service : getSimulation().getAllServices())	
			new ServicePrinter(getSeparator(), service).printStatic(buffer, range);
	}
	
	private void printStaticInformationScenarios(StringBuilder builer, Inclusive range) {
		for(WorkloadScenario scenario : simulation.getWorkload().getScenarios())
			new ScenarioPrinter(getSeparator(), simulation.getQueuingNet(), scenario).printStatic(builer, range);
	}
	
	/*********** Net Printing ***********/
	
	public void printNetEvolution(OutputStream out) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		getNetPrinter().printPropertyHeaderLine(builder);
		getNetPrinter().printPropertyEvolution(builder, getFinalTime(), getTimeStepSize());
		
		out.write(builder.toString().getBytes());
		out.flush();
	}
	
	/*********** Service Printing ***********/
	
	public void printAllServicesEvolution(OutputStream out) throws IOException {
		for(Service service : getSimulation().getAllServices())	
			printServiceEvolution(out, service);
	}
	
	public void printServiceEvolution(OutputStream out, Service service) throws IOException {		
		ServicePrinter printer = new ServicePrinter(getSeparator(), service);
		StringBuilder builder = new StringBuilder();
		
		printer.printPropertyHeaderLine(builder);
		printer.printPropertyEvolution(builder, getFinalTime(), getTimeStepSize());
		
		out.write(builder.toString().getBytes());
		out.flush();
	}
	
	/************* Property Printing *********/
	
	public void printAllServicePropertiesEvolution(OutputStream out) throws IOException {
		printServicesUtilizationEvolution(out);
		printServicesAvgQueueLengthEvolution(out);
	}
	
	public void printServicesUtilizationEvolution(OutputStream out) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		IEvolutionResultPrinter printer = new ServicesUtilizationPrinter(getSeparator(), getSimulation().getAllServices());
		printer.printPropertyHeaderLine(builder);
		printer.printPropertyEvolution(builder, getFinalTime(), getTimeStepSize());
		
		out.write(builder.toString().getBytes());
		out.flush();
	}
	
	public void printServicesAvgQueueLengthEvolution(OutputStream out) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		IEvolutionResultPrinter printer = new ServicesAvgQueueLengthPrinter(getSeparator(), getSimulation().getAllServices());
		printer.printPropertyHeaderLine(builder);
		printer.printPropertyEvolution(builder, getFinalTime(), getTimeStepSize());
		
		out.write(builder.toString().getBytes());
		out.flush();
	}
}
