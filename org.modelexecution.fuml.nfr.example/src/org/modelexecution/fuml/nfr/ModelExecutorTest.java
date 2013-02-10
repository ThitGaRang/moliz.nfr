/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Tanja Mayerhofer - initial API and implementation
 */
package org.modelexecution.fuml.nfr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.CallActionExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.Trace;
import org.modelexecution.fumldebug.papyrus.PapyrusModelExecutor;

import fUML.Syntax.Actions.BasicActions.CallAction;
import fUML.Syntax.Actions.IntermediateActions.AddStructuralFeatureValueAction;
import fUML.Syntax.Actions.IntermediateActions.CreateObjectAction;
import fUML.Syntax.Actions.IntermediateActions.ReadSelfAction;
import fUML.Syntax.Actions.IntermediateActions.ReadStructuralFeatureAction;
import fUML.Syntax.Activities.IntermediateActivities.ActivityFinalNode;
import fUML.Syntax.Activities.IntermediateActivities.InitialNode;

public class ModelExecutorTest {

	@Test
	public void executeSimpleExample() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor("model/simple001.di");
		Trace trace = executor.executeActivity("main"); //$NON-NLS-1$
		
		assertEquals(5, trace.getActivityExecutions().size());
		
		ActivityExecution execution_main = trace.getActivityExecutions().get(0);
		assertEquals("main", execution_main.getActivity().name);
		ActivityExecution execution_RPIP_service = trace.getActivityExecutions().get(1);
		assertEquals("RPIP_service", execution_RPIP_service.getActivity().name);
		ActivityExecution execution_requestPatientInfoPages = trace.getActivityExecutions().get(2);
		assertEquals("requestPatientInfoPages", execution_requestPatientInfoPages.getActivity().name);
		ActivityExecution execution_a = trace.getActivityExecutions().get(3);
		assertEquals("A", execution_a.getActivity().name);
		ActivityExecution execution_b = trace.getActivityExecutions().get(4);
		assertEquals("B", execution_b.getActivity().name);
		
		// assert main
		assertEquals(4, execution_main.getNodeExecutions().size());
		assertTrue(execution_main.getNodeExecutions().get(0).getNode() instanceof CreateObjectAction);
		assertTrue(execution_main.getNodeExecutions().get(1).getNode() instanceof CreateObjectAction);
		assertTrue(execution_main.getNodeExecutions().get(2).getNode() instanceof AddStructuralFeatureValueAction);		 
		assertTrue(execution_main.getNodeExecutions().get(3).getNode() instanceof CallAction);
		CallActionExecution call_RPIP_service = (CallActionExecution)execution_main.getNodeExecutions().get(3);
		
		// assert RPIP_service
		assertEquals(4, execution_RPIP_service.getNodeExecutions().size());
		assertTrue(execution_RPIP_service.getNodeExecutions().get(0).getNode() instanceof ReadSelfAction);
		assertTrue(execution_RPIP_service.getNodeExecutions().get(1).getNode() instanceof ReadStructuralFeatureAction);
		assertTrue(execution_RPIP_service.getNodeExecutions().get(2).getNode() instanceof CallAction);
		CallActionExecution call_requestPatientInfoPages = (CallActionExecution)execution_RPIP_service.getNodeExecutions().get(2);
		assertTrue(execution_RPIP_service.getNodeExecutions().get(3).getNode() instanceof ActivityFinalNode);
		
