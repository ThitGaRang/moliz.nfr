package org.modelexecution.fuml.nfr.qn.conversion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.modelexecution.fuml.nfr.qn.MarteService;
import org.modelexecution.fuml.nfr.qn.MarteTrace;

import scala.collection.immutable.Range.Inclusive;

import at.ac.tuwien.big.simpleqn.QueuingNet;
import at.ac.tuwien.big.simpleqn.Service;

public class ConversionCSVPrinter {
	
	private static final String NEWLINE = "\n";
	
	private MarteAnalysisToQNConversion conversion;
	private char separator = ',';
	private int finalTime;
	
	public ConversionCSVPrinter(MarteAnalysisToQNConversion conversion) {
		this.conversion = conversion;
		this.finalTime = conversion.getQueuingNet().completionTime();
	}
	
	public char getSeparator() {
		return separator;
	}

	public ConversionCSVPrinter setSeparator(char separator) {
		this.separator = separator;
		return this;
	}
	
	public int getFinalTime() {
		return finalTime;
	}

	public ConversionCSVPrinter setFinalTime(int finalTime) {
		this.finalTime = finalTime;
		return this;
	}
	
	public MarteAnalysisToQNConversion getConversion() {
		return conversion;
	}

	/*********** Printing All  ***********/
	
	public void printAllToFiles(String baseFileName) throws IOException {
		printStaticInformationToFile(baseFileName + ".txt");
		printServicesToFile(baseFileName + "_services" + ".csv");
		printNetToFile(baseFileName + "_net" + ".csv");
	}
	
	public void printAllTo(OutputStream out) throws IOException {
		printStaticInformationTo(out);
		printNetTo(out);
		printServicesTo(out);
	}
	
	/*********** Static Printing ***********/
	
	public void printStaticInformationToFile(String fileName) throws IOException {
		FileOutputStream stream = new FileOutputStream(new File(fileName));
		printStaticInformationTo(stream);
		stream.close();
	}
	
