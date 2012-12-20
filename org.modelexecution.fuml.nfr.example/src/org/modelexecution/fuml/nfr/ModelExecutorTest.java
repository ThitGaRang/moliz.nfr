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

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.papyrus.infra.core.sashwindows.di.PageList;
import org.eclipse.papyrus.infra.core.sashwindows.di.SashWindowsMngr;
import org.eclipse.papyrus.infra.core.sashwindows.di.util.DiResourceFactoryImpl;
import org.eclipse.uml2.uml.NamedElement;
import org.junit.Test;
import org.modelexecution.fuml.convert.ConverterRegistry;
import org.modelexecution.fuml.convert.IConversionResult;
import org.modelexecution.fuml.convert.IConverter;
import org.modelexecution.fumldebug.core.ExecutionContext;
import org.modelexecution.fumldebug.core.ExecutionEventListener;
import org.modelexecution.fumldebug.core.event.ActivityEntryEvent;
import org.modelexecution.fumldebug.core.event.Event;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.CallActionExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.Trace;
import org.modelexecution.fumldebug.papyrus.util.DiResourceUtil;

import fUML.Semantics.CommonBehaviors.BasicBehaviors.ParameterValueList;
import fUML.Syntax.Actions.BasicActions.CallAction;
import fUML.Syntax.Actions.IntermediateActions.AddStructuralFeatureValueAction;
import fUML.Syntax.Actions.IntermediateActions.CreateObjectAction;
import fUML.Syntax.Actions.IntermediateActions.ReadSelfAction;
import fUML.Syntax.Actions.IntermediateActions.ReadStructuralFeatureAction;
import fUML.Syntax.Activities.IntermediateActivities.Activity;
import fUML.Syntax.Activities.IntermediateActivities.InitialNode;

public class ModelExecutorTest {

	private int executionID = -1;
	
	/** The reference to the converter. */
	private static final ConverterRegistry converterRegistry = ConverterRegistry
			.getInstance();

	/** The resource set to be used for loading the model resource. */
	private ResourceSet resourceSet;

	/** The current di resource. */
	private Resource diResource;

	public ModelExecutorTest() {
		initializeResourceSet();
	}

	/**
	 * Initializes the resource set used by this class.
	 * 
	 * Basically, we may use a default {@link ResourceSetImpl}, but we set the
	 * papyrus-specific resource set factory {@link DiResourceFactoryImpl} for
	 * the file extension &quot;di&quot;.
	 */
	private void initializeResourceSet() {
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("di", new DiResourceFactoryImpl()); //$NON-NLS-1$
	}

	/**
	 * Loads, converts, and executes the {@link Activity} called
	 * &quot;CallBehaviorAD&quot; in the file &quot;models/PersonCD.di&quot;.
	 */
	@Test
	public void executeCallBehaviorActivity() {
		loadModel("model/simple001.di"); //$NON-NLS-1$
		Trace trace = executeActivity("main"); //$NON-NLS-1$
		
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

	/**
	 * Load the model located at the specified {@code path}.
	 * 
	 * @param path
	 *            the path of the model to be loaded.
	 */
	private void loadModel(String path) {
		diResource = resourceSet.getResource(getFileURI(path), true);
	}

	/**
	 * Returns a {@link URI} from the specified {@code path}.
	 * 
	 * @param path
	 *            path to get {@link URI} for.
	 * @return the {@link URI} representing the specified {@code path}.
	 */
	private URI getFileURI(String path) {
		return URI.createFileURI(new File(path).getAbsolutePath());
	}

	/**
	 * Executes the {@link Activity} with the specified {@code name}, which is
	 * contained in the currently loaded {@link #diResource}.
	 * 
	 * @param name
	 *            name of the activity to be loaded.
	 */
	public Trace executeActivity(String name) {
		IConversionResult conversionResult = convertDiResource();
		Activity activity = conversionResult.getActivity(name);
		return executeActivity(activity);
	}

	/**
	 * Converts the currently loaded {@link #diResource} into the fUML object
	 * representation.
	 * 
	 * @return the result of the conversion in terms of a
	 *         {@link IConversionResult}.
	 */
	private IConversionResult convertDiResource() {
		NamedElement namedElement = obtainFirstNamedElement();
		IConverter converter = getConverter(namedElement);
		return converter.convert(namedElement);
	}

	/**
	 * Obtains the first named element that is found in the {@link #diResource}.
	 * 
	 * Therefore, we first obtain the {@link SashWindowsMngr} of the di
	 * resource, read the contained page list, and obtain the first named
	 * element from the list. For these methods, we may use the
	 * {@link DiResourceUtil}.
	 * 
	 * @return the first {@link NamedElement} of the resource currently loaded.
	 */
	private NamedElement obtainFirstNamedElement() {
		SashWindowsMngr sashWindowMngr = DiResourceUtil
				.obtainSashWindowMngr(diResource);
		PageList pageList = sashWindowMngr.getPageList();
		return DiResourceUtil.obtainFirstNamedElement(pageList);
	}

	/**
	 * Obtains a converter for the specified {@code namedElement} from the
	 * converter registry.
	 * 
	 * @param namedElement
	 *            to get the converter for.
	 * @return the obtained {@link IConverter}.
	 */
	private IConverter getConverter(NamedElement namedElement) {
		return converterRegistry.getConverter(namedElement);
	}

	/**
	 * Executes the specified {@code activity}.
	 */
	private Trace executeActivity(Activity activity) {
		// register an anonymous event listener that prints the events
		// to system.out directly and calls resume after each step event.
		getExecutionContext().getExecutionEventProvider().addEventListener(
				new ExecutionEventListener() {
					@Override
					public void notify(Event event) {
						System.out.println(event);
						if(executionID == -1) {
							if (event instanceof ActivityEntryEvent) {
								ActivityEntryEvent activityEntryEvent = (ActivityEntryEvent) event;
								executionID = activityEntryEvent.getActivityExecutionID();
							}
						}
					}
				});

		// start the execution
		getExecutionContext().execute(activity, null, new ParameterValueList());
		
		Trace trace = getExecutionContext().getTrace(executionID);		
		executionID = -1;
		
		return trace;
	}

	/**
	 * Obtains the singleton {@link ExecutionContext}.
	 * 
	 * @return the {@link ExecutionContext}.
	 */
	private ExecutionContext getExecutionContext() {
		return ExecutionContext.getInstance();
	}

}
