package org.modelexecution.fuml.nfr.qn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;

public class MarteUtil {
	
	@SuppressWarnings("unchecked")
	public static Class<? extends EObject> getClass(String qualifiedName) {
		try {
			return (Class<? extends EObject>) Class.forName("org.eclipse.papyrus." + qualifiedName.replace("::", "."));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public static String getString(Class<? extends EObject> clazz) {
		return clazz.getName().replace("org.eclipse.papyrus.", "").replace(".", "::");
	}
	
	/***
	 * Retrieves all stereotype applications from the given uml element which 
	 * are (sub-)classes of the given class.
	 * 
	 * @param umlElement
	 * @param clazz
	 * @return
	 */
	public static <T extends EObject> List<T> getStereotypes(Element umlElement, java.lang.Class<T> clazz) {
		List<T> stereotypeApplications = new ArrayList<T>();
		for(EObject stereotypeApplication : umlElement.getStereotypeApplications())
			if(clazz.isInstance(stereotypeApplication))
				stereotypeApplications.add(clazz.cast(stereotypeApplication));
		return stereotypeApplications;
	}
	
	/***
	 * Retrieves the first stereotype applications from the given uml element which 
	 * is a (sub-)class of the given class.
	 * 
	 * @param umlElement
	 * @param clazz
	 * @return
	 */
	public static <T extends EObject> T getFirstStereotype(Element umlElement, java.lang.Class<T> clazz) {
		if(umlElement == null)
			return null;
		for(EObject stereotypeApplication : umlElement.getStereotypeApplications())
			if(clazz.isInstance(stereotypeApplication))
				return clazz.cast(stereotypeApplication);
		return null;
	}
	

	public static <T extends EObject> T getExactStereotype(Element umlElement, java.lang.Class<T> clazz) {
		Stereotype appliedStereotype = null;
		String stereotype = getString(clazz);
		
		try {
			// this throws an exception the first time it is called?!?! *sigh*
			appliedStereotype = umlElement.getAppliedStereotype(stereotype);
		} catch (Exception e) {
			appliedStereotype = umlElement.getAppliedStereotype(stereotype);
		}
		return appliedStereotype != null ? 
				clazz.cast(umlElement.getStereotypeApplication(appliedStereotype)) :
				null;
	}
	
	public static <T extends EObject> T setOrUpdateFeature(NamedElement umlElement, Class<T> clazz, String featureName, Object value) {
		T appliedStereotype = MarteUtil.getExactStereotype(umlElement, clazz);
		
		if(appliedStereotype == null) {
			// apply stereotype
			Stereotype newStereotype = umlElement.getApplicableStereotype(MarteUtil.getString(clazz));
			if(newStereotype == null)
				// stereotype is not applicable
				return null;
			umlElement.applyStereotype(newStereotype);
			umlElement.setValue(newStereotype, featureName, value);
		} else {
			appliedStereotype.eSet(appliedStereotype.eClass().getEStructuralFeature(featureName), value);
		}
		
		return MarteUtil.getExactStereotype(umlElement, clazz);
	}
	
	public static <T extends EObject> void setFeature(T appliedStereotype, String featureName, Object value) {
		appliedStereotype.eSet(appliedStereotype.eClass().getEStructuralFeature(featureName), value);
	}
	
	public static double extractDoubleFromString(String text) {
		return Double.parseDouble(text.replaceAll("(\\D*)(\\d*)(\\.?)(\\d*)(\\D*)", "$2$3$4"));
	}
}
