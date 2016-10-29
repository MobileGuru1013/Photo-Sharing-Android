package org.aynsoft.imageSharing;

import org.aynsoft.javafile.Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgetPasswordActivity extends  BaseActivity implements OnClickListener{

	private EditText edtEmail;
	private Button btnReset,btnCancle;
	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.forget_password_layout);
		
		// ** Admob Code */
				AdView adView = (AdView) this.findViewById(R.id.adView);
				AdRequest request = new AdRequest.Builder().build();
				adView.loadAd(request);
				interstitial = new InterstitialAd(getBaseContext());
				interstitial.setAdUnitId(getResources().getString(
						R.string.interstitial_id));
				interstitial.loadAd(request);
		
		edtEmail=(EditText)this.findViewById(R.id.edtForgetEmail);
		btnReset=(Button)this.findViewById(R.id.btnReset);
		btnCancle=(Button)this.findViewById(R.id.btnCancel);
		
		btnReset.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnReset:
			if(Utils.getInstance().isEmailValid(edtEmail.getText().toString().trim())){
				resetPassword();
			}else{
				edtEmail.setError(""+getResources().getString(R.string.ERROR_INVALID_EMAIL));
			}
			break;
		case R.id.btnCancel:
			finish();
			break;
		default:
			break;
		}
	}

	private void resetPassword() {
		ParseUser.requestPasswordResetInBackground(""+edtEmail.getText().toString().trim(),
				new RequestPasswordResetCallback() {
					@Override
					public void done(ParseException e) {					
						if(e==null){
							showSuccessDialog();
						}else{
							Toast.makeText(getBaseContext(), ""+e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
				}
		});
	}
	
	public void showSuccessDialog(){
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle(getResources().getString(R.string.FORGET_SUCCESS_TITLE));
		builder.setMessage(getResources().getString(R.string.FORGET_SUCCESS_MSG));
		builder.setPositiveButton("ok",new android.content.DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();		
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
}
