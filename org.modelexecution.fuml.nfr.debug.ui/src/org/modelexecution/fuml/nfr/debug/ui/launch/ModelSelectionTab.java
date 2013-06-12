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
package org.modelexecution.fuml.nfr.debug.ui.launch;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.papyrus.infra.core.sashwindows.di.PageList;
import org.eclipse.papyrus.infra.core.sashwindows.di.SashWindowsMngr;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.NamedElement;
import org.modelexecution.fuml.nfr.debug.NFRDebugPlugin;
import org.modelexecution.fumldebug.papyrus.util.DiResourceUtil;

public class ModelSelectionTab extends AbstractLaunchConfigurationTab {

	private static final String DI = "di";
	private static final String PLATFORM_RESOURCE = "platform:/resource";

	protected Button browseWorkspaceButton;
	protected Text uriText;
	protected Button loadButton;
	private Label mainActivityLabel;
	private TreeViewer modelTreeViewer;

	private ResourceSet resourceSet;
	private Resource diResource;
	private NamedElement rootModelElement;
	private Activity mainActivity;

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH
				| GridData.GRAB_VERTICAL));

		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 8;
		composite.setLayout(layout);

		createURIControl(composite);
		setControl(composite);
	}

	protected void createURIControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL));
		{
			FormLayout layout = new FormLayout();
			layout.marginTop = 10;
			layout.spacing = 10;
			composite.setLayout(layout);
		}

		Label uriLabel = new Label(composite, SWT.LEFT);
		{
			FormData data = new FormData();
			data.left = new FormAttachment(0);
			uriLabel.setLayoutData(data);
		}
		uriLabel.setText("");

		Composite uriComposite = new Composite(composite, SWT.NONE);
		{
			FormData data = new FormData();
			data.top = new FormAttachment(uriLabel, 5);
			data.left = new FormAttachment(0);
			data.right = new FormAttachment(100);
			uriComposite.setLayoutData(data);

			GridLayout layout = new GridLayout(2, false);
			layout.marginTop = -5;
			layout.marginLeft = -5;
			layout.marginRight = -5;
			uriComposite.setLayout(layout);
		}

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		{
			FormData data = new FormData();
			data.top = new FormAttachment(uriLabel, 0, SWT.CENTER);
			data.left = new FormAttachment(uriLabel, 0);
			data.right = new FormAttachment(100);
			buttonComposite.setLayoutData(data);

			FormLayout layout = new FormLayout();
			layout.marginTop = 0;
			layout.marginBottom = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.spacing = 5;
			buttonComposite.setLayout(layout);
		}

		browseWorkspaceButton = new Button(buttonComposite, SWT.PUSH);
		browseWorkspaceButton.setText("Browse Workspace");
		browseWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseWorkspace();
			}
		});
		browseWorkspaceButton.setFocus();

		{
			FormData data = new FormData();
			data.right = new FormAttachment(100);
			browseWorkspaceButton.setLayoutData(data);
		}

		uriText = new Text(uriComposite, SWT.SINGLE | SWT.BORDER);
		setURIText("");
		if (uriText.getText().length() > 0) {
			uriText.selectAll();
		}
		uriText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				uriTextModified(uriText.getText().trim());
			}
		});

		loadButton = new Button(uriComposite, SWT.PUSH);
		loadButton.setText("Load");
		loadButton.setLayoutData(new GridData(GridData.END));
		loadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadModel();
			}
		});

		{
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL
					| GridData.GRAB_HORIZONTAL);
			if (uriComposite.getChildren().length == 1) {
				gridData.horizontalSpan = 2;
			}
			uriText.setLayoutData(gridData);
		}

		mainActivityLabel = new Label(parent, SWT.LEFT);
		mainActivityLabel.setText("Select main activity");
		mainActivityLabel.setVisible(false);
		modelTreeViewer = new TreeViewer(parent);
		GridData treeLayoutData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		treeLayoutData.heightHint = 400;
		modelTreeViewer.getTree().setLayoutData(treeLayoutData);
		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		adapterFactory
				.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory
				.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		modelTreeViewer.setContentProvider(new AdapterFactoryContentProvider(
				adapterFactory));
		modelTreeViewer.setLabelProvider(new AdapterFactoryLabelProvider(
				adapterFactory));
		modelTreeViewer.getTree().setEnabled(false);
		modelTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						String text = uriText.getText();
						loadButton.setEnabled(text != null
								&& text.trim().length() > 0);
						loadActivityFromSelection(event.getSelection());
						updateLaunchConfigurationDialog();
					}
				});
	}

	protected void loadActivityFromSelection(ISelection selection) {
		mainActivity = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			structuredSelection.size();
			for (Iterator<?> iter = structuredSelection.iterator(); iter
					.hasNext();) {
				Object next = iter.next();
				if (next instanceof Activity) {
					mainActivity = (Activity) next;
				}
			}
		}
	}

	private void updateEClassTreeViewer() {
		if (haveModel()) {
			modelTreeViewer.getTree().setEnabled(true);
			modelTreeViewer.setInput(rootModelElement);
			modelTreeViewer.refresh(true);
			modelTreeViewer.getTree().setVisible(true);
			mainActivityLabel.setVisible(true);
		} else {
			modelTreeViewer.getTree().setEnabled(false);
			modelTreeViewer.getTree().setVisible(false);
			mainActivityLabel.setVisible(false);
		}
	}

	private void uriTextModified(String text) {
		setErrorMessage(null);
		setMessage(null);
	}

	private boolean browseWorkspace() {
		ViewerFilter extensionFilter = null;
		extensionFilter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				return !(element instanceof IFile)
						|| DI.equals(((IFile) element).getFileExtension());
			}
		};

		IFile[] files = WorkspaceResourceDialog.openFileSelection(getShell(),
				null, null, false, null, extensionFilter == null ? null
						: Collections.singletonList(extensionFilter));
		if (files.length > 0) {
			StringBuffer text = new StringBuffer();
			for (int i = 0; i < files.length; ++i) {
				text.append(URI.createPlatformResourceURI(files[i]
						.getFullPath().toString(), true));
				text.append("  ");
			}
			setURIText(URI.decode(text.toString()));
			return true;
		}
		return false;
	}

	private void setURIText(String uri) {
		uri = uri.trim();
		StringBuffer text = new StringBuffer(uriText.getText());
		if (!uri.equals(text)) {
			uriText.setText(uri.trim());
		}
	}

	protected boolean loadModel() {
		if (uriText.getText().startsWith("platform:/")) {
			resourceSet = new ResourceSetImpl();
			diResource = resourceSet.getResource(URI.createPlatformResourceURI(
					uriText.getText().replace(PLATFORM_RESOURCE, ""), true),
					true);
			SashWindowsMngr sashWindowMngr = DiResourceUtil
					.obtainSashWindowMngr(diResource);
			PageList pageList = sashWindowMngr.getPageList();
			rootModelElement = DiResourceUtil.obtainFirstNamedElement(pageList);
		}
		updateEClassTreeViewer();
		return haveModel();
	}

	private boolean haveModel() {
		return rootModelElement != null;
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (!haveModel()) {
			setErrorMessage("Select a model resource.");
			return false;
		} else if (!isMainActivitySelected()) {
			setErrorMessage("Selected a main activity.");
			return false;
		} else {
			setErrorMessage(null);
			setMessage(null);
			return super.isValid(launchConfig);
		}
	}

	private boolean isMainActivitySelected() {
		return mainActivity != null;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID,
				NFRDebugPlugin.PROCESS_FACTORY_ID);
		configuration.setAttribute(NFRDebugPlugin.ATT_MODEL_PATH, uriText
				.getText().trim());
		configuration.setAttribute(NFRDebugPlugin.ATT_MAIN_ACTIVITY_NAME,
				getMainActivityName());
	}

	private String getMainActivityName() {
		if (mainActivity == null)
			return "";
		return mainActivity.getQualifiedName();
	}

	@Override
	public String getName() {
		return "Model Resource";
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String modelResource = "";
		String activityName = "";

		try {
			modelResource = configuration.getAttribute(
					NFRDebugPlugin.ATT_MODEL_PATH, "");
			activityName = configuration.getAttribute(
					NFRDebugPlugin.ATT_MAIN_ACTIVITY_NAME, "");
		} catch (CoreException e) {
		}

		uriText.setText(modelResource);
		if (loadModel()) {
			TreeIterator<EObject> iterator = rootModelElement.eAllContents();
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();
				if (eObject instanceof Activity) {
					Activity activity = (Activity) eObject;
					if (activity.getQualifiedName().equals(activityName)) {
						mainActivity = activity;
						break;
					}
				}
			}
		}
		if (mainActivity != null) {
			ISelection selection = new StructuredSelection(mainActivity);
			modelTreeViewer.setSelection(selection);
		}
	}

}