		// assert requestPatientInfoPage 
		assertEquals(4, execution_requestPatientInfoPages.getNodeExecutions().size());
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(0).getNode() instanceof InitialNode);
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_a = (CallActionExecution)execution_requestPatientInfoPages.getNodeExecutions().get(1);
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(2).getNode() instanceof CallAction);
		CallActionExecution call_b = (CallActionExecution)execution_requestPatientInfoPages.getNodeExecutions().get(2);
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(3).getNode() instanceof ActivityFinalNode);
		
		// assert A
		assertEquals(0, execution_a.getNodeExecutions().size());
		
		// assert B
		assertEquals(0, execution_b.getNodeExecutions().size());
		
		// assert execution hierarchy
		assertEquals(null, execution_main.getCaller());
		
		assertEquals(execution_RPIP_service, call_RPIP_service.getCallee());
		assertEquals(call_RPIP_service, execution_RPIP_service.getCaller());
		
		assertEquals(execution_requestPatientInfoPages, call_requestPatientInfoPages.getCallee());
		assertEquals(call_requestPatientInfoPages, execution_requestPatientInfoPages.getCaller());
		
		assertEquals(execution_a, call_a.getCallee());
		assertEquals(call_a, execution_a.getCaller());
		
		assertEquals(execution_b, call_b.getCallee());
		assertEquals(call_b, execution_b.getCaller());
	}

	@Test
	public void executeEHSExample() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor("model/ehs.di");
		Trace trace = executor.executeActivity("eHS::main"); //$NON-NLS-1$
		
		assertEquals(15, trace.getActivityExecutions().size());
		
		ActivityExecution execution_main = trace.getActivityExecutions().get(0);
		assertEquals("eHS::main", execution_main.getActivity().name);		
		ActivityExecution execution_rpd = trace.getActivityExecutions().get(1);
		assertEquals("Client::RPD", execution_rpd.getActivity().name);
		ActivityExecution execution_rpd_service = trace.getActivityExecutions().get(2);
		assertEquals("ApplicationServer::RPD_service", execution_rpd_service.getActivity().name);
		ActivityExecution execution_login = trace.getActivityExecutions().get(3);
		assertEquals("Database::login", execution_login.getActivity().name);
		ActivityExecution execution_sendResultsLogin = trace.getActivityExecutions().get(4);
		assertEquals("elaborate_send_results", execution_sendResultsLogin.getActivity().name);
		ActivityExecution execution_getPatientData = trace.getActivityExecutions().get(5);
		assertEquals("Database::getPatientData", execution_getPatientData.getActivity().name);
		ActivityExecution execution_sendResultsGetPatientData = trace.getActivityExecutions().get(6);
		assertEquals("elaborate_send_results", execution_sendResultsGetPatientData.getActivity().name);
		ActivityExecution execution_getMedicalHistories = trace.getActivityExecutions().get(7);
		assertEquals("Database::getMedicalHistories", execution_getMedicalHistories.getActivity().name);
		ActivityExecution execution_sendResultsGetMedicalHistories = trace.getActivityExecutions().get(8);
		assertEquals("elaborate_send_results", execution_sendResultsGetMedicalHistories.getActivity().name);
		ActivityExecution execution_getXrayImages = trace.getActivityExecutions().get(9);
		assertEquals("ImageDatabase::getXrayImages", execution_getXrayImages.getActivity().name);
		ActivityExecution execution_sendResultsGetXrayImages = trace.getActivityExecutions().get(10);
		assertEquals("elaborate_send_results", execution_sendResultsGetXrayImages.getActivity().name);
		ActivityExecution execution_getDiseaseImages = trace.getActivityExecutions().get(11);
		assertEquals("ImageDatabase::getDiseaseImages", execution_getDiseaseImages.getActivity().name);
		ActivityExecution execution_sendResultsDiseaseImages = trace.getActivityExecutions().get(12);
		assertEquals("elaborate_send_results", execution_sendResultsDiseaseImages.getActivity().name);
		ActivityExecution execution_sendResultsRPD_service = trace.getActivityExecutions().get(13);
		assertEquals("elaborate_send_results", execution_sendResultsRPD_service.getActivity().name);
		ActivityExecution execution_displayResultsClient = trace.getActivityExecutions().get(14);
		assertEquals("elaborate_display_results", execution_displayResultsClient.getActivity().name);
		
		assertEquals(11, execution_main.getNodeExecutions().size());
		assertEquals(5, execution_rpd.getNodeExecutions().size());
		assertEquals(13, execution_rpd_service.getNodeExecutions().size());
		assertEquals(0, execution_displayResultsClient.getNodeExecutions().size());
		assertEquals(1, execution_login.getNodeExecutions().size());
		assertEquals(1, execution_getPatientData.getNodeExecutions().size());
		assertEquals(1, execution_getMedicalHistories.getNodeExecutions().size());
		assertEquals(1, execution_getXrayImages.getNodeExecutions().size());
		assertEquals(1, execution_getDiseaseImages.getNodeExecutions().size());
		assertEquals(0, execution_sendResultsLogin.getNodeExecutions().size());
		assertEquals(0, execution_sendResultsGetPatientData.getNodeExecutions().size());
		assertEquals(0, execution_sendResultsGetMedicalHistories.getNodeExecutions().size());
		assertEquals(0, execution_sendResultsGetXrayImages.getNodeExecutions().size());
		assertEquals(0, execution_sendResultsDiseaseImages.getNodeExecutions().size());
		assertEquals(0, execution_sendResultsRPD_service.getNodeExecutions().size());
		
		// assert call hierarchy
		assertTrue(execution_main.getNodeExecutions().get(10).getNode() instanceof CallAction);
		CallActionExecution call_RPD = (CallActionExecution)execution_main.getNodeExecutions().get(10);
		assertEquals(call_RPD.getCallee(), execution_rpd);
		assertEquals(execution_rpd.getCaller(), call_RPD);
		
		assertTrue(execution_rpd.getNodeExecutions().get(3).getNode() instanceof CallAction);
		CallActionExecution call_RPD_service = (CallActionExecution)execution_rpd.getNodeExecutions().get(3);
		assertEquals(call_RPD_service.getCallee(), execution_rpd_service);
		assertEquals(execution_rpd_service.getCaller(), call_RPD_service);
		
		assertTrue(execution_rpd.getNodeExecutions().get(4).getNode() instanceof CallAction);
		CallActionExecution call_displayResultsClient = (CallActionExecution)execution_rpd.getNodeExecutions().get(4);
		assertEquals(call_displayResultsClient.getCallee(), execution_displayResultsClient);
		assertEquals(execution_displayResultsClient.getCaller(), call_displayResultsClient);
		
		assertTrue(execution_rpd_service.getNodeExecutions().get(5).getNode() instanceof CallAction);
		CallActionExecution call_login = (CallActionExecution)execution_rpd_service.getNodeExecutions().get(5);
		assertEquals(call_login.getCallee(), execution_login);
		assertEquals(execution_login.getCaller(), call_login);
		
		assertTrue(execution_rpd_service.getNodeExecutions().get(6).getNode() instanceof CallAction);
		CallActionExecution call_getPatientData = (CallActionExecution)execution_rpd_service.getNodeExecutions().get(6);
		assertEquals(call_getPatientData.getCallee(), execution_getPatientData);
		assertEquals(execution_getPatientData.getCaller(), call_getPatientData);
		
		assertTrue(execution_rpd_service.getNodeExecutions().get(7).getNode() instanceof CallAction);
		CallActionExecution call_getMedicalHistories = (CallActionExecution)execution_rpd_service.getNodeExecutions().get(7);
		assertEquals(call_getMedicalHistories.getCallee(), execution_getMedicalHistories);
		assertEquals(execution_getMedicalHistories.getCaller(), call_getMedicalHistories);
		
		assertTrue(execution_rpd_service.getNodeExecutions().get(10).getNode() instanceof CallAction);
		CallActionExecution call_getXrayImages = (CallActionExecution)execution_rpd_service.getNodeExecutions().get(10);
		assertEquals(call_getXrayImages.getCallee(), execution_getXrayImages);
		assertEquals(execution_getXrayImages.getCaller(), call_getXrayImages);
		
		assertTrue(execution_rpd_service.getNodeExecutions().get(11).getNode() instanceof CallAction);
		CallActionExecution call_getDiseaseImages = (CallActionExecution)execution_rpd_service.getNodeExecutions().get(11);
		assertEquals(call_getDiseaseImages.getCallee(), execution_getDiseaseImages);
		assertEquals(execution_getDiseaseImages.getCaller(), call_getDiseaseImages);
		
		assertTrue(execution_login.getNodeExecutions().get(0).getNode() instanceof CallAction);
		CallActionExecution call_sendResultsLogin = (CallActionExecution)execution_login.getNodeExecutions().get(0);
		assertEquals(call_sendResultsLogin.getCallee(), execution_sendResultsLogin);
		assertEquals(execution_sendResultsLogin.getCaller(), call_sendResultsLogin);
				
		assertTrue(execution_getPatientData.getNodeExecutions().get(0).getNode() instanceof CallAction);
		CallActionExecution call_sendResultsGetPatientData = (CallActionExecution)execution_getPatientData.getNodeExecutions().get(0);
		assertEquals(call_sendResultsGetPatientData.getCallee(), execution_sendResultsGetPatientData);
		assertEquals(execution_sendResultsGetPatientData.getCaller(), call_sendResultsGetPatientData);
		
		assertTrue(execution_getMedicalHistories.getNodeExecutions().get(0).getNode() instanceof CallAction);
		CallActionExecution call_sendResultsGetMedicalHistories = (CallActionExecution)execution_getMedicalHistories.getNodeExecutions().get(0);
		assertEquals(call_sendResultsGetMedicalHistories.getCallee(), execution_sendResultsGetMedicalHistories);
		assertEquals(execution_sendResultsGetMedicalHistories.getCaller(), call_sendResultsGetMedicalHistories);
		
		assertTrue(execution_getXrayImages.getNodeExecutions().get(0).getNode() instanceof CallAction);
		CallActionExecution call_sendResultsGetXrayImages = (CallActionExecution)execution_getXrayImages.getNodeExecutions().get(0);
		assertEquals(call_sendResultsGetXrayImages.getCallee(), execution_sendResultsGetXrayImages);
		assertEquals(execution_sendResultsGetXrayImages.getCaller(), call_sendResultsGetXrayImages);
		
		assertTrue(execution_getDiseaseImages.getNodeExecutions().get(0).getNode() instanceof CallAction);
		CallActionExecution call_sendResultsGetDiseaseImages = (CallActionExecution)execution_getDiseaseImages.getNodeExecutions().get(0);
		assertEquals(call_sendResultsGetDiseaseImages.getCallee(), execution_sendResultsDiseaseImages);
		assertEquals(execution_sendResultsDiseaseImages.getCaller(), call_sendResultsGetDiseaseImages);
		
		assertTrue(execution_rpd_service.getNodeExecutions().get(12).getNode() instanceof CallAction);
		CallActionExecution call_sendResultsRPD_service = (CallActionExecution)execution_rpd_service.getNodeExecutions().get(12);
		assertEquals(call_sendResultsRPD_service.getCallee(), execution_sendResultsRPD_service);
		assertEquals(execution_sendResultsRPD_service.getCaller(), call_sendResultsRPD_service);		
	}

}
