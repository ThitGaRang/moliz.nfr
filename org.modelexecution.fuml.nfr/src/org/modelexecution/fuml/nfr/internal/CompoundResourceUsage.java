/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Langer - initial API and implementation
 */
package org.modelexecution.fuml.nfr.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.Resource;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.ResourceUsage;
import org.modelexecution.fuml.nfr.IResourceUsage;

public class CompoundResourceUsage extends BasicResourceUsage {

	private static final String UNSUPPORTED_VARIABLE = "#unsupported_variable#"; //$NON-NLS-1$
	private static final String TOTAL = VARIABLE_CHAR + "total"; //$NON-NLS-1$

	private Collection<IResourceUsage> subUsages;

	public CompoundResourceUsage(ResourceUsage resourceUsage,
			Collection<IResourceUsage> subUsages) {
		super(resourceUsage);
		this.subUsages = subUsages;
	}

	@Override
	public String getAllocatedMemory(Resource resource) {
		if (isVariable(super.getAllocatedMemory(resource))) {
			Collection<String> subValues = getSubAllocatedMemory(resource);
			return computeVariableValue(super.getAllocatedMemory(resource),
					subValues);
		} else {
			return super.getAllocatedMemory(resource);
		}
	}

	protected Collection<String> getSubAllocatedMemory(Resource resource) {
		Collection<String> subAllocatedMemory = new ArrayList<String>();
		for (IResourceUsage subUsage : getSubUsages()) {
			String subValue = subUsage.getAllocatedMemory(resource);
			if (isDefined(subValue)) {
				subAllocatedMemory.add(subValue);
			}
			if (subUsage instanceof CompoundResourceUsage) {
				CompoundResourceUsage compoundUsage = (CompoundResourceUsage) subUsage;
				subAllocatedMemory.addAll(compoundUsage
						.getSubAllocatedMemory(resource));
			}
		}
		return subAllocatedMemory;
	}

	private boolean isDefined(String value) {
		return value != null && !UNDEFINED.equals(value);
	}

	@Override
	public String getEnergy(Resource resource) {
		if (isVariable(super.getEnergy(resource))) {
			Collection<String> subValues = getSubEnergy(resource);
			return computeVariableValue(super.getEnergy(resource), subValues);
		} else {
			return super.getEnergy(resource);
		}
	}

	protected Collection<String> getSubEnergy(Resource resource) {
		Collection<String> subEnergy = new ArrayList<String>();
		for (IResourceUsage subUsage : getSubUsages()) {
			String subValue = subUsage.getEnergy(resource);
			if (isDefined(subValue)) {
				subEnergy.add(subValue);
			}
			if (subUsage instanceof CompoundResourceUsage) {
				CompoundResourceUsage compoundUsage = (CompoundResourceUsage) subUsage;
				subEnergy.addAll(compoundUsage.getSubEnergy(resource));
			}
		}
		return subEnergy;
	}

	@Override
	public String getExecTime(Resource resource) {
		if (isVariable(super.getExecTime(resource))) {
			Collection<String> subValues = getSubExecTime(resource);
			return computeVariableValue(super.getExecTime(resource), subValues);
		} else {
			return super.getExecTime(resource);
		}
	}

	protected Collection<String> getSubExecTime(Resource resource) {
		Collection<String> subExecTime = new ArrayList<String>();
		for (IResourceUsage subUsage : getSubUsages()) {
			String subValue = subUsage.getExecTime(resource);
			if (isDefined(subValue)) {
				subExecTime.add(subValue);
			}
			if (subUsage instanceof CompoundResourceUsage) {
				CompoundResourceUsage compoundUsage = (CompoundResourceUsage) subUsage;
				subExecTime.addAll(compoundUsage.getSubExecTime(resource));
			}
		}
		return subExecTime;
	}

	@Override
	public String getMsgSize(Resource resource) {
		if (isVariable(super.getMsgSize(resource))) {
			Collection<String> subValues = getSubMsgSize(resource);
			return computeVariableValue(super.getMsgSize(resource), subValues);
		} else {
			return super.getMsgSize(resource);
		}
	}

	protected Collection<String> getSubMsgSize(Resource resource) {
		Collection<String> subMsgSize = new ArrayList<String>();
		for (IResourceUsage subUsage : getSubUsages()) {
			String subValue = subUsage.getMsgSize(resource);
			if (isDefined(subValue)) {
				subMsgSize.add(subValue);
			}
			if (subUsage instanceof CompoundResourceUsage) {
				CompoundResourceUsage compoundUsage = (CompoundResourceUsage) subUsage;
				subMsgSize.addAll(compoundUsage.getSubMsgSize(resource));
			}
		}
		return subMsgSize;
	}

	@Override
	public String getPowerPeak(Resource resource) {
		if (isVariable(super.getPowerPeak(resource))) {
			Collection<String> subValues = getSubPowerPeak(resource);
			return computeVariableValue(super.getPowerPeak(resource), subValues);
		} else {
			return super.getPowerPeak(resource);
		}
	}

	protected Collection<String> getSubPowerPeak(Resource resource) {
		Collection<String> subPowerPeak = new ArrayList<String>();
		for (IResourceUsage subUsage : getSubUsages()) {
			String subValue = subUsage.getPowerPeak(resource);
			if (isDefined(subValue)) {
				subPowerPeak.add(subValue);
			}
			if (subUsage instanceof CompoundResourceUsage) {
				CompoundResourceUsage compoundUsage = (CompoundResourceUsage) subUsage;
				subPowerPeak.addAll(compoundUsage.getSubPowerPeak(resource));
			}
		}
		return subPowerPeak;
	}

	@Override
	public String getUsedMemory(Resource resource) {
		if (isVariable(super.getUsedMemory(resource))) {
			Collection<String> subValues = getSubUsedMemory(resource);
			return computeVariableValue(super.getUsedMemory(resource),
					subValues);
		} else {
			return super.getUsedMemory(resource);
		}
	}

	protected Collection<String> getSubUsedMemory(Resource resource) {
		Collection<String> subUsedMemory = new ArrayList<String>();
		for (IResourceUsage subUsage : getSubUsages()) {
			String subValue = subUsage.getUsedMemory(resource);
			if (isDefined(subValue)) {
				subUsedMemory.add(subValue);
			}
			if (subUsage instanceof CompoundResourceUsage) {
				CompoundResourceUsage compoundUsage = (CompoundResourceUsage) subUsage;
				subUsedMemory.addAll(compoundUsage.getSubUsedMemory(resource));
			}
		}
		return subUsedMemory;
	}

	private String computeVariableValue(String variable,
			Collection<String> subValues) {
		if (TOTAL.equals(variable)) {
			return computeSum(subValues);
		}
		return UNSUPPORTED_VARIABLE;
	}

	private String computeSum(Collection<String> subValues) {
		float sum = 0;
		for (String value : subValues) {
			float floatValue = safeGetFloatValue(value);
			sum += floatValue;
		}
		return String.valueOf(sum);
	}

	private Float safeGetFloatValue(String value) {
		if (value == null)
			return 0f;
		try {
			return Float.valueOf(value);
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	public Collection<IResourceUsage> getSubUsages() {
		return Collections.unmodifiableCollection(subUsages);
	}
}
