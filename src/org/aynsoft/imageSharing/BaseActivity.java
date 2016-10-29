package org.aynsoft.imageSharing;

import android.app.Activity;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

public class BaseActivity extends Activity {

	private static final String PARSE_APP_KEY="Enter your Parse app  key";
	private static final String PARSE_CLIENT_KEY="Enter your Parse Client key";
	private static final String FACEBOOK_APP_ID="Enter your Facebook app id";
	protected static final int RESPONCE_SUCCESS=200;
	protected static final int RESPONCE_FAILURE=104;
	
	protected static final int REQUEST_HOME=102;
	protected static final int REQUEST_LOGIN=103;
	protected static final int REQUEST_SIGNUP=104;
	protected static final int REQUEST_UPLOADPOST=105;
	protected static final int REQUEST_FACEBOOK_LOGIN=106;
	protected static final int SELECT_PHOTO=100;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(getBaseContext(), PARSE_APP_KEY, PARSE_CLIENT_KEY);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParseFacebookUtils.initialize(FACEBOOK_APP_ID);
		//PushService.setDefaultPushCallback(getBaseContext(), PushActivity.class);		
		//PushService.subscribe(getBaseContext(), "PostPush", BaseActivity.class);
	}
}
