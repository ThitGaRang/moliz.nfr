package org.modelexecution.fuml.nfr;
/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 */


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
import fUML.Syntax.Activities.IntermediateActivities.InitialNode;

public class ModelExecutorTest {

	@Test
	public void executeCallBehaviorActivity() {
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
		assertEquals(3, execution_RPIP_service.getNodeExecutions().size());
		assertTrue(execution_RPIP_service.getNodeExecutions().get(0).getNode() instanceof ReadSelfAction);
		assertTrue(execution_RPIP_service.getNodeExecutions().get(1).getNode() instanceof ReadStructuralFeatureAction);
		assertTrue(execution_RPIP_service.getNodeExecutions().get(2).getNode() instanceof CallAction);
		CallActionExecution call_requestPatientInfoPages = (CallActionExecution)execution_RPIP_service.getNodeExecutions().get(2);
		
		// assert requestPatientInfoPage 
		assertEquals(3, execution_requestPatientInfoPages.getNodeExecutions().size());
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(0).getNode() instanceof InitialNode);
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(1).getNode() instanceof CallAction);
		CallActionExecution call_a = (CallActionExecution)execution_requestPatientInfoPages.getNodeExecutions().get(1);
		assertTrue(execution_requestPatientInfoPages.getNodeExecutions().get(2).getNode() instanceof CallAction);
		CallActionExecution call_b = (CallActionExecution)execution_requestPatientInfoPages.getNodeExecutions().get(2);
		
		// assert A
		assertEquals(1, execution_a.getNodeExecutions().size());
		assertTrue(execution_a.getNodeExecutions().get(0).getNode() instanceof InitialNode);
		
		// assert B
		assertEquals(1, execution_b.getNodeExecutions().size());
		assertTrue(execution_b.getNodeExecutions().get(0).getNode() instanceof InitialNode);
		
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

}
