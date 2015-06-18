package com.easylink.android;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

public class EasyLinkDeviceConfigActivity extends Activity implements
		OnClickListener {
	private TextView tvTitle = null;
	private Button btnBack = null;
	private Button btnSave = null;

	// private TextView tvFirmwareUpdate = null;

	// private String firmwareUpdate = "";

	private String deviceOldName = "";
	private String deviceConfigString = "";
	private String IPAddress = "";
	private String configRequestPort = "8000";
	private String configRequestMethod = "/config-write";
	private String configString = "";

	private String[] configType;
	private String[][] configContent;

	public static Map<String, String> configResultMap;

	public Button btnToNextForResult = null;
	public String selectValueBtnName = "";

	private AlertDialog.Builder dialogBuilder = null;
	private Dialog currenDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		EasyLinkUtils
				.setProtraitOrientationEnabled(EasyLinkDeviceConfigActivity.this);
		setContentView(R.layout.easylink_configuration);
		getIntentData();
		initViews();
		setViewClickListeners();
	}

	private void getIntentData() {
		configResultMap = new HashMap<String, String>();
		Intent recvIntent = getIntent();
		deviceConfigString = recvIntent.getStringExtra("device_config");
		IPAddress = recvIntent.getStringExtra("IPAddress");
		// deviceConfigString =
		// "{ \"N\": \"EMW3162(42586D)\", \"C\": [ { \"N\": \"MICO SYSTEM\", \"C\": [ { \"N\": \"Device Name\", \"C\": \"EMW3162 MI\", \"P\": \"RW\" }, { \"N\": \"Bonjour\", \"C\": true, \"P\": \"RW\" }, { \"N\": \"RF power save\", \"C\": false, \"P\": \"RW\" }, { \"N\": \"MCU power save\", \"C\": false, \"P\": \"RW\" }, { \"N\": \"Detail\", \"C\": [ { \"N\": \"\", \"C\": [ { \"N\": \"Firmware Rev.\", \"C\": \"MICO_SPP_2_1\", \"P\": \"RO\" }, { \"N\": \"Hardware Rev.\", \"C\": \"3162\", \"P\": \"RO\" }, { \"N\": \"MICO OS Rev.\", \"C\": \"31620002.016\", \"P\": \"RO\" }, { \"N\": \"RF Driver Rev.\", \"C\": \"5.90.195\", \"P\": \"RO\" }, { \"N\": \"Model\", \"C\": \"EMW3162\", \"P\": \"RO\" }, { \"N\": \"Manufacturer\", \"C\": \"MXCHIP Inc.\", \"P\": \"RO\" }, { \"N\": \"Protocol\", \"C\": \"com.mxchip.spp\", \"P\": \"RO\" } ] }, { \"N\": \"WLAN\", \"C\": [ { \"N\": \"BSSID\", \"C\": \"8C:BE:BE:25:1C:CA\", \"P\": \"RO\" }, { \"N\": \"Channel\", \"C\": 13, \"P\": \"RO\" }, { \"N\": \"Security\", \"C\": \"WPA2 MIXED\", \"P\": \"RO\" }, { \"N\": \"PMK\", \"C\": \"27AF96FB2B83D8DD7529F9811AE10E139DF8A91AF3E54AD6DF24A4E73B152A3D\", \"P\": \"RO\" }, { \"N\": \"DHCP\", \"C\": true, \"P\": \"RO\" }, { \"N\": \"IP address\", \"C\": \"192.168.31.217\", \"P\": \"RO\" }, { \"N\": \"Net Mask\", \"C\": \"255.255.255.0\", \"P\": \"RO\" }, { \"N\": \"Gateway\", \"C\": \"192.168.31.1\", \"P\": \"RO\" }, { \"N\": \"DNS Server\", \"C\": \"192.168.31.1\", \"P\": \"RO\" } ] } ] } ] }, { \"N\": \"WLAN\", \"C\": [ { \"N\": \"Wi-Fi\", \"C\": \"Xiaomi.Router\", \"P\": \"RW\" }, { \"N\": \"Password\", \"C\": \"stm32f215\", \"P\": \"RW\" } ] }, { \"N\": \"SPP Remote Server\", \"C\": [ { \"N\": \"Connect SPP Server\", \"C\": true, \"P\": \"RW\" }, { \"N\": \"SPP Server\", \"C\": \"192.168.2.254\", \"P\": \"RW\" }, { \"N\": \"SPP Server Port\", \"C\": 8080, \"P\": \"RW\" } ] }, { \"N\": \"MCU IOs\", \"C\": [ { \"N\": \"Baurdrate\", \"C\": 115200, \"P\": \"RW\", \"S\": [ 9600, 19200, 38400, 57600, 115200 ] } ] } ], \"PO\": \"com.mxchip.spp\", \"HD\": \"3162\", \"FW\": \"MICO_SPP_2_1\" }";
		if (deviceConfigString != null && !"".equals(deviceConfigString)) {
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(deviceConfigString);
				// firmwareUpdate = jsonObj.getString("FW");
				deviceOldName = jsonObj.getString("N");
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
		tvTitle.setText(R.string.configsave_title);
		btnBack = (Button) findViewById(R.id.header_btn_left);
		btnBack.setText(R.string.config_back);
		btnSave = (Button) findViewById(R.id.header_btn_right);
		btnSave.setText(R.string.configsave_done);
		// tvFirmwareUpdate = (TextView) findViewById(R.id.tv_firmware_update);
		// tvFirmwareUpdate.setText(firmwareUpdate);
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
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				lp.gravity = Gravity.RIGHT;
				TextView textView = new TextView(
						EasyLinkDeviceConfigActivity.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				textView.setPadding(20, 10, 20, 10);
				textView.setTextSize(18);
				textView.setSingleLine();
				return textView;
			}

			private EditText getChildEditText() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 20);
				lp.gravity = Gravity.RIGHT;
				EditText editText = new EditText(
						EasyLinkDeviceConfigActivity.this);
				editText.setLayoutParams(lp);
				editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				editText.setPadding(20, 10, 20, 10);
				editText.setTextSize(18);
				editText.setSingleLine();
				return editText;
			}

			private Button getChildButton() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				lp.gravity = Gravity.RIGHT;
				Button button = new Button(EasyLinkDeviceConfigActivity.this);
				button.setLayoutParams(lp);
				button.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				button.setPadding(25, 10, 25, 10);
				button.setTextSize(18);
				return button;
			}

			private CheckBox getChildCheckbox() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				lp.gravity = Gravity.RIGHT;
				CheckBox checkBox = new CheckBox(
						EasyLinkDeviceConfigActivity.this);
				checkBox.setLayoutParams(lp);
				checkBox.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				checkBox.setPadding(20, 0, 20, 0);
				checkBox.setTextSize(18);
				return checkBox;
			}

			// private Spinner getChildSpinner() {
			// AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
			// ViewGroup.LayoutParams.MATCH_PARENT, 60);
			// Spinner spinner = new Spinner(EasyLinkConfigActivity.this);
			// spinner.setLayoutParams(lp);
			// spinner.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			// // spinner.setTextSize(18);
			// return spinner;
			// }

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
				final String finalCurChildString = curChildString;
				final String curConfigName = configName;
				LinearLayout ll = new LinearLayout(
						EasyLinkDeviceConfigActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
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
							final Button toListButton = getChildButton();
							toListButton.setBackgroundColor(getResources()
									.getColor(R.color.white));
							toListButton.setText(configContetn + " >");
							if ("RW".equals(configRW)) {
								toListButton.setEnabled(true);
								toListButton.setTextColor(getResources()
										.getColor(R.color.black));
								toListButton
										.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View arg0) {
												btnToNextForResult = toListButton;
												selectValueBtnName = curConfigName;
												Intent toListIntent = new Intent(
														EasyLinkDeviceConfigActivity.this,
														EasyLinkConfigSelectListActivity.class);
												toListIntent.putExtra(
														"config_select_list",
														finalCurChildString);
												startActivityForResult(
														toListIntent, 1);
											}
										});

							} else {
								toListButton.setEnabled(false);
								toListButton.setTextColor(getResources()
										.getColor(R.color.grey_text_color));
							}
							ll.addView(toListButton);

							// spinner 方法
							// JSONObject banuJsonObject;
							// try {
							// banuJsonObject = new JSONObject(
							// finalCurChildString);
							// JSONArray jbanuArray = banuJsonObject
							// .getJSONArray("S");
							// if (jbanuArray != null) {
							// int arrNum = jbanuArray.length();
							// final String[] banuArray = new String[arrNum];
							// for (int i = 0; i < arrNum; i++) {
							// banuArray[i] = jbanuArray.getString(i);
							// }
							// final Spinner banuSpinner = getChildSpinner();
							// banuSpinner
							// .setBackgroundColor(getResources()
							// .getColor(R.color.white));
							//
							// ArrayAdapter<String> adapter = new
							// ArrayAdapter<String>(
							// EasyLinkConfigActivity.this,
							// android.R.layout.simple_spinner_item,
							// banuArray);
							// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							// banuSpinner.setAdapter(adapter);
							// if ("RW".equals(configRW)) {
							// banuSpinner.setEnabled(true);
							// banuSpinner.setOnItemSelectedListener(new
							// OnItemSelectedListener() {
							//
							// @Override
							// public void onItemSelected(
							// AdapterView<?> arg0,
							// View arg1, int arg2,
							// long arg3) {
							// EasyLinkConfigActivity.configResultMap
							// .put(curConfigName, "\""
							// + banuArray[arg2]
							// + "\"");
							// }
							//
							// @Override
							// public void onNothingSelected(
							// AdapterView<?> arg0) {
							// }
							// });
							// } else {
							// banuSpinner.setEnabled(false);
							// }
							// ll.addView(banuSpinner);
							// }
							// } catch (JSONException e) {
							// e.printStackTrace();
							// }

						} else {
							if ("true".equalsIgnoreCase(configContetn)
									|| "false".equalsIgnoreCase(configContetn)) {
								// boolean CheckBox
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
												EasyLinkDeviceConfigActivity.configResultMap.put(
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
											EasyLinkDeviceConfigActivity.configResultMap
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
						nextPageButton
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										Intent nextIntent = new Intent(
												EasyLinkDeviceConfigActivity.this,
												EasyLinkConfigDetailActivity.class);
										nextIntent.putExtra(
												"detailConfigString",
												finalCurChildString);
										startActivity(nextIntent);
									}
								});
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
						EasyLinkDeviceConfigActivity.this);
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
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				TextView textView = new TextView(
						EasyLinkDeviceConfigActivity.this);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 2) {
			if (requestCode == 1) {
				String strSelectValue = data.getStringExtra("selectValue");
				btnToNextForResult.setText(strSelectValue + " >");
				EasyLinkDeviceConfigActivity.configResultMap.put(
						selectValueBtnName, "" + strSelectValue);
			}
		}
	}

	private String getConfigResultString() {
		String configResultString = "";
		if (configResultMap == null || configResultMap.size() == 0) {
			configResultString = "{ }";
		} else {
			configResultString += "{";
			for (Map.Entry<String, String> entry : configResultMap.entrySet()) {
				configResultString += "\"" + entry.getKey() + "\":"
						+ entry.getValue() + ",";
			}
			configResultString = configResultString.substring(0,
					configResultString.lastIndexOf(","));
			configResultString += "}";
		}
		return configResultString;
	}

	private void finishConfig() {
		configString = getConfigResultString();
		if (IPAddress == null || "".equals(IPAddress)) {
			return;
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					HttpPostData(myHandler);
				}
			}).start();
		}
	}

	private void HttpPostData(Handler handler) {
		Looper.prepare();
		Message message = new Message();
		try {
			HttpClient httpclient = new DefaultHttpClient();
			String urlString = "http://" + IPAddress + ":" + configRequestPort
					+ configRequestMethod;
			HttpPost httppost = new HttpPost(urlString);
			// 添加http头信息
			// httppost.addHeader("User-Agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 1.7; .NET CLR 1.1.4322; CIBA; .NET CLR 2.0.50727)");
			// httppost.addHeader("Content-Length", "" + configString.length());
			// // 认证token
			// httppost.addHeader("Content-Type", "application/json");
			// http post的json数据格式： {"name": "your name","parentId":
			// "id_of_parent"}
			// JSONObject obj = new JSONObject();
			// obj.put("name", "your name");
			// obj.put("parentId", "your parentid");
//			Log.e("====", configString);
			httppost.setEntity(new StringEntity(configString));
			HttpResponse response;
			response = httpclient.execute(httppost);
			// 检验状态码，如果成功接收数据
			int respCode = response.getStatusLine().getStatusCode();
			if (respCode == HttpURLConnection.HTTP_OK) {
				message.what = 1;
			} else {
				message.what = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.what = 0;
		} finally {
			handler.handleMessage(message);
			Looper.loop();
		}
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (currenDialog != null) {
				currenDialog.dismiss();
			}

			switch (msg.what) {
			case 1:
				if (EasyLinkDeviceCenterActivity.findDeviceMap
						.containsKey(deviceOldName)) {
					EasyLinkDeviceCenterActivity.findDeviceMap
							.remove(deviceOldName);
				}
				Intent intent = new Intent();
				setResult(2, intent);
				finish();
				break;
			case 0:
				dialogBuilder
						.setTitle(null)
						.setMessage("Connect to " + IPAddress + " failed.")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										return;
									}
								});
				currenDialog = dialogBuilder.show();
				break;
			}
		};
	};

	private void setViewClickListeners() {
		btnBack.setOnClickListener(this);
		btnSave.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			finish();
			break;
		case R.id.header_btn_right:
			dialogBuilder = new AlertDialog.Builder(this)
					.setTitle("Please wait....")
					.setMessage(
							"Connect to " + IPAddress + " \r\n on Port "
									+ configRequestPort + " ....")
					.setPositiveButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									return;
								}
							});
			currenDialog = dialogBuilder.show();
			finishConfig();
			break;
		}

	}

}
