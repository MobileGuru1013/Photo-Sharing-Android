package org.aynsoft.viewcomponents;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ViewUtils {

	private static ViewUtils utils;
	private static final int MAX_GRID_SIZE=180;
	private  ViewUtils() {
		
	}
	
	public static  ViewUtils getInstance(){
		if(utils==null){
			utils=new ViewUtils();
		}
		return utils;
	}
	
	public int getNoOfColumns(Activity c){
		DisplayMetrics metrics = new DisplayMetrics();
		c.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width  = metrics.widthPixels;
		if(width<=480){
			return 3;
		}else
			return width/MAX_GRID_SIZE;
	}
}
