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
package org.modelexecution.fuml.nfr.debug;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NFRDebugPlugin extends AbstractUIPlugin {

	public static final String ATT_MODEL_PATH = "ATT_MODEL_PATH"; //$NON-NLS-1$
	public static final String ATT_MAIN_ACTIVITY_NAME = "ATT_MAIN_ACTIVITY_NAME"; //$NON-NLS-1$
	public static final String PROCESS_FACTORY_ID = "org.modelexecution.fuml.nfr.debug.processFactory"; //$NON-NLS-1$
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.modelexecution.fuml.nfr.debug"; //$NON-NLS-1$

	// The shared instance
	private static NFRDebugPlugin plugin;
	
	/**
	 * The constructor
	 */
	public NFRDebugPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static NFRDebugPlugin getDefault() {
		return plugin;
	}

}
