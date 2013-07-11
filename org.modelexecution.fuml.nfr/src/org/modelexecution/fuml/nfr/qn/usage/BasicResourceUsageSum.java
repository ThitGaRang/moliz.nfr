package org.modelexecution.fuml.nfr.qn.usage;

import java.util.Collection;

import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.ResourceUsage;
import org.eclipse.uml2.uml.NamedElement;
import org.modelexecution.fuml.nfr.internal.BasicResourceUsage;

public class BasicResourceUsageSum extends BasicResourceUsage implements IResourceUsageSum {

	private float execTimeSum, allocatedMemorySum, usedMemorySum, powerPeakSum, energySum, msgSizeSum;
	
	public BasicResourceUsageSum(NamedElement namedElement,
			ResourceUsage resourceUsage) {
		super(namedElement, resourceUsage);
		computeSums();
	}
	
	public void reComputeSums() {
		computeSums();
	}
	
	private void computeSums() {
		execTimeSum = computeSum(getRawResourceUsage().getExecTime());
		allocatedMemorySum = computeSum(getRawResourceUsage().getAllocatedMemory());
		usedMemorySum = computeSum(getRawResourceUsage().getUsedMemory());
		powerPeakSum = computeSum(getRawResourceUsage().getPowerPeak());
		energySum = computeSum(getRawResourceUsage().getEnergy());
		msgSizeSum = computeSum(getRawResourceUsage().getMsgSize());
	}
	
	private float computeSum(Collection<String> subValues) {
		float sum = 0;
		for (String value : subValues) {
			float floatValue = safeGetFloatValue(value);
			sum += floatValue;
		}
		return sum;
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

	public float getExecTimeSum() {
		return execTimeSum;
	}
	
	public float getAllocatedMemorySum() {
		return allocatedMemorySum;
	}

	@Override
	public float getUsedMemorySum() {
		return usedMemorySum;
	}

	@Override
	public float getPowerPeakSum() {
		return powerPeakSum;
	}

	@Override
	public float getEnergySum() {
		return energySum;
	}

	@Override
	public float getMsgSizeSum() {
		return msgSizeSum;
	}
}
