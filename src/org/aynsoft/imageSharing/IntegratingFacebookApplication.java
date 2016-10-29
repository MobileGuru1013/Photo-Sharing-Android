package org.aynsoft.imageSharing;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class IntegratingFacebookApplication extends Application {

	static final String TAG = "MyApp";

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "YOUR_PARSE_APPLICATION_ID",
				"YOUR_PARSE_CLIENT_KEY");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));

	}

}
