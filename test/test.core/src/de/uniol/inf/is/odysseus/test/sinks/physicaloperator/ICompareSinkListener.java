package de.uniol.inf.is.odysseus.test.sinks.physicaloperator;

import de.uniol.inf.is.odysseus.core.metadata.IMetaAttribute;
import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.test.StatusCode;

public interface ICompareSinkListener {
	public void compareSinkProcessingDone(AbstractCompareSink<? extends IStreamObject<? extends IMetaAttribute>> sink, boolean done, StatusCode result);	
}
