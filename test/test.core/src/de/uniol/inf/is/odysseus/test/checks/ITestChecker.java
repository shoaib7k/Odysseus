package de.uniol.inf.is.odysseus.test.checks;

import de.uniol.inf.is.odysseus.test.StatusCode;
import de.uniol.inf.is.odysseus.test.set.ITestSet;

public interface ITestChecker {
	
	public StatusCode check(ITestSet set);
	
}
