package org.aynsoft.javafile;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Utils {

	private static Utils instance;
	private  Utils() {
		// TODO Auto-generated constructor stub
	}
	
	public static Utils getInstance(){
		if(instance==null){
			instance=new Utils();
		}
		return instance;
	}
	
	public boolean isEmailValid(String email){
		if(email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
			return true;
		}
		return false;
	}
	
	public static byte[] getBytesFromBitmap(Bitmap bitmap) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG, 70, stream);
	    return stream.toByteArray();
	}
	
	
	
}
