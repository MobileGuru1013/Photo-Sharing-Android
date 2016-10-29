package org.aynsoft.imageSharing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class UserLoginActivity extends BaseActivity implements OnClickListener {

	private EditText edtUserName;
	private EditText edtPassword;
	private Button btnLogin;
	private TextView txtForgetPass;
	private InterstitialAd interstitial;
	private Animation errorAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_main_layout);
		// ** Admob Code */
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest request = new AdRequest.Builder().build();
		adView.loadAd(request);
		interstitial = new InterstitialAd(getBaseContext());
		interstitial.setAdUnitId(getResources().getString(
				R.string.interstitial_id));
		interstitial.loadAd(request);

		errorAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

		txtForgetPass = (TextView) findViewById(R.id.txtForgetPassword);
		edtUserName = (EditText) findViewById(R.id.edtUserName);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);		
		btnLogin.setOnClickListener(this);
		txtForgetPass.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnLogin:
			if (isValidate()) {
				login();
			} else {
				showError();
			}

			break;
		case R.id.txtForgetPassword:
			startActivity(new Intent(this, ForgetPasswordActivity.class));
			break;		
		default:
			break;
		}
	}

	private void showError() {
		findViewById(R.id.LayoutFormHolder).startAnimation(errorAnimation);
		edtUserName.setText("");
		edtPassword.setText("");
	}

	private void login() {
		final ProgressBar bar = (ProgressBar) findViewById(R.id.progressIndicator);
		bar.setVisibility(ProgressBar.VISIBLE);
		ParseUser.logInInBackground(edtUserName.getText().toString().trim(),
				edtPassword.getText().toString().trim(), new LogInCallback() {

					@Override
					public void done(ParseUser user, ParseException exception) {
						bar.setVisibility(ProgressBar.GONE);
						if (user != null) {
							// jump to next activity
							jumpToNext();
						} else {
							showError();
							Toast.makeText(getBaseContext(),
									"" + exception.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private boolean isValidate() {
		String userName = edtUserName.getText().toString().trim();
		String pass = edtPassword.getText().toString().trim();
		if ((userName.length() > 0) && (pass.length() > 0)) {
			return true;
		}
		return false;
	}

	private void jumpToNext() {
		startActivity(new Intent(this, NewHomeActivity.class));
		setResult(RESPONCE_SUCCESS);
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESPONCE_FAILURE);
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
}
