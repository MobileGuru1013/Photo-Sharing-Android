package org.aynsoft.imageSharing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class FacebookLoginActivity extends Activity {

	private Button loginButton;
	private Dialog progressDialog;
	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		// ** Admob Code */
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest request = new AdRequest.Builder().build();
		adView.loadAd(request);
		interstitial = new InterstitialAd(getBaseContext());
		interstitial.setAdUnitId(getResources().getString(
				R.string.interstitial_id));
		interstitial.loadAd(request);
		
		showHashKey(this);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			showUserDetailsActivity();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			showUserDetailsActivity();
		}else{
		FacebookLoginActivity.this.progressDialog = ProgressDialog.show(
				FacebookLoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_relationships", "user_birthday", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				FacebookLoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.i("massage generated", ""+err.getLocalizedMessage());
					Log.d(IntegratingFacebookApplication.TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(IntegratingFacebookApplication.TAG,
							"User signed up and logged in through Facebook!");
					showUserDetailsActivity();
				} else {
					Log.d(IntegratingFacebookApplication.TAG,
							"User logged in through Facebook!");
					showUserDetailsActivity();
				}
			}
		});
	}}

	private void showUserDetailsActivity() {
		Intent intent = new Intent(this, NewHomeActivity.class);
		startActivity(intent);
	}
	
	public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "org.aynsoft.logInParseEx", PackageManager.GET_SIGNATURES); //Your            package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (interstitial.isLoaded()) {
			interstitial.show();
		}	
	}
}
