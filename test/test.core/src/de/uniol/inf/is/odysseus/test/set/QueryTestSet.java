package de.uniol.inf.is.odysseus.test.set;


public class QueryTestSet implements ITestSet{
	
	private String name = "";
	private String query;	
	
	
	public QueryTestSet(){
		name = toString();
	}
	
	@Override
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public String getName(){
		return this.name;
	}
	
}
