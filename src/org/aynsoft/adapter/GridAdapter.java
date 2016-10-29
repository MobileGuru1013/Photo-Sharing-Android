package org.aynsoft.adapter;

import java.util.List;

import org.aynsoft.imageSharing.R;
import org.aynsoft.lazy.ImageLoader;
import org.aynsoft.viewcomponents.SquareImageView;

import com.parse.ParseFile;
import com.parse.ParseObject;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter{

	Activity act;
	List<ParseObject> itemList;
	LayoutInflater inflater;
	public  ImageLoader loader;
	
	
	public GridAdapter(Activity activity,List<ParseObject> list) {
		this.act=activity;
		itemList=list;
		inflater=LayoutInflater.from(activity);
		loader=new ImageLoader(activity, 85, true);
	}
	
	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.griditem_layout,parent,false);
			holder.poster=(SquareImageView)convertView.findViewById(R.id.imgGridPost);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		ParseFile file=itemList.get(position).getParseFile(act.getString(R.string.USER_POST_PIC));
		if(file.isDataAvailable()){
			holder.poster.setParseFile(file);
			holder.poster.loadInBackground();
		}		
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView title;
		SquareImageView poster;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

}
