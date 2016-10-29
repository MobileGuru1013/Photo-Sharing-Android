package org.aynsoft.adapter;

import org.aynsoft.imageSharing.R;
import org.aynsoft.javafile.App;
import org.aynsoft.viewcomponents.SquareImageView;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class HomeParseAdapter extends ParseQueryAdapter<ParseObject> {

	Context c;
	OnPostItemClickListener listener;	

	public OnPostItemClickListener getListener() {
		return listener;
	}

	public void setListener(OnPostItemClickListener listener) {
		this.listener = listener;
	}

	public HomeParseAdapter(Context context) {			
		 super(context,new QueryFactory<ParseObject>() {
			@Override
			public ParseQuery<ParseObject> create() {	
				ParseQuery<ParseObject> query=ParseQuery.getQuery(App.getAppContext().getString(R.string.TABLE_USER_POST));
				query.addDescendingOrder("createdAt");
				
				query.include(App.getAppContext().getString(R.string.USER_USER));
				query.setLimit(10);
				return query;
			}
		 } );
	}
	
	@Override
	public View getItemView(ParseObject object, View convertView, ViewGroup viewgroup) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(getContext(), R.layout.post_list_view_items, null);
			holder.titleUser=(TextView)convertView.findViewById(R.id.txtHomeListUserName);
			holder.postTime=(TextView)convertView.findViewById(R.id.txtHomeListTime);
			holder.postCaption=(TextView)convertView.findViewById(R.id.txtHomeListPostText);
			holder.imgUser=(SquareImageView)convertView.findViewById(R.id.imgHomeListUser);
			holder.imgPost=(SquareImageView)convertView.findViewById(R.id.imgHomeListPostImage);
			holder.imgOverflow=(ImageButton)convertView.findViewById(R.id.imgHomeListOverflow);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}		
		ParseFile postFile=object.getParseFile(getContext().getString(R.string.USER_POST_PIC));
		String postCaption=object.getString(getContext().getString(R.string.USER_POST_CAPTION));
		
		ParseUser user=(ParseUser)object.get(getContext().getString(R.string.USER_USER));
		ParseFile imgUserFile=user.getParseFile(getContext().getString(R.string.USER_PROFILE_PIC));
		String username=user.getString(getContext().getString(R.string.USER_NAME));
		
		holder.titleUser.setText(""+username);
		holder.postTime.setText("");
		holder.postCaption.setText(""+postCaption);	
		OnClickHandler handler=new OnClickHandler();		
		if(postFile!=null){
			holder.imgPost.setParseFile(postFile);
			holder.imgPost.loadInBackground();	
			holder.imgOverflow.setOnClickListener(handler);
			holder.imgOverflow.setTag(postFile.getUrl());
		}
		
		if(imgUserFile!=null){
			holder.imgUser.setParseFile(imgUserFile);
			holder.imgUser.loadInBackground();
			if(listener!=null){
				holder.imgUser.setOnClickListener(handler);
				holder.imgUser.setTag(object);
			}
		}					
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView titleUser,postTime,postCaption;
		SquareImageView imgUser,imgPost;
		ImageButton imgOverflow;
	}

	
	
	class OnClickHandler implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgHomeListUser:
				listener.onPostImageClick(v.getTag());
				break;
			case R.id.imgHomeListOverflow:
				listener.onOverflowMenuClick((String)v.getTag());
				break;
			default:
				break;
			}
		}
	}
	
	public interface OnPostItemClickListener{
		public void onPostImageClick(Object object);
		public void onOverflowMenuClick(String url);
	}
}
