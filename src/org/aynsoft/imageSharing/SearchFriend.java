package org.aynsoft.imageSharing;

import java.util.ArrayList;
import java.util.List;

import org.aynsoft.adapter.FriendListAdapter;
import org.aynsoft.javafile.UserDetails;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SearchFriend extends Activity {

	private ListView listView;
	ProgressDialog mProgressDialog;
	List<UserDetails> details;
	private InterstitialAd interstitial;
	private FriendListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ** Admob Code */
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest request = new AdRequest.Builder().build();
		adView.loadAd(request);
		interstitial = new InterstitialAd(getBaseContext());
		interstitial.setAdUnitId(getResources().getString(
				R.string.interstitial_id));
		interstitial.loadAd(request);

		listView = (ListView) findViewById(R.id.usernameListView);
		details = new ArrayList<UserDetails>();
		adapter = new FriendListAdapter(this, details);
		listView.setAdapter(adapter);
		new RemoteDataTask().execute();
	}

	// RemoteDataTask AsyncTask
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a progressdialog
			mProgressDialog = new ProgressDialog(SearchFriend.this);
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			// Show progressdialog
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ParseUser currentUser = ParseUser.getCurrentUser();
			String id = currentUser.getObjectId();
			final ParseQuery<ParseUser> query = ParseUser.getQuery();
			query.whereNotEqualTo("objectId", id);
			query.findInBackground(new FindCallback<ParseUser>() {
				public void done(List<ParseUser> objects, ParseException e) {
					if (e == null) {
						for (int i = 0; i < objects.size(); i++) {
							ParseUser u = (ParseUser) objects.get(i);
							Log.i("user name: ", ""
									+ u.getString("username").toString());
							details.add(new UserDetails(u.getString("username")
									.toString(), u.getParseFile("profilepic")));
							adapter.notifyDataSetChanged();
						}
					} else {
						// Something went wrong.
						Log.i("parse error", "Something went wrong");
					}
				}

			});
			Log.i("return", ": finshed doinbackground");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
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
