/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 * Tanja Mayerhofer - implementation
 */
package org.modelexecution.fuml.nfr.debug.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.modelexecution.fuml.nfr.debug.internal.process.InternalNFRProcess;
import org.modelexecution.fuml.nfr.debug.internal.process.InternalNFRQNProcess;
import org.modelexecution.fuml.nfr.debug.logger.ConsoleLogger;

public class NFRQNProcess extends PlatformObject implements IProcess {

	private ILaunch launch;
	private InternalNFRQNProcess internalProcess;
	private String name;
	@SuppressWarnings("rawtypes")
	private Map attributes;

	private ConsoleLogger consoleLogger = new ConsoleLogger();

	public NFRQNProcess(ILaunch launch, Process process, String name,
			@SuppressWarnings("rawtypes") Map attributes) {
		setFields(launch, process, name, attributes);
		if (!isInDebugMode()) {
			runProcess();
			printAnalysisResult();
			fireTerminateEvent();
		}
	}
	
	private void printAnalysisResult() {
		try {
			consoleLogger.write("QN process ready");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setFields(ILaunch launch, Process process, String name,
			@SuppressWarnings("rawtypes") Map attributes) {
		this.launch = launch;
		assertNFRQNProcess(process);
		this.internalProcess = (InternalNFRQNProcess) process;
		launch.addProcess(this);
		this.name = name;
		this.attributes = attributes;
	}

	private void assertNFRQNProcess(Process process) {
		Assert.isTrue(process instanceof InternalNFRQNProcess);
	}

	private boolean isInDebugMode() {
		return ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode());
	}

	public void runProcess() {
		this.internalProcess.run();
	}

	@Override
	public boolean canTerminate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTerminated() {
		// TODO return internalProcess.isTerminated();
		return false;
	}

	@Override
	public void terminate() throws DebugException {
		internalProcess.destroy();
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		return consoleLogger;
	}

	@Override
	public int getExitValue() throws DebugException {
		return InternalNFRProcess.EXIT_VALUE;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setAttribute(String key, String value) {
		if (attributes == null) {
			attributes = new HashMap(5);
		}
		Object origVal = attributes.get(key);
		if (origVal != null && origVal.equals(value)) {
			return; // nothing changed.
		}

		attributes.put(key, value);
		fireChangeEvent();
	}

	@Override
	public String getAttribute(String key) {
		if (attributes == null) {
			return null;
		}
		return (String) attributes.get(key);
	}

	protected void fireEvent(DebugEvent event) {
		DebugPlugin manager = DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[] { event });
		}
	}

	protected void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	protected void fireSuspendEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND));
	}

	protected void fireChangeEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.equals(IProcess.class)) {
			return this;
		}
		if (adapter.equals(IDebugTarget.class)) {
			return null;
		}
		if (adapter.equals(ILaunch.class)) {
			return getLaunch();
		}
		// CONTEXTLAUNCHING
		if (adapter.equals(ILaunchConfiguration.class)) {
			return getLaunch().getLaunchConfiguration();
		}
		return super.getAdapter(adapter);
	}

}
