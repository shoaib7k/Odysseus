package de.uniol.inf.is.odysseus.test.context;

import java.net.URL;

public class BasicTestContext implements ITestContext{
	
	private String username;
	private String password;
	private URL dataRootPath;
	
	@Override
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public URL getDataRootPath() {
		return dataRootPath;
	}
	public void setDataRootPath(URL dataRootPath) {
		this.dataRootPath = dataRootPath;
	}	

}
