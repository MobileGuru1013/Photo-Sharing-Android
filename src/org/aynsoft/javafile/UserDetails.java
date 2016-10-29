package org.aynsoft.javafile;

import com.parse.ParseFile;

public class UserDetails {

	String userName;
	ParseFile userProfileFile;
	public UserDetails(String userName, ParseFile userProfileFile){
		this.userName = userName;
		this.userProfileFile = userProfileFile;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public ParseFile getUserProfileFile() {
		return userProfileFile;
	}
	public void setUserProfileFile(ParseFile userProfileFile) {
		this.userProfileFile = userProfileFile;
	}
	
}
