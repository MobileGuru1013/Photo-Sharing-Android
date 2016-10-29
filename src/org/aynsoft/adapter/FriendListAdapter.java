package org.aynsoft.adapter;

import java.util.List;

import org.aynsoft.imageSharing.R;
import org.aynsoft.javafile.UserDetails;
import org.aynsoft.viewcomponents.SquareImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter{

	private List<UserDetails> details;
	private LayoutInflater inflater;
	public FriendListAdapter(Context context, List<UserDetails> details){
		this.details = details;
		this.inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return details.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		if(view == null){
			view = inflater.inflate(R.layout.listview_item, arg2,false);
		}
		TextView name = (TextView) view.findViewById(R.id.textViewUserName);
		name.setText(""+details.get(position).getUserName());
		SquareImageView profilePic = (SquareImageView) view.findViewById(R.id.imgListUser);
		if(details.get(position).getUserProfileFile() != null){
		profilePic.setParseFile(details.get(position).getUserProfileFile());
		profilePic.loadInBackground();
		}
		return view;
	}

}
