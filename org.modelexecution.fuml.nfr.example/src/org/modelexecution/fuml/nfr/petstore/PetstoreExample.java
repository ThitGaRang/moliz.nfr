package org.modelexecution.fuml.nfr.petstore;

public class PetstoreExample {
	public static final String INPUT_MODEL_PATH = "model/petstore/petstore.uml";
	public static final String OUTPUT_MODEL_PATH = "model/petstore/output/petstore.uml";
	
	public static final String OUTPUT_BASE_PATH = "model/petstore/output/";
	public static final String OUTPUT_SERVICES_PATH = "model/petstore/output/petstoremodel_services.csv";
	public static final String OUTPUT_NET_PATH = "model/petstore/output/petstoremodel_net.csv";
	public static final String OUTPUT_INFO_PATH = "model/petstore/output/petstoremodel_overview.txt";
	
	public static final String CASE_STUDY_1_ACTIVITY = "caseStudy1";
	public static final int SIMULATION_TIME = 5000; // 5s
	public static final String AC_QN = "petstore::AnalysisContext";
}
