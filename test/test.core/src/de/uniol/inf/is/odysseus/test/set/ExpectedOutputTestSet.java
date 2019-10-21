package de.uniol.inf.is.odysseus.test.set;

import java.util.List;

import de.uniol.inf.is.odysseus.core.collection.Pair;

public class ExpectedOutputTestSet extends QueryTestSet {
	
	private List<Pair<String, String>> expectedOutput;
    private String dataHandler;
	
	public List<Pair<String, String>> getExpectedOutput() {
		return expectedOutput;
	}

	public void setExpectedOutput(List<Pair<String, String>> expectedOutput) {
		this.expectedOutput = expectedOutput;
	}
	
	public String getDataHandler(){
		return dataHandler;
	}
	
	public void setDataHandler(String dataHandler) {
		this.dataHandler = dataHandler;
	}
}
