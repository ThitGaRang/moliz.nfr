package org.modelexecution.fuml.nfr.qn;

import java.io.File;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.papyrus.infra.core.sashwindows.di.PageList;
import org.eclipse.papyrus.infra.core.sashwindows.di.SashWindowsMngr;
import org.eclipse.papyrus.infra.core.sashwindows.di.util.DiResourceFactoryImpl;
import org.eclipse.uml2.uml.NamedElement;
import org.modelexecution.fuml.convert.ConverterRegistry;
import org.modelexecution.fuml.convert.IConversionResult;
import org.modelexecution.fuml.convert.IConverter;
import org.modelexecution.fumldebug.papyrus.util.DiResourceUtil;

public class PapyrusModelLoader {
	
	private static final ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
	private static final String PLATFORM_RESOURCE = "platform:/resource";

	private String modelPath;
	private ResourceSet resourceSet;
	private Resource diResource;

	private IConversionResult conversionResult;

	public PapyrusModelLoader() {
		resourceSet = createResourceSet();
	}
	
	public PapyrusModelLoader setModel(String modelPath) {
		this.modelPath = modelPath;
		return this;
	}
	
	protected ResourceSet createResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("di", new DiResourceFactoryImpl()); //$NON-NLS-1$
		return resourceSet;
	}

	public PapyrusModelLoader loadModel() {
		if(diResource != null)
			return this;
		
		if(modelPath.contains(PLATFORM_RESOURCE))
			diResource = resourceSet.getResource(getResourceURI(modelPath), true);
		else
			diResource = resourceSet.getResource(getFileURI(modelPath), true);
		return this;
	}

	private URI getResourceURI(String path) {
		return URI.createPlatformResourceURI(path.replace(PLATFORM_RESOURCE, ""), true);
	}

	private URI getFileURI(String path) {
		return URI.createFileURI(new File(path).getAbsolutePath());
	}
	
	private IConverter getConverter(NamedElement namedElement) {
		return converterRegistry.getConverter(namedElement);
	}

	private IConversionResult convertDiResource() {
		NamedElement namedElement = obtainFirstNamedElement();
		IConverter converter = getConverter(namedElement);
		return converter.convert(namedElement);
	}

	public NamedElement obtainFirstNamedElement() {
		SashWindowsMngr sashWindowMngr = DiResourceUtil.obtainSashWindowMngr(diResource);
		PageList pageList = sashWindowMngr.getPageList();
		return DiResourceUtil.obtainFirstNamedElement(pageList);
	}

	public String getModelPath() {
		return this.modelPath;
	}
	
	public Resource getDiModelResource() {
		return diResource;
	}

	public IConversionResult getConversionResult() {
		if(conversionResult == null)
			conversionResult = convertDiResource();
		return conversionResult;
	}
}
