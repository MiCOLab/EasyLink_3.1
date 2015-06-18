package com.easylink.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ListAdapter extends SimpleAdapter {
	private int selectedPosition = -1;
	private List<HashMap<String, String>> data = null;
	private LayoutInflater mInflater;
	private Context ctx;
	private int resource;

	@SuppressWarnings("unchecked")
	public ListAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		ctx = context;
		mInflater = LayoutInflater.from(context);
		this.data = (List<HashMap<String, String>>) data;
		this.resource = resource;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(this.resource, null);
			holder.tvDeviceName = (TextView) convertView
					.findViewById(R.id.device_item_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// int index = position;
		holder.tvDeviceName.setText(data.get(position).get("device_name")
				.toString());
//		if (position % 2 == 0) {
//			convertView.setBackgroundColor(Color.rgb(255, 255, 189));
//		} else {
//			convertView.setBackgroundColor(Color.rgb(255, 255, 255));
//		}
		if (selectedPosition == position) {
			convertView.setBackgroundColor(Color.rgb(148, 181, 214));
		}
		holder = null;
		return convertView;
	}

	public final class ViewHolder {
		public TextView tvDeviceName;
	}
}
