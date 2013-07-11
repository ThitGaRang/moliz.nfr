package org.modelexecution.fuml.nfr.qn.usage;

import org.modelexecution.fuml.nfr.IResourceUsage;

public interface IResourceUsageSum extends IResourceUsage {
	public float getExecTimeSum();
	public float getAllocatedMemorySum();
	public float getUsedMemorySum();
	public float getPowerPeakSum();
	public float getEnergySum();
	public float getMsgSizeSum();
	
	public void reComputeSums();
}
