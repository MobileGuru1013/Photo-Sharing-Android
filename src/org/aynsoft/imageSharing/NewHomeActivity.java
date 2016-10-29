package org.aynsoft.imageSharing;

import java.util.ArrayList;
import java.util.List;

import org.aynsoft.adapter.HomeParseAdapter;
import org.aynsoft.adapter.HomeParseAdapter.OnPostItemClickListener;
import org.aynsoft.javafile.App;
import org.aynsoft.javafile.Utils;
import org.aynsoft.viewcomponents.PopUpMenuDialog;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

public class NewHomeActivity extends BaseActivity implements OnClickListener,
		OnPostItemClickListener {

	private ListView listView;
	public static List<ParseObject> latestPostList;
	private HomeParseAdapter adapter;
	private ImageView imgPostImage;
	private static final int SELECT_PHOTO = 100;
	private static boolean isImageUploaded = false;
	private ParseFile imageFile;
	ProgressBar barIndicator;
	private InterstitialAd interstitial;
	ParseFile profileImageFile;
	Button dialogButton;
	EditText comment;
	ImageView postImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		latestPostList = new ArrayList<ParseObject>();

		setContentView(R.layout.listview);

		// ** Admob Code */
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest request = new AdRequest.Builder().build();
		adView.loadAd(request);
		interstitial = new InterstitialAd(getBaseContext());
		interstitial.setAdUnitId(getResources().getString(
				R.string.interstitial_id));
		interstitial.loadAd(request);

		listView = (ListView) findViewById(R.id.listView);

		imgPostImage = (ImageView) findViewById(R.id.img_postImage);

		barIndicator = (ProgressBar) findViewById(R.id.barUploadPostIndicator);

		imgPostImage.setOnClickListener(this);
		
		// Fetch Facebook user info if the session is active
				Session session = ParseFacebookUtils.getSession();
				if (session != null && session.isOpened()) {
					makeMeRequest();				
				}
				
		setAdapter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_find_friend:
			startActivity(new Intent(this, SearchFriend.class));
			break;
		case R.id.action_edit_profile:
			startActivity(new Intent(this, EditProfileActivity.class));
			break;
		case R.id.action_log_out:
			ParseUser.getCurrentUser().logOut();
			startActivity(new Intent(this, WelcomeActivity.class));
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_postImage:
			showDialog();
			break;		
		default:
			break;
		}
	}

	@Override
	public void onPostImageClick(Object object) {
		App.object = (ParseObject) object;
		startActivity(new Intent(this, BrowseUserActivity.class));
	}

	@Override
	public void onOverflowMenuClick(String url) {
		PopUpMenuDialog dialog = new PopUpMenuDialog(NewHomeActivity.this, url);
		dialog.showDialog();
	}
	private void setAdapter() {
		adapter = new HomeParseAdapter(NewHomeActivity.this);
		adapter.setListener(this);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESPONCE_SUCCESS) {
			setAdapter();
		}
		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				try {
					decodeSelectedImage(picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void decodeSelectedImage(final String path) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Bitmap imageBitmap = BitmapFactory.decodeFile(path);
				postImage.setImageBitmap(imageBitmap);
				byte[] imageData = Utils.getBytesFromBitmap(imageBitmap);
				if (imageData != null) {
					uploadImage(imageData);
				}
			}
		}, 0);
	}

	private void uploadImage(byte[] data) {
		final ProgressDialog dialog = new ProgressDialog(NewHomeActivity.this);
		dialog.setMessage("Uploading Image ");
		dialog.setCancelable(false);
		dialog.show();
		imageFile = new ParseFile("image.png", data);
		imageFile.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
					isImageUploaded = true;
				} else {

				}
			}
		}, new ProgressCallback() {
			@Override
			public void done(Integer arg0) {
				dialog.setMessage("Uploading Image " + arg0 + "%");
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
	
	public void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							// Create a JSON object to hold the profile info
							JSONObject userProfile = new JSONObject();
							try {
								// Populate the JSON object
								userProfile.put("facebookId", user.getId());
								userProfile.put("name", user.getName());
								
								if (user.getLocation().getProperty("name") != null) {
									userProfile.put("location", (String) user
											.getLocation().getProperty("name"));
								}
								
								/**you can also use these details if you need*/
								/*if (user.getProperty("gender") != null) {
									userProfile.put("gender",
											(String) user.getProperty("gender"));
								}								
								if (user.getBirthday() != null) {
									userProfile.put("birthday",
											user.getBirthday());
								}
								if (user.getProperty("relationship_status") != null) {
									userProfile
											.put("relationship_status",
													(String) user
															.getProperty("relationship_status"));
								}*/

								// Save the user profile info in a user property
								ParseUser currentUser = ParseUser
										.getCurrentUser();
								currentUser.put("name", userProfile.getString("name"));
								currentUser.put("username", userProfile.getString("name"));
								currentUser.put("facebookId", userProfile.getString("facebookId"));								
								
								currentUser.saveInBackground();

								// Show the user info
								
							} catch (JSONException e) {
								Log.d(IntegratingFacebookApplication.TAG,
										"Error parsing returned user data.");
							}

						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d(IntegratingFacebookApplication.TAG,
										"The facebook session was invalidated.");
								
							} else {
								Log.d(IntegratingFacebookApplication.TAG,
										"Some other error: "
												+ response.getError()
														.getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();

	}
	
	private void showDialog(){
		// custom dialog
					final Dialog dialog = new Dialog(NewHomeActivity.this);
					dialog.setContentView(R.layout.custom_post_dialog);
					dialog.setTitle("Share Post");
		 
					// set the custom dialog components - text, image and button
					comment = (EditText) dialog.findViewById(R.id.editPost);										
					postImage = (ImageView) dialog.findViewById(R.id.imgCamera);
					postImage.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
							photoPickerIntent.setType("image/*");
							startActivityForResult(photoPickerIntent, SELECT_PHOTO);
							
						}
					});
					dialogButton = (Button) dialog.findViewById(R.id.btnDialogPost);					
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (isImageUploaded) {
								publishPost();
								dialog.dismiss();
							} else {
								Toast.makeText(getBaseContext(), "Please Select Pic",
										Toast.LENGTH_LONG).show();
							}							
						}
					});
		 
					dialog.show();
	}
	private void publishPost() {
		barIndicator.setVisibility(ProgressBar.VISIBLE);
		imgPostImage.setEnabled(false);
		ParseObject post = new ParseObject(getString(R.string.TABLE_USER_POST));
		post.put(getString(R.string.USER_POST_PIC), imageFile);
		post.put(getString(R.string.USER_USER), ParseUser.getCurrentUser());
		post.put(getString(R.string.USER_POST_CAPTION), comment.getText()
				.toString().trim());
		post.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Toast.makeText(getBaseContext(), "Published`",
							Toast.LENGTH_SHORT).show();
					setResult(RESPONCE_SUCCESS);
					barIndicator.setVisibility(ProgressBar.GONE);
					comment.setText("");
				} else {
					barIndicator.setVisibility(ProgressBar.GONE);
					imgPostImage.setEnabled(true);
					Toast.makeText(getBaseContext(), "" + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
}
