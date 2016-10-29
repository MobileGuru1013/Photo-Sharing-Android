package org.aynsoft.service;

import java.util.Random;

import org.aynsoft.imageSharing.EditProfileActivity;
import org.aynsoft.imageSharing.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

public class UploadPictureService extends Service{

	NotificationManager mNotificationManager ;
	private static final String PICTURE_UPLOADING_TITLE="Uploading Picture";
	
	
	public static ParseFile file;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {		
		super.onCreate();		
		mNotificationManager = (NotificationManager) getSystemService(Context.
				NOTIFICATION_SERVICE);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		if(intent.hasExtra(getString(R.string.EXTRA_PARSE_FILE))){			
			uploadImage(intent.getByteArrayExtra(getString(R.string.EXTRA_PARSE_FILE)));
		}else{
			Toast.makeText(getBaseContext(), "No Extra Found",Toast.LENGTH_SHORT).show();
		}
		
		return super.onStartCommand(intent, flags, startId);		
	}
	
	private void uploadImage(byte[] data) {
		file=new ParseFile("image.png",data);
		
		final int id = showNotification(PICTURE_UPLOADING_TITLE,true);
		file.saveInBackground(new SaveCallback() {			
			@Override
			public void done(ParseException e) {
				if(e==null){
					clearNotification(id);
					Intent mIntent=new Intent(getString(R.string.ACTION_IMAGE_UPLOADED));
					sendBroadcast(mIntent);		
				}else{
					Toast.makeText(getBaseContext(), ""+e.getMessage(),Toast.LENGTH_SHORT).show();
				}
				stopSelf();
			}
		},new ProgressCallback() {			
			@Override
			public void done(Integer arg0) {
				Toast.makeText(getBaseContext(), ""+arg0+"%",Toast.LENGTH_SHORT).show();
				
			}
		});
	}
	
	private int showNotification(String title,boolean isOngoing){
		NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(title)
		        .setOngoing(isOngoing);						
		Intent resultIntent = new Intent(this, EditProfileActivity.class);		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);		
		stackBuilder.addParentStack(EditProfileActivity.class);		
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(new Random().nextInt(1000),
		            PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);		
		int id =new Random().nextInt(1000);
		mNotificationManager.notify(id, mBuilder.build());
		return id;
	}
	
	private void clearNotification(int notificationID){
		mNotificationManager.cancel(notificationID);
	}
	
}
