package org.aynsoft.imageSharing;

import org.aynsoft.javafile.Utils;
import org.aynsoft.service.UploadPictureService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileActivity extends BaseActivity implements OnClickListener  {

	ParseUser cUser;
	private Button btnLogout,btnUpdate;
	private ParseImageView imgUserPic;
	private EditText edtName,edtEmail,edtUsername;
	private InterstitialAd interstitial;
	private static boolean isImageUploaded=false;
	
	private ImageUploadedReceiver imageUploadedReceiver;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		cUser=ParseUser.getCurrentUser();
		imageUploadedReceiver=new ImageUploadedReceiver();
		
		setContentView(R.layout.edit_profile_activity_layout);
		
		// ** Admob Code */
				AdView adView = (AdView) this.findViewById(R.id.adView);
				AdRequest request = new AdRequest.Builder().build();
				adView.loadAd(request);
				interstitial = new InterstitialAd(getBaseContext());
				interstitial.setAdUnitId(getResources().getString(
						R.string.interstitial_id));
				interstitial.loadAd(request);
				
		edtEmail=(EditText)findViewById(R.id.edtEditEmail);
		edtName=(EditText)findViewById(R.id.edtEditName);
		edtUsername=(EditText)findViewById(R.id.edtEditUserName);
		imgUserPic=(ParseImageView)findViewById(R.id.imgMainUserPic);
		btnLogout=(Button)findViewById(R.id.btnEditLogout);
		btnUpdate=(Button)findViewById(R.id.btnEditSubmit);
		
		btnUpdate.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		imgUserPic.setOnClickListener(this);
		
		initialiseView();
	}
	private void initialiseView(){
		String name =""+cUser.getString(getString(R.string.USER_NAME));
		String user=""+cUser.getUsername();
		String email=""+cUser.getEmail();
	
		
		edtName.setText(name);
		edtUsername.setText(user);
		edtEmail.setText(email);
		
		ParseFile file=cUser.getParseFile(getString(R.string.USER_PROFILE_PIC));
		if(file!=null){
			imgUserPic.setParseFile(file);
			imgUserPic.loadInBackground();
		}else{
			Toast.makeText(getBaseContext(), "Null File",Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnEditLogout:
			finish();
			break;
		case R.id.imgMainUserPic:
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);   
			break;
		case R.id.btnEditSubmit:
			if(isValidate()){
				updateProfile();
			}
			break;
		default:
			break;
		}	
	}
	
	
	
	private void updateProfile() {
		cUser.setUsername(edtUsername.getText().toString().trim());
		cUser.setEmail(edtEmail.getText().toString().trim());
		cUser.put(getString(R.string.USER_NAME),
				edtName.getText().toString().trim());
		
		if(isImageUploaded){
			cUser.put(getString(R.string.USER_PROFILE_PIC),UploadPictureService.file);
		}
		cUser.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e==null){
					Toast.makeText(getBaseContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
				}else{
					showError(e);
				}
			}
		});
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
	            	byte[] imageData=Utils.getBytesFromBitmap(BitmapFactory.decodeFile(picturePath));
	            	if(imageData!=null){
	            		uploadImage(imageData);
	            	}else{
	            		Toast.makeText(getBaseContext(), "Error_NODATA",Toast.LENGTH_SHORT).show();
	            	}
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	        }
	    }
	}
	
	private void uploadImage(byte[] data) {						
		Intent intent =new Intent(this,UploadPictureService.class);
		intent.putExtra(getString(R.string.EXTRA_PARSE_FILE),data);
		startService(intent);
	}
	
	

	protected void showError(ParseException e) {
		Toast.makeText(getBaseContext(), ""+e.getMessage(),Toast.LENGTH_LONG).show();
	}

	
	
	
	class ImageUploadedReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {	
			isImageUploaded=true;
		}		
	}
	
	@Override
	protected void onStart() {
		registerReceiver(imageUploadedReceiver, new IntentFilter
				(getString(R.string.ACTION_IMAGE_UPLOADED)));
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		unregisterReceiver(imageUploadedReceiver);
		super.onStop();
	}	
	
	
	private boolean isValidate() {
		String email=edtEmail.getText().toString().trim();
		String user=edtUsername.getText().toString().trim();
		String profileName=edtName.getText().toString().trim();
		if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
			edtEmail.setError(""+getResources().getString(R.string.ERROR_INVALID_EMAIL));
			return false;
		}else if(user.length()<6){
			edtUsername.setError(""+getResources().getString(R.string.ERROR_INVALID_USERNAME));
			return false;
			
		}else if(profileName.length()<6){
			edtName.setError(""+getResources().getString(R.string.ERROR_INVALID_PASSWORD));			
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (interstitial.isLoaded()) {
			interstitial.show();
		}	
	}
}
