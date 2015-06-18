package com.easylink.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.easylink.android.utils.EasyLinkUtils;

public class EasyLinkConfigSelectListActivity extends Activity implements
		OnClickListener {
	private TextView tvTitle = null;
	private Button btnBack = null;
	private Button btnSave = null;
	private String titleString = "";
	private String selectValue = "";
	private String deviceConfigString = "";
	private String[] valueArray;
	private LayoutInflater mInflater;
	private MyAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		EasyLinkUtils
				.setProtraitOrientationEnabled(EasyLinkConfigSelectListActivity.this);
		setContentView(R.layout.config_select_list);
		getIntentData();
		initViews();
		setViewClickListeners();
	}

	private void getIntentData() {
		Intent recvIntent = getIntent();
		deviceConfigString = recvIntent.getStringExtra("config_select_list");
		if (deviceConfigString != null && !"".equals(deviceConfigString)) {
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(deviceConfigString);
				titleString = jsonObj.getString("N");
				selectValue = jsonObj.getString("C").trim();
				JSONArray jsonArray = jsonObj.getJSONArray("S");
				if (jsonArray != null) {
					int valueCount = jsonArray.length();
					valueArray = new String[valueCount];
					for (int i = 0; i < valueCount; i++) {
						valueArray[i] = jsonArray.getString(i).toString()
								.trim();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.header_text);
		tvTitle.setText(titleString);
		btnBack = (Button) findViewById(R.id.header_btn_left);
		btnBack.setText(R.string.config_back);
		btnSave = (Button) findViewById(R.id.header_btn_right);
		btnSave.setVisibility(Button.GONE);
		listView = (ListView) findViewById(R.id.select_list);
		adapter = new MyAdapter(EasyLinkConfigSelectListActivity.this);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public class MyAdapter extends BaseAdapter {
		public MyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return valueArray.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder holder;
			final int itemPosition = arg0;
			if (arg1 == null) {
				holder = new ViewHolder();
				arg1 = mInflater
						.inflate(R.layout.config_select_list_item, null);
				holder.tvValue = (TextView) arg1
						.findViewById(R.id.select_item_tv);
				holder.ckboBox = (CheckBox) arg1
						.findViewById(R.id.select_item_chbox);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}
			holder.tvValue.setText(valueArray[arg0]);
			if (valueArray[arg0].equals(selectValue)) {
				holder.ckboBox.setChecked(true);
			} else {
				holder.ckboBox.setChecked(false);
			}
			holder.ckboBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectValue = valueArray[itemPosition].toString().trim();
					adapter.notifyDataSetChanged();
				}
			});
			return arg1;
		}
	}

	public final class ViewHolder {
		public TextView tvValue;
		public CheckBox ckboBox;
	}

	private void setViewClickListeners() {
		btnBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			Intent intent = new Intent();
			intent.putExtra("selectValue", selectValue);
			setResult(2, intent);
			finish();
			break;
		}
	}

}
