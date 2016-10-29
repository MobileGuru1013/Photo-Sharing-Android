package org.aynsoft.imageSharing;

import java.util.List;

import org.aynsoft.adapter.GridAdapter;
import org.aynsoft.javafile.App;
import org.aynsoft.viewcomponents.SquareImageView;
import org.aynsoft.viewcomponents.ViewUtils;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class BrowseUserActivity extends BaseActivity{

	private SquareImageView imgUserPic;
	private TextView txtUserName;
	private GridView gridView;	
	private ParseUser user;
	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_user_activity_layout);
		
		// ** Admob Code */
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest request = new AdRequest.Builder().build();
		adView.loadAd(request);
		interstitial = new InterstitialAd(getBaseContext());
		interstitial.setAdUnitId(getResources().getString(
				R.string.interstitial_id));
		interstitial.loadAd(request);
		
		imgUserPic=(SquareImageView)findViewById(R.id.imgBrowseUserPic);
		txtUserName=(TextView)findViewById(R.id.txtBrowseUsername);
		gridView=(GridView)findViewById(R.id.gridview);
		gridView.setNumColumns(ViewUtils.getInstance().getNoOfColumns(this));
		
		ParseObject object=App.getObject();
		user=(ParseUser)object.get(getString(R.string.USER_USER));
		ParseFile imgFile=user.getParseFile(getString(R.string.USER_PROFILE_PIC));
		if(imgFile!=null){
			imgUserPic.setParseFile(imgFile);
			imgUserPic.loadInBackground();
		}
		txtUserName.setText(""+user.getUsername());
		getData();
	}
	
	private void getData(){
		ParseQuery<ParseObject> query=ParseQuery.getQuery(getString(R.string.TABLE_USER_POST));
		query.whereEqualTo(getString(R.string.USER_USER),user);
		query.findInBackground(new FindCallback<ParseObject>() {			
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				GridAdapter adapter=new GridAdapter(BrowseUserActivity.this, arg0);
				gridView.setAdapter(adapter);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (interstitial.isLoaded()) {
			interstitial.show();
		}	
	}
}

