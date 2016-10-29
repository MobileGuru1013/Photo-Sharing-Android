package org.aynsoft.imageSharing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class PushActivity  extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent=getIntent();
		
		String key="com.parse.Data";
		Bundle bundle=intent.getExtras();
		String value=bundle.getString(key);
		Log.i("value", ""+value);
		Toast.makeText(getBaseContext(), ""+value,Toast.LENGTH_LONG).show();
		TextView tv=new TextView(getBaseContext());
		tv.setText(""+value);
		setContentView(tv);
		
	}
}
