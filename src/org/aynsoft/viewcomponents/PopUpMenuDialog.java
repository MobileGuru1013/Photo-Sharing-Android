package org.aynsoft.viewcomponents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PopUpMenuDialog implements OnItemClickListener {

	Activity act;
	AlertDialog dialog;
	AlertDialog.Builder builder;
	String[] string = { "Share" };
	String filepath;

	public PopUpMenuDialog(Activity activity, String uri) {
		this.act = activity;
		builder = new Builder(activity);
		filepath = uri;		
	}

	public void showDialog() {

		ListView lv = new ListView(act);
		dialog = builder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setView(lv);
		lv.setAdapter(new ArrayAdapter<String>(act,
				android.R.layout.simple_list_item_1, string));
		dialog.show();
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg2) {
		case 0:
			share();
			break;
		default:
			break;
		}
		dialog.dismiss();
	}

	// method to share
	public void share() {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_TEXT, (filepath));
		act.startActivity(Intent.createChooser(share, "Share Via"));
	}	
}
