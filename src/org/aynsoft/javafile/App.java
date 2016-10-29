package org.aynsoft.javafile;

import com.parse.ParseObject;

import android.app.Application;
import android.content.Context;

public class App extends Application{

    private static Context context;
    public  static  ParseObject object;

    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
    
    public static ParseObject getObject(){
    	return object;
    }
}