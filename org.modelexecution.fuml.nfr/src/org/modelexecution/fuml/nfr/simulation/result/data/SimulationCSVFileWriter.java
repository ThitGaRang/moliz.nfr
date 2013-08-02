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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.modelexecution.fuml.nfr.simulation.WorkloadSimulation;

import at.ac.tuwien.big.simpleqn.Service;

public class SimulationCSVFilePrinter extends SimulationCSVPrinter {

	private static final String INFO_FILE_EXT = ".txt";
	private static final String INFO_FILE = "_overview";
	
	private static final String FILE_EXT = ".csv";
	private static final String NET_FILE = "_net";
	private static final String SERVICE_FILE = "_service";

	private static final String DEFAULT_DIRECTORY = System.getProperty("user.home");
	
	private File fileDirectory;
	
	public SimulationCSVFilePrinter(WorkloadSimulation simulation) {
		super(simulation);
		setFileDirectory(DEFAULT_DIRECTORY);
	}
	
	public File getFileDirectory() {
		return fileDirectory;
	}
	
	public SimulationCSVFilePrinter setFileDirectory(String fileDirectory) {
		File directory = new File(fileDirectory);
		if(!directory.isDirectory())
			return this;
		directory.mkdirs();
		this.fileDirectory = directory;
		return this;
	}
	
	private String getStaticFileName(String baseName) {
		return baseName + INFO_FILE + INFO_FILE_EXT;
	}
	
	private String getNetFileName(String baseName) {
		return baseName + NET_FILE + FILE_EXT;
	}
	
	private String getServiceCenterFileName(String baseName, Service service) {
		return baseName + SERVICE_FILE + "_" + getSimulation().getServiceName(service) + FILE_EXT;
	}
	
	private FileOutputStream getFileStream(String fileName) throws IOException {
		File file = new File(getFileDirectory(), fileName);
		if(!file.exists())
			file.createNewFile();
		if(!file.isFile())
			return null;
		
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}
	
	private String getServicesUtilizationFileName(String baseName) {
		return baseName + SERVICE_FILE + "s_" + ServiceCenterResult.Utilization + FILE_EXT;
	}
	
	private String getServicesAvgQueueLengthFileName(String baseName) {
		return baseName + SERVICE_FILE + "s_" + ServiceCenterResult.AvgQueueLength + FILE_EXT;
	}
	
	private String getDefaultBaseName() {
		return getSimulation().getModelName();
	}
	
	/*********** Printing All  ***********/
	
	public void printAll() throws IOException {
		printAll(getDefaultBaseName());
	}
	
	public void printAll(String baseName) throws IOException {
		printStaticInformation(getStaticFileName(baseName));
		printNetEvolution(getNetFileName(baseName));
		printAllServicesEvolution(baseName);
		printAllServicePropertiesEvolution(baseName);
	}
	
	/*********** Static Printing ***********/
	
	public void printStaticInformation() throws IOException {
		printStaticInformation(getStaticFileName(getDefaultBaseName()));
	}
	
	public void printStaticInformation(String fileName) throws IOException {
		FileOutputStream stream = getFileStream(fileName);
		printStaticInformation(stream);
		stream.close();
	}
	
	public void printNetEvolution() throws IOException {
		printNetEvolution(getNetFileName(getDefaultBaseName()));
	}
	
	public void printNetEvolution(String fileName) throws IOException {
		FileOutputStream stream = getFileStream(fileName);
		printNetEvolution(stream);
		stream.close();
	}
	
	public void printAllServicesEvolution() throws IOException {
		printNetEvolution(getDefaultBaseName());
	}
	
	public void printAllServicesEvolution(String baseName) throws IOException {
		for(Service service : getSimulation().getAllServices())	
			printServiceEvolution(getServiceCenterFileName(baseName, service), service);
	}
	
	public void printServiceEvolution(String fileName, Service service) throws IOException {
		FileOutputStream stream = getFileStream(fileName);
		printServiceEvolution(stream, service);
		stream.close();
	}
	
	public void printAllServicePropertiesEvolution() throws IOException {
		printAllServicePropertiesEvolution(getDefaultBaseName());
	}
	
	public void printAllServicePropertiesEvolution(String baseName) throws IOException {
		printServicesUtilizationEvolution(getServicesUtilizationFileName(baseName));
		printServicesAvgQueueLengthEvolution(getServicesAvgQueueLengthFileName(baseName));
	}
	
	public void printServicesUtilizationEvolution(String fileName) throws IOException {
		FileOutputStream stream = getFileStream(fileName);
		printServicesUtilizationEvolution(stream);
		stream.close();
	}
	
	public void printServicesAvgQueueLengthEvolution(String fileName) throws IOException {
		FileOutputStream stream = getFileStream(fileName);
		printServicesAvgQueueLengthEvolution(stream);
		stream.close();
	}
}
