package org.aynsoft.imageSharing;

import org.aynsoft.javafile.Utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignUpActivity  extends BaseActivity implements OnClickListener{

	private EditText edtEmail,edtPass,edtUserName,edtName,edtPhone;
	private Button btnSignUp;
	private ImageView imgProfilePic;
	private Animation errorAnimation;
	private TextView txtCancel;
	private InterstitialAd interstitial;
	private boolean isImageUploaded=false;
	ParseFile imageFile;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.signup_activity_layout);
		
		// ** Admob Code */
				AdView adView = (AdView) this.findViewById(R.id.adView);
				AdRequest request = new AdRequest.Builder().build();
				adView.loadAd(request);
				interstitial = new InterstitialAd(getBaseContext());
				interstitial.setAdUnitId(getResources().getString(
						R.string.interstitial_id));
				interstitial.loadAd(request);
		
		errorAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
		edtEmail=(EditText)findViewById(R.id.edtSignEmail);
		edtPass=(EditText)findViewById(R.id.edtSignPassword);
		edtUserName=(EditText)findViewById(R.id.edtSignUserName);
		edtName=(EditText)findViewById(R.id.edtSignName);
		edtPhone=(EditText)findViewById(R.id.edtSignPhone);
		imgProfilePic=(ImageView)findViewById(R.id.imgSignProfilePic);
		
		btnSignUp=(Button)findViewById(R.id.btnSignUp);
		txtCancel=(TextView)findViewById(R.id.btnSignUpCancel);
		
		btnSignUp.setOnClickListener(this);
		imgProfilePic.setOnClickListener(this);
		txtCancel.setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.btnSignUp:
			if(isValidate()){
				signUp();
			}else{
				shake();
			}
			break;
		case R.id.imgSignProfilePic:
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			break;
		case R.id.btnSignUpCancel:
			setResult(RESPONCE_FAILURE);
			finish();
			break;
		default:
			break;
		}
	}


	private void signUp() {
		Toast.makeText(getBaseContext(),"Signing",Toast.LENGTH_SHORT).show();
		ParseUser user=new ParseUser();
		user.setEmail(edtEmail.getText().toString().trim());
		user.setPassword(edtPass.getText().toString().trim());
		user.setUsername(edtUserName.getText().toString().trim());
		ParseACL acl=new ParseACL();
		acl.setPublicReadAccess(true);
		user.setACL(acl);
		user.put(getString(R.string.USER_NAME),edtName.getText().toString().trim());
		user.put(getString(R.string.USER_PHONE), edtPhone.getText().toString().trim());
		if(isImageUploaded){
			user.put(getString(R.string.USER_PROFILE_PIC),imageFile);
		}
		user.signUpInBackground(new SignUpCallback() {			
			@Override
			public void done(ParseException e) {
				if(e==null){
					ParseUser.logInInBackground(edtUserName.getText().toString().trim(), 
							edtPass.getText().toString().trim(), new LogInCallback() {								
								@Override
								public void done(ParseUser arg0, ParseException arg1) {
									jumpToNext();
								}
					});
					
				}else{
					Toast.makeText(getBaseContext(), ""+e.getMessage(),
							Toast.LENGTH_SHORT).show();
					shake();
				}
			}
		});
	}
	
	private void jumpToNext(){
		startActivity(new Intent(this,NewHomeActivity.class));
		setResult(RESPONCE_SUCCESS);
		finish();
	}

	private boolean isValidate() {
		String email=edtEmail.getText().toString().trim();
		String user=edtUserName.getText().toString().trim();
		String pass=edtUserName.getText().toString().trim();
		String profileName=edtName.getText().toString().trim();
	
		if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
			edtEmail.setError(""+getResources().getString(R.string.ERROR_INVALID_EMAIL));
			return false;
		}else if(user.length()<6){
			edtUserName.setError(""+getResources().getString(R.string.ERROR_INVALID_USERNAME));
			return false;
			
		}else if(pass.length()<6){
			edtPass.setError(""+getResources().getString(R.string.ERROR_INVALID_PASSWORD));			
			return false;
		}else if(profileName.length()<6){
			edtName.setError(""+getResources().getString(R.string.ERROR_INVALID_USERNAME));			
			return false;
		}
		
		return true;
	}	
	
	private void shake() {
		findViewById(R.id.LayoutFormHolder).startAnimation(errorAnimation);
		edtUserName.setText("");
		edtEmail.setText("");
		edtPass.setText("");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) { 
	    case SELECT_PHOTO:
	        if(resultCode == RESULT_OK){  
	        	Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };	 
	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();	 
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            try{
	            	decodeSelectedImage(picturePath);
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	        }
	    }
	}
	
	private void  decodeSelectedImage(final String path){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Bitmap imageBitmap=BitmapFactory.decodeFile(path);
				imgProfilePic.setImageBitmap(imageBitmap);
				byte[] imageData=Utils.getBytesFromBitmap(imageBitmap);
				if(imageData!=null){
					uploadImage(imageData);
				}
			}
		}, 0);		
	}
	
	private void uploadImage(byte[] data){
		final ProgressDialog dialog=new ProgressDialog(SignUpActivity.this);
		dialog.setMessage("Uploading Image ");
		dialog.setCancelable(false);
		dialog.show();
		imageFile =new ParseFile("image.png",data);
		imageFile.saveInBackground(new SaveCallback() {			
			@Override
			public void done(ParseException e) {
				if(e==null){
					if(dialog.isShowing()){
						dialog.dismiss();
					}
					isImageUploaded=true;
				}else{
					
				}
			}
		}, new ProgressCallback() {			
			@Override
			public void done(Integer arg0) {
				dialog.setMessage("Uploading Image "+arg0+"%");
			}
		});
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