	public void printStaticInformationTo(OutputStream out) throws IOException {
		StringBuilder buffer = new StringBuilder();
		Inclusive range = new Inclusive(0, getFinalTime(), 1);
		
		printLine(buffer, "Analysis Data for time 0 - " + getFinalTime());
		printNewLine(buffer);
		out.write(buffer.toString().getBytes());
		out.flush();
		buffer = new StringBuilder();
		
		printLine(buffer, "Overall Values");
		printLine(buffer, "--------------");
		printStaticInformationNet(buffer, range);
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
	
	private void printStaticInformationNet(StringBuilder buffer, Inclusive range) {
		QueuingNet net = getConversion().getQueuingNet();		
		printLine(buffer, "Utilization:      " + net.utilization(range));
		printLine(buffer, "Throughput:       " + net.throughput(range));
		printLine(buffer, "Completed Jobs:   " + net.completedJobs(range).size());
		printLine(buffer, "Jobs:             " + net.jobs().size());
		printLine(buffer, "Services:         " + net.services().size());
		printLine(buffer, "Completion Time:  " + net.completionTime());
		printLine(buffer, "LastArrival Time: " + net.latestCompletingJob().arrivalTime());
	}
	
	private void printStaticInformationServices(StringBuilder buffer, Inclusive range) {
		Service qnService;
		for(MarteService service : getConversion().getMarteAnalysis().getServices()) {
			qnService = getConversion().getService(service);
			if(qnService == null)
				break;
			printLine(buffer, "Name: " + qnService.name());
			printLine(buffer, "  Service Time:     " + qnService.serviceTime());
			printLine(buffer, "  Utilization:      " + qnService.utilization(range));
			printLine(buffer, "  Idle Time:        " + qnService.idleTime(range));
			printLine(buffer, "  Busy Time:        " + qnService.busyTime(range));
			printLine(buffer, "  Max Queue Length: " + qnService.maxQueueLength(range));
			printLine(buffer, "  Avg Queue Length: " + qnService.avgQueueLength(range));
			printNewLine(buffer);
		}
	}
	
	private void printStaticInformationScenarios(StringBuilder buffer, Inclusive range) {
		QueuingNet net = conversion.getQueuingNet();
		String traceName;
		for(MarteTrace trace : conversion.getMarteAnalysis().getTraces()) {
			traceName = trace.getName();
			printLine(buffer, "Name: " + trace.getName());
			printLine(buffer, "  Avg Residence Time: " + net.averageResidenceTimeOfJobCategory(traceName));
			printLine(buffer, "  Min Residence Time: " + net.minResidenceTimeOfJobCategory(traceName));
			printLine(buffer, "  Max Residence Time: " + net.maxResidenceTimeOfJobCategory(traceName));
			printLine(buffer, "  Avg Service Time:   " + net.averageServiceTimeOfJobCategory(traceName));
			printLine(buffer, "  Min Service Time:   " + net.minServiceTimeOfJobCategory(traceName));
			printLine(buffer, "  Max Service Time:   " + net.maxServiceTimeOfJobCategory(traceName));
			printLine(buffer, "  Avg Waiting Time:   " + net.averageWaitingTimeOfJobCategory(traceName));
			printLine(buffer, "  Min Waiting Time:   " + net.minWaitingTimeOfJobCategory(traceName));
			printLine(buffer, "  Max Waiting Time:   " + net.maxWaitingTimeOfJobCategory(traceName));
			printNewLine(buffer);
		}
	}
	
	/*********** Net Printing ***********/
	
	public void printNetToFile(String fileName) throws IOException {
		FileOutputStream stream = new FileOutputStream(new File(fileName));
		printNetTo(stream);
		stream.close();
	}
	
	public void printNetTo(OutputStream out) throws IOException {
		StringBuilder buffer = new StringBuilder();
		
		printNetHeader(buffer);
		printNet(buffer);
		
		out.write(buffer.toString().getBytes());
		out.flush();
	}
	
	private void printNetHeader(StringBuilder buffer) {
		printWithSeparator(buffer, "time");
		printWithSeparator(buffer, "utilization");
		printWithSeparator(buffer, "throughput");
		printWithSeparator(buffer, "completedJobs");
		printWithSeparator(buffer, "busyTime");
		printNewLine(buffer);
	}
	
	private void printNet(StringBuilder buffer) {
		QueuingNet net = getConversion().getQueuingNet();
		Inclusive range;
		
		for(int i = 0; i < getFinalTime(); i++) {
			range = new Inclusive(0, i, 1);
			printWithSeparator(buffer, i);
			printWithSeparator(buffer, net.utilization(range));
			printWithSeparator(buffer, net.throughput(range));
			printWithSeparator(buffer, net.completedJobs(range).size());
			printWithSeparator(buffer, net.busyTime(range));
			printNewLine(buffer);
		}
	}
	
	/*********** Service Printing ***********/
	
	public void printServicesToFile(String fileName) throws IOException {
		FileOutputStream stream = new FileOutputStream(new File(fileName));
		printServicesTo(stream);
		stream.close();
	}
	
	public void printServicesTo(OutputStream out) throws IOException {
		StringBuilder buffer = new StringBuilder();
		
		printServiceHeader(buffer);
		printServices(buffer);
		
		out.write(buffer.toString().getBytes());
		out.flush();
	}
	
	private void printServiceHeader(StringBuilder buffer) {
		printWithSeparator(buffer, "time");
		printWithSeparator(buffer, "name");
		printWithSeparator(buffer, "utilization");
		printWithSeparator(buffer, "idleTime");
		printWithSeparator(buffer, "busyTime");
		printWithSeparator(buffer, "maxQueueLength");
		printWithSeparator(buffer, "avgQueueLength");
		// "serviceTime", qnService.serviceTime() -> Service time is fix
		printNewLine(buffer);
	}
	
	private void printServices(StringBuilder buffer) {
		for(int i = 0; i < getFinalTime(); i++) {
			printServices(buffer, i);
		}
	}
	
	private void printServices(StringBuilder buffer, int time) {
		Service qnService;
		for(MarteService service : getConversion().getMarteAnalysis().getServices()) {
			qnService = getConversion().getService(service);
			if(qnService == null)
				break;
			
			printService(buffer, qnService, time);
		}
	}
	
	private void printService(StringBuilder buffer, Service service, int endTime) {
		Inclusive range = new Inclusive(0, endTime, 1);
		printWithSeparator(buffer, range.last());
		printWithSeparator(buffer, service.name());
		printWithSeparator(buffer, service.utilization(range));
		printWithSeparator(buffer, service.idleTime(range));
		printWithSeparator(buffer, service.busyTime(range));
		printWithSeparator(buffer, service.maxQueueLength(range));
		printWithSeparator(buffer, service.avgQueueLength(range));
		printNewLine(buffer);
	}
	
	private void printLine(StringBuilder buffer, Object text) {
		buffer.append(text.toString() + NEWLINE);
	}
	
	private void printWithSeparator(StringBuilder buffer, Object text) {
		buffer.append(text.toString() + getSeparator());
	}
	
	private void printNewLine(StringBuilder buffer) {
		// don't allow empty values, all separator at the end get removed
		while(getSeparator() == buffer.charAt(buffer.length() - 1))
			buffer.deleteCharAt(buffer.length() - 1);
		buffer.append(NEWLINE);
	}
}
