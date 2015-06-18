package com.easylink.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylink.android.utils.EasyLinkUtils;

public class EasyLinkConfigDetailActivity extends Activity implements
		OnClickListener {
	private TextView tvTitle = null;
	private Button btnBack = null;
	private Button btnSave = null;

	private String titleString = "";

	private String detailConfigString = "";

	private String[] configType;
	private String[][] configContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		EasyLinkUtils
				.setProtraitOrientationEnabled(EasyLinkConfigDetailActivity.this);
		setContentView(R.layout.easylink_configuration);
		getIntentData();
		initViews();
		setViewClickListeners();
	}

	private void getIntentData() {
		Intent recvIntent = getIntent();
		detailConfigString = recvIntent.getStringExtra("detailConfigString");
		if (detailConfigString != null && !"".equals(detailConfigString)) {
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(detailConfigString);
				titleString = jsonObj.getString("N");
				JSONArray configJsonArray = jsonObj.getJSONArray("C");

				int configNum = configJsonArray.length();
				if (configNum > 0) {
					configType = new String[configNum];
					configContent = new String[configNum][];
					for (int i = 0; i < configNum; i++) {
						String configTypeName = configJsonArray
								.getJSONObject(i).getString("N");
						configType[i] = configTypeName;
						JSONArray jArrayConfigContent = configJsonArray
								.getJSONObject(i).getJSONArray("C");
						int contentNum = jArrayConfigContent.length();
						if (contentNum > 0) {
							String[] oneConfig = new String[contentNum];
							for (int j = 0; j < contentNum; j++) {
								oneConfig[j] = jArrayConfigContent
										.getJSONObject(j).toString();
							}
							configContent[i] = oneConfig;
						}
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
		btnSave.setVisibility(View.GONE);
		initListView();
	}

	private void initListView() {
		ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return configContent[groupPosition][childPosition];
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return configContent[groupPosition].length;
			}

			private TextView getChildTextView() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				lp.gravity = Gravity.RIGHT;
				TextView textView = new TextView(
						EasyLinkConfigDetailActivity.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				textView.setPadding(20, 10, 20, 10);
				textView.setTextSize(18);
				textView.setSingleLine();
				return textView;
			}

			private EditText getChildEditText() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 20);
				lp.gravity = Gravity.RIGHT;
				EditText editText = new EditText(
						EasyLinkConfigDetailActivity.this);
				editText.setLayoutParams(lp);
				editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				editText.setPadding(20, 10, 20, 10);
				editText.setTextSize(18);
				editText.setSingleLine();
				return editText;
			}

			private Button getChildButton() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				lp.gravity = Gravity.RIGHT;
				Button button = new Button(EasyLinkConfigDetailActivity.this);
				button.setLayoutParams(lp);
				button.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				button.setPadding(25, 10, 25, 10);
				button.setTextSize(18);
				return button;
			}

			private CheckBox getChildCheckbox() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				lp.gravity = Gravity.RIGHT;
				CheckBox checkBox = new CheckBox(
						EasyLinkConfigDetailActivity.this);
				checkBox.setLayoutParams(lp);
				checkBox.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				checkBox.setPadding(20, 0, 20, 0);
				checkBox.setTextSize(18);
				return checkBox;
			}

			// 该方法决定每个子选项的外观
			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				String curChildString = getChild(groupPosition, childPosition)
						.toString();
				String configName = "";
				JSONObject curJsonObject = null;
				try {
					curJsonObject = new JSONObject(curChildString);
					configName = curJsonObject.getString("N");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				final String curConfigName = configName;
				LinearLayout ll = new LinearLayout(
						EasyLinkConfigDetailActivity.this);
				ll.setOrientation(0);
				ll.setBackgroundColor(getResources().getColor(R.color.white));
				TextView textView = getChildTextView();
				textView.setText(configName);
				textView.setBackgroundColor(getResources().getColor(
						R.color.white));
				textView.setTextColor(getResources().getColor(R.color.black));
				ll.addView(textView);
				if (curJsonObject != null) {
					if (curJsonObject.has("P")) {
						String configRW = null;
						String configContetn = null;
						try {
							configRW = curJsonObject.getString("P");
							configContetn = curJsonObject.getString("C");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (curJsonObject.has("S")) {
							// > SelectPage
							Button nextPageButton = getChildButton();
							nextPageButton.setBackgroundColor(getResources()
									.getColor(R.color.white));
							nextPageButton.setText(configContetn + ">");
							if ("RW".equals(configRW)) {
								nextPageButton.setEnabled(true);
								nextPageButton.setTextColor(getResources()
										.getColor(R.color.black));
							} else {
								nextPageButton.setEnabled(false);
								nextPageButton.setTextColor(getResources()
										.getColor(R.color.grey_text_color));
							}
							ll.addView(nextPageButton);
						} else {
							if ("true".equalsIgnoreCase(configContetn)
									|| "false".equalsIgnoreCase(configContetn)) {
								// boolean TaggleButton
								boolean isSelected = true;
								try {
									isSelected = curJsonObject.getBoolean("C");
									final CheckBox chBox = getChildCheckbox();
									chBox.setChecked(isSelected);
									if ("RW".equals(configRW)) {
										chBox.setEnabled(true);
										chBox.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												EasyLinkConfigActivity.configResultMap.put(
														curConfigName,
														"" + chBox.isChecked());
											}
										});
									} else {
										chBox.setEnabled(false);
									}
									LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.WRAP_CONTENT,
											60);
									lp.gravity = Gravity.CENTER;
									lp.setMargins(10, 0, 10, 0);
									ll.addView(chBox, lp);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								// text EditView
								final EditText editText = getChildEditText();
								editText.setText(configContetn);
								editText.setBackgroundColor(getResources()
										.getColor(R.color.white));
								editText.setSingleLine();
								if ("RW".equals(configRW)) {
									editText.setEnabled(true);
									editText.setTextColor(getResources()
											.getColor(R.color.black));
									editText.addTextChangedListener(new TextWatcher() {

										@Override
										public void onTextChanged(
												CharSequence arg0, int arg1,
												int arg2, int arg3) {

										}

										@Override
										public void beforeTextChanged(
												CharSequence arg0, int arg1,
												int arg2, int arg3) {

										}

										@Override
										public void afterTextChanged(
												Editable arg0) {
											EasyLinkConfigActivity.configResultMap
													.put(curConfigName, "\""
															+ editText
																	.getText()
																	.toString()
															+ "\"");
										}
									});
								} else {
									editText.setEnabled(false);
									editText.setTextColor(getResources()
											.getColor(R.color.grey_text_color));
								}

								ll.addView(editText);
							}
						}
					} else {
						// Detail > NextPage
						Button nextPageButton = getChildButton();
						nextPageButton.setBackgroundColor(getResources()
								.getColor(R.color.white));
						nextPageButton.setText(">");
						nextPageButton.setTextColor(getResources().getColor(
								R.color.black));
						ll.addView(nextPageButton);
					}
				}
				return ll;
			}

			// 获取指定组位置处的组数据
			@Override
			public Object getGroup(int groupPosition) {
				return configType[groupPosition];
			}

			@Override
			public int getGroupCount() {
				return configType.length;
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			// 该方法决定每个组选项的外观
			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(
						EasyLinkConfigDetailActivity.this);
				ll.setOrientation(0);
				ll.setBackgroundColor(getResources().getColor(
						R.color.grey_background));
				TextView textView = getGroupTextView();
				textView.setText(getGroup(groupPosition).toString());
				ll.addView(textView);
				return ll;
			}

			private TextView getGroupTextView() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				TextView textView = new TextView(
						EasyLinkConfigDetailActivity.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				textView.setPadding(20, 20, 0, 20);
				textView.setTextColor(getResources().getColor(
						R.color.grey_text_color));
				textView.setTextSize(16);
				return textView;
			}

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition) {
				return true;
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}
		};
		ExpandableListView expandListView = (ExpandableListView) findViewById(R.id.list);
		expandListView.setGroupIndicator(null);
		expandListView.setAdapter(adapter);
		// 遍历所有group,将所有项设置成默认展开
		int groupCount = expandListView.getCount();
		for (int i = 0; i < groupCount; i++) {
			// expandListView.expandGroup(i);
			expandListView.expandGroup(i, true);
		}
		// 设置group不能收缩
		expandListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});

	}

	private void setViewClickListeners() {
		btnBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			finish();
			break;
		}

	}

}
