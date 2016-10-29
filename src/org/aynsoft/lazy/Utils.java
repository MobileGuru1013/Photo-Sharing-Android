package org.aynsoft.lazy;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
 
public class Utils {
	private static final String KEY_SEEK_POSITION="seek";
	private static final String KEY_SONG_POSITION="position";
	
	private static Utils utils;
	
	private  Utils() {
		
	}
	
	public static Utils getInstance(){
		if(utils==null){
			return new Utils();
		}
		return utils;
	}
	
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
             
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              //Read byte from input stream
                 
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
               
              //Write byte from output stream
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public void putCurrentStatus(Activity act,int seek_positon,int song_position){    	
    	SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(act);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putInt(KEY_SONG_POSITION, song_position);
    	editor.commit();
    	editor.putInt(KEY_SEEK_POSITION, seek_positon);
    	editor.commit();
    }
    
    public int[] getCurrentStatus(Activity act){
    	int[] array=new int[2];
    	SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(act);
    	array[0]=prefs.getInt(KEY_SEEK_POSITION, -1);
    	array[1]=prefs.getInt(KEY_SONG_POSITION, -1);
    	return array;
    }
    
    public void clearStatus(Activity act){
    	SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(act);
    	Editor editor=prefs.edit();
    	editor.remove(KEY_SEEK_POSITION);
    	editor.remove(KEY_SONG_POSITION);
    	editor.commit();
    	
    }
    
}