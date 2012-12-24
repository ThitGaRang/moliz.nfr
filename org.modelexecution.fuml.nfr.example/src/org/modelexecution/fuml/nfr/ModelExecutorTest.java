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
		PapyrusModelExecutor executor = new PapyrusModelExecutor("model/eHS.di");
		Trace trace = executor.executeActivity("main"); //$NON-NLS-1$
		
		assertEquals(18, trace.getActivityExecutions().size());
		
		ActivityExecution execution_main = trace.getActivityExecutions().get(0);
		assertEquals("main", execution_main.getActivity().name);		
		ActivityExecution execution_invokeServices = trace.getActivityExecutions().get(1);
		assertEquals("invokeServices", execution_invokeServices.getActivity().name);
		ActivityExecution execution_requestPatientInfoPages = trace.getActivityExecutions().get(2);
		assertEquals("requestPatientInfoPages", execution_requestPatientInfoPages.getActivity().name);
		ActivityExecution execution_getPatientDataAppserver = trace.getActivityExecutions().get(3);
		assertEquals("getPatientData", execution_getPatientDataAppserver.getActivity().name);
		ActivityExecution execution_loginInteraction = trace.getActivityExecutions().get(4);
		assertEquals("loginInteraction", execution_loginInteraction.getActivity().name);
		ActivityExecution execution_sendResults1 = trace.getActivityExecutions().get(5);
		assertEquals("sendResults", execution_sendResults1.getActivity().name);
		ActivityExecution execution_getPatientDataDB = trace.getActivityExecutions().get(6);
		assertEquals("getPatientData", execution_getPatientDataDB.getActivity().name);
		ActivityExecution execution_sendResults2 = trace.getActivityExecutions().get(7);
		assertEquals("sendResults", execution_sendResults2.getActivity().name);
		ActivityExecution execution_getMedicalHistory = trace.getActivityExecutions().get(8);
		assertEquals("getMedicalHistory", execution_getMedicalHistory.getActivity().name);
		ActivityExecution execution_sendResults3 = trace.getActivityExecutions().get(9);
		assertEquals("sendResults", execution_sendResults3.getActivity().name);
		ActivityExecution execution_getDiseaseData = trace.getActivityExecutions().get(10);
		assertEquals("getDiseaseData", execution_getDiseaseData.getActivity().name);
		ActivityExecution execution_sendResults4 = trace.getActivityExecutions().get(11);
		assertEquals("sendResults", execution_sendResults4.getActivity().name);
		ActivityExecution execution_getXrayImages = trace.getActivityExecutions().get(12);
		assertEquals("getXrayImages", execution_getXrayImages.getActivity().name);
		ActivityExecution execution_sendResults5 = trace.getActivityExecutions().get(13);
		assertEquals("sendResults", execution_sendResults5.getActivity().name);
		ActivityExecution execution_getDiseaseImages = trace.getActivityExecutions().get(14);
		assertEquals("getDiseaseImages", execution_getDiseaseImages.getActivity().name);
		ActivityExecution execution_sendResults6 = trace.getActivityExecutions().get(15);
		assertEquals("sendResults", execution_sendResults6.getActivity().name);
		ActivityExecution execution_displayResultsAppServer = trace.getActivityExecutions().get(16);
		assertEquals("displayResults", execution_displayResultsAppServer.getActivity().name);
		ActivityExecution execution_displayResultsClient = trace.getActivityExecutions().get(17);
		assertEquals("displayResults", execution_displayResultsClient.getActivity().name);
		
		assertEquals(10, execution_main.getNodeExecutions().size());
		assertEquals(5, execution_invokeServices.getNodeExecutions().size());
		assertEquals(6, execution_requestPatientInfoPages.getNodeExecutions().size());
		assertEquals(0, execution_displayResultsClient.getNodeExecutions().size());
		assertEquals(15, execution_getPatientDataAppserver.getNodeExecutions().size());
		assertEquals(3, execution_loginInteraction.getNodeExecutions().size());
		assertEquals(3, execution_getPatientDataDB.getNodeExecutions().size());
		assertEquals(3, execution_getMedicalHistory.getNodeExecutions().size());
		assertEquals(3, execution_getDiseaseData.getNodeExecutions().size());
		assertEquals(3, execution_getXrayImages.getNodeExecutions().size());
		assertEquals(3, execution_getDiseaseImages.getNodeExecutions().size());
		assertEquals(0, execution_sendResults1.getNodeExecutions().size());
		assertEquals(0, execution_sendResults2.getNodeExecutions().size());
		assertEquals(0, execution_sendResults3.getNodeExecutions().size());
		assertEquals(0, execution_sendResults4.getNodeExecutions().size());
		assertEquals(0, execution_sendResults5.getNodeExecutions().size());
		assertEquals(0, execution_sendResults6.getNodeExecutions().size());
		assertEquals(0, execution_displayResultsAppServer.getNodeExecutions().size());
		
		// assert call hierarchy
		assertTrue(execution_main.getNodeExecutions().get(9).getNode() instanceof CallAction);
		CallActionExecution call_invokeServices = (CallActionExecution)execution_main.getNodeExecutions().get(9);
		assertEquals(call_invokeServices.getCallee(), execution_invokeServices);
		assertEquals(execution_invokeServices.getCaller(), call_invokeServices);
		
		assertTrue(execution_invokeServices.getNodeExecutions().get(3).getNode() instanceof CallAction);
		CallActionExecution call_requestPatientInfoPages = (CallActionExecution)execution_invokeServices.getNodeExecutions().get(3);
		assertEquals(call_requestPatientInfoPages.getCallee(), execution_requestPatientInfoPages);
		assertEquals(execution_requestPatientInfoPages.getCaller(), call_requestPatientInfoPages);
		
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(3).getNode() instanceof CallAction);
		CallActionExecution call_getPatientDataAppserver = (CallActionExecution)execution_requestPatientInfoPages.getNodeExecutions().get(3);
		assertEquals(call_getPatientDataAppserver.getCallee(), execution_getPatientDataAppserver);
		assertEquals(execution_getPatientDataAppserver.getCaller(), call_getPatientDataAppserver);
		
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(4).getNode() instanceof CallAction);
		CallActionExecution call_displayResultsClient = (CallActionExecution)execution_requestPatientInfoPages.getNodeExecutions().get(4);
		assertEquals(call_displayResultsClient.getCallee(), execution_displayResultsClient);
		assertEquals(execution_displayResultsClient.getCaller(), call_displayResultsClient);
		
		assertTrue(execution_getPatientDataAppserver.getNodeExecutions().get(5).getNode() instanceof CallAction);
		CallActionExecution call_loginInteraction = (CallActionExecution)execution_getPatientDataAppserver.getNodeExecutions().get(5);
		assertEquals(call_loginInteraction.getCallee(), execution_loginInteraction);
		assertEquals(execution_loginInteraction.getCaller(), call_loginInteraction);
		
		assertTrue(execution_getPatientDataAppserver.getNodeExecutions().get(6).getNode() instanceof CallAction);
		CallActionExecution call_getPatientDataDB = (CallActionExecution)execution_getPatientDataAppserver.getNodeExecutions().get(6);
		assertEquals(call_getPatientDataDB.getCallee(), execution_getPatientDataDB);
		assertEquals(execution_getPatientDataDB.getCaller(), call_getPatientDataDB);
		
		assertTrue(execution_getPatientDataAppserver.getNodeExecutions().get(7).getNode() instanceof CallAction);
		CallActionExecution call_getMedicalHistory = (CallActionExecution)execution_getPatientDataAppserver.getNodeExecutions().get(7);
		assertEquals(call_getMedicalHistory.getCallee(), execution_getMedicalHistory);
		assertEquals(execution_getMedicalHistory.getCaller(), call_getMedicalHistory);
		
		assertTrue(execution_getPatientDataAppserver.getNodeExecutions().get(8).getNode() instanceof CallAction);
		CallActionExecution call_getDiseaseData = (CallActionExecution)execution_getPatientDataAppserver.getNodeExecutions().get(8);
		assertEquals(call_getDiseaseData.getCallee(), execution_getDiseaseData);
		assertEquals(execution_getDiseaseData.getCaller(), call_getDiseaseData);
		
		assertTrue(execution_getPatientDataAppserver.getNodeExecutions().get(11).getNode() instanceof CallAction);
		CallActionExecution call_getXrayImages = (CallActionExecution)execution_getPatientDataAppserver.getNodeExecutions().get(11);
		assertEquals(call_getXrayImages.getCallee(), execution_getXrayImages);
		assertEquals(execution_getXrayImages.getCaller(), call_getXrayImages);
		
		assertTrue(execution_getPatientDataAppserver.getNodeExecutions().get(12).getNode() instanceof CallAction);
		CallActionExecution call_getDiseaseImages = (CallActionExecution)execution_getPatientDataAppserver.getNodeExecutions().get(12);
		assertEquals(call_getDiseaseImages.getCallee(), execution_getDiseaseImages);
		assertEquals(execution_getDiseaseImages.getCaller(), call_getDiseaseImages);
		
		assertTrue(execution_getPatientDataAppserver.getNodeExecutions().get(13).getNode() instanceof CallAction);
		CallActionExecution call_getResultsAppserver = (CallActionExecution)execution_getPatientDataAppserver.getNodeExecutions().get(13);
		assertEquals(call_getResultsAppserver.getCallee(), execution_displayResultsAppServer);
		assertEquals(execution_displayResultsAppServer.getCaller(), call_getResultsAppserver);
		
		assertTrue(execution_loginInteraction.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_sendResults1 = (CallActionExecution)execution_loginInteraction.getNodeExecutions().get(1);
		assertEquals(call_sendResults1.getCallee(), execution_sendResults1);
		assertEquals(execution_sendResults1.getCaller(), call_sendResults1);
		
		assertTrue(execution_getPatientDataDB.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_sendResults2 = (CallActionExecution)execution_getPatientDataDB.getNodeExecutions().get(1);
		assertEquals(call_sendResults2.getCallee(), execution_sendResults2);
		assertEquals(execution_sendResults2.getCaller(), call_sendResults2);
		
		assertTrue(execution_getMedicalHistory.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_sendResults3 = (CallActionExecution)execution_getMedicalHistory.getNodeExecutions().get(1);
		assertEquals(call_sendResults3.getCallee(), execution_sendResults3);
		assertEquals(execution_sendResults3.getCaller(), call_sendResults3);
		
		assertTrue(execution_getDiseaseData.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_sendResults4 = (CallActionExecution)execution_getDiseaseData.getNodeExecutions().get(1);
		assertEquals(call_sendResults4.getCallee(), execution_sendResults4);
		assertEquals(execution_sendResults4.getCaller(), call_sendResults4);
		
		assertTrue(execution_getXrayImages.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_sendResults5 = (CallActionExecution)execution_getXrayImages.getNodeExecutions().get(1);
		assertEquals(call_sendResults5.getCallee(), execution_sendResults5);
		assertEquals(execution_sendResults5.getCaller(), call_sendResults5);
		
		assertTrue(execution_getDiseaseImages.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_sendResults6 = (CallActionExecution)execution_getDiseaseImages.getNodeExecutions().get(1);
		assertEquals(call_sendResults6.getCallee(), execution_sendResults6);
		assertEquals(execution_sendResults6.getCaller(), call_sendResults6);
	}

}
