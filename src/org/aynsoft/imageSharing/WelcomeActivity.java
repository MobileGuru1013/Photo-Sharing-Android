package org.aynsoft.imageSharing;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.ParseUser;

public class WelcomeActivity extends BaseActivity implements OnClickListener{

	
	private Button btnLogin,btnSignIn, btnFBLogin;
	private InterstitialAd interstitial;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			getActionBar().hide();
		}else{
			
		}
		if(ParseUser.getCurrentUser()!=null){
			startActivity(new Intent(this,NewHomeActivity.class));
			finish();
		}else{			
			setContentView(R.layout.welcome_activity_layout);
			// ** Admob Code */
			AdView adView = (AdView) this.findViewById(R.id.adView);
			AdRequest request = new AdRequest.Builder().build();
			adView.loadAd(request);
			interstitial = new InterstitialAd(getBaseContext());
			interstitial.setAdUnitId(getResources().getString(
					R.string.interstitial_id));
			interstitial.loadAd(request);
			
			btnLogin=(Button)findViewById(R.id.btnLogin);
			btnSignIn=(Button)findViewById(R.id.btnSignIn);
			btnFBLogin = (Button) findViewById(R.id.btnFBLogin);
			btnLogin.setOnClickListener(this);
			btnSignIn.setOnClickListener(this);
			btnFBLogin.setOnClickListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			startActivityForResult(new Intent(this,UserLoginActivity.class),REQUEST_LOGIN);
			break;
		case R.id.btnSignIn:
			startActivityForResult(new Intent(this,SignUpActivity.class),REQUEST_SIGNUP);
			break;
		case R.id.btnFBLogin:
			
			startActivityForResult(new Intent(this,FacebookLoginActivity.class),REQUEST_FACEBOOK_LOGIN);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESPONCE_SUCCESS){
			finish();
		}	
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (interstitial.isLoaded()) {
			interstitial.show();
		}	
	}
	
	
}

