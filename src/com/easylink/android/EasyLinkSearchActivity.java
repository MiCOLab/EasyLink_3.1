package com.easylink.android;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylink.android.utils.EasyLinkConstants;
import com.easylink.android.utils.EasyLinkDialogManager;
import com.easylink.android.utils.EasyLinkUtils;
import com.easylink.android.utils.EasyLinkWifiManager;
import com.mxchip.ftc_service.FTC_Listener;
import com.mxchip.ftc_service.FTC_Service;
/**
 * 用于增加新设备的位置
 * @author Lyon
 *
 */
public class EasyLinkSearchActivity extends Activity implements
		OnClickListener, FirstTimeConfigListener, OnCheckedChangeListener,
		TextWatcher {
	/**
	 * Wifi Manager instance which gives the network related information like
	 * Wifi ,SSID etc.
	 */
	private EasyLinkWifiManager mWifiManager = null;
	private RelativeLayout footerView = null;

	/**
	 * Sending a request to server is done onClick of this button,it interacts
	 * with the smartConfig.jar library
	 */
	private Button mSendDataPackets_2 = null;
	private EditText mSSIDInputField = null;

	/**
	 * The Password input field details are entered by user also called Key
	 * field
	 */
	private EditText mPasswordInputField = null;

	private TextView mGateWayIPInputField = null;

	/**
	 * The Encryption key field input field is entered by user
	 */
	// private EditText mKeyInputField = null;

	public static EditText mDeviceNameInputField = null;

	/**
	 * A Dialog instance which is responsible to generate all dialogs in the app
	 */
	private EasyLinkDialogManager mDialogManager = null;

	/**
	 * A check box which when checked sends the encryption key to server as a
	 * input and checks if key is exactly 16chars
	 */
	// private CheckBox mconfig_key_checkbox = null;

	/**
	 * Progressbar
	 */
	// private ProgressBar mconfigProgress_1 = null;
	private ProgressBar mconfigProgress_2 = null;

	/**
	 * Called initially and loading of views are done here Orientation is
	 * restricted for mobile phones to protrait only and both orientations for
	 * tablet
	 */

	/**
	 * Timer instance to activate network wifi check for every few minutes Popps
	 * out an alert if no network or if connected to any network
	 */
	private Timer timer = new Timer();
	private Timer listTimer = new Timer();

	/**
	 * A boolean to check if network alert is visible or invisible. If visible
	 * we are dismissing the existing alert
	 */
	public static boolean sIsNetworkAlertVisible = false;

	/**
	 * Boolean to check if network is enabled or not
	 */
	public boolean isNetworkConnecting = false;

	@SuppressWarnings("unused")
	private boolean flag = true;

	/**
	 * Dialog ID to tigger no network dialog
	 */
	private static final int NO_NETWORK_DIALOG_ID = 002;

	static MulticastLock multicastLock;
	public static int ipAddr;

	private FTC_Service ftcService = null;
	// private udpSearch udpS = null;
	private TextView tvTitle = null;
	private Button btnBack = null;
	private Button btnNext = null;
	private ListView listView = null;
	private MyHandler myHandler = null;
	private List<Map<String, String>> list;
	private ListAdapter dataAdapter;
	private int curSelectedPosition = -1;

	//设置保存的wifi密码
	private Context ctx = null;
	private SharedPreferences sp = null;
	private Editor editor = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);

		//设置保存的wifi密码
		ctx = EasyLinkSearchActivity.this;
		sp = ctx.getSharedPreferences("SSID_PASSWORD", MODE_PRIVATE);
		editor = sp.edit();

		/**
		 * Disable orientation if launched in mobile
		 */
		EasyLinkUtils
				.setProtraitOrientationEnabled(EasyLinkSearchActivity.this);

		setContentView(R.layout.easylink_devicesearch);
		WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		multicastLock = wifiManager.createMulticastLock("multicast.test");

		/**
		 * Check for WIFI intially
		 */
		if (isNetworkConnecting) {

		} else {
			if (!sIsNetworkAlertVisible) {
				checkNetwork("ONCREATE");
			}
		}

		/**
		 * Initialize all view componets in screen
		 */
		initViews();

		/**
		 * Initailize all click listeners to views
		 */
		setViewClickListeners();

		/**
		 * Set initial data to all non editable components
		 */
		initData();
		timerDelayForAPUpdate();

		/**
		 * Initializing the intent Filter for network check cases and
		 * registering the events to broadcast reciever
		 */
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	/**
	 * returns the Wifi Manager instance which gives the network related
	 * information like Wifi ,SSID etc.
	 * 
	 * @return Wifi Manager instance
	 */
	public EasyLinkWifiManager getWiFiManagerInstance() {
		if (mWifiManager == null) {
			mWifiManager = new EasyLinkWifiManager(EasyLinkSearchActivity.this);
			ipAddr = mWifiManager.getCurrentIpAddressConnectedInt();
		}
		return mWifiManager;
	}

	/**
	 * Initialize all view componets in screen with input data
	 */
	private void initData() {
		if (getWiFiManagerInstance().getCurrentSSID() != null
				&& getWiFiManagerInstance().getCurrentSSID().length() > 0) {
			mSSIDInputField.setText(getWiFiManagerInstance().getCurrentSSID());

			//设置保存的wifi密码
			if (sp != null) {
				String password = sp.getString(getWiFiManagerInstance()
						.getCurrentSSID(), "none");
				if (!password.equals("none")) {
					mPasswordInputField.setText(password);
				}
			}

			mSSIDInputField.setEnabled(false);
			mSSIDInputField.setFocusable(false);
			mSSIDInputField.setFocusableInTouchMode(false);
		}

		mGateWayIPInputField.setText(getWiFiManagerInstance()
				.getGatewayIpAddress());
	}

	/**
	 * Check for wifi network if not throw a alert to user
	 */
	@SuppressWarnings("deprecation")
	private boolean checkNetwork(String str) {

		if (!(getWiFiManagerInstance().isWifiConnected())) {
			sIsNetworkAlertVisible = true;
			mDialogManager = new EasyLinkDialogManager(
					EasyLinkSearchActivity.this);
			showDialog(NO_NETWORK_DIALOG_ID);
			return false;
			// Do stuff when wifi not there.. disable start button.
		} else {
			return true;
		}

	}

	/**
	 * Initialise all view components from xml
	 */
	private void initViews() {
		// mSendDataPackets_1 = (Button)
		// findViewById(R.id.config_start_button_1);
		mSendDataPackets_2 = (Button) findViewById(R.id.config_start_button_2);
		// mSendDataPackets_1.setVisibility(Button.INVISIBLE);
		footerView = (RelativeLayout) findViewById(R.id.config_footerview);
		mSSIDInputField = (EditText) findViewById(R.id.config_ssid_input);
		mPasswordInputField = (EditText) findViewById(R.id.config_passwd_input);
		mGateWayIPInputField = (TextView) findViewById(R.id.config_gateway_input);
		// mKeyInputField = (EditText) findViewById(R.id.config_key_input);
		mDeviceNameInputField = (EditText) findViewById(R.id.config_device_name_input);
		// mconfigProgress_1 = (ProgressBar)
		// findViewById(R.id.config_progress_1);
		mconfigProgress_2 = (ProgressBar) findViewById(R.id.config_progress_2);
		mconfigProgress_2.bringToFront();
		tvTitle = (TextView) findViewById(R.id.header_text);
		tvTitle.setText(R.string.config_title);
		btnBack = (Button) findViewById(R.id.header_btn_left);
		btnBack.setText(R.string.config_back);
		btnNext = (Button) findViewById(R.id.header_btn_right);
		btnNext.setText("Next");
		btnNext.setVisibility(TextView.GONE);
		// listView = (ListView) findViewById(R.id.config_device_find);
		// udpS = new udpSearch();
	}

	/**
	 * Init the click listeners of all required views
	 */
	private void setViewClickListeners() {
		// mSendDataPackets_1.setOnClickListener(this);
		mSendDataPackets_2.setOnClickListener(this);
		footerView.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		// btnNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.config_start_button_2:
			//设置保存的wifi密码
			if (editor != null) {
				editor.putString(mSSIDInputField.getText().toString().trim(),
						mPasswordInputField.getText().toString().trim());
				editor.commit();
			}
			// Check network
			if (checkNetwork("bUTTON")) {
				try {
					sendPacketData2();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.header_btn_left:
			back();
			break;
		case R.id.header_btn_right:
			// Intent intent=new
			// Intent(EasyLinkSearchActivity.this,EasyLinkConfigActivity.class);
			// startActivity(intent);
			break;
		}
	}

	public boolean isCalled = false;
	public boolean isCalled2 = false;

	private void sendPacketData2() throws Exception {
		if (!isCalled2) {
			isCalled2 = true;
			ftcService = FTC_Service.getInstence();
			myHandler = new MyHandler();
			multicastLock.acquire();
			// /
			// sendUdpData();
			// /
			ftcService.transmitSettings(ctx, mSSIDInputField.getText().toString()
					.trim(), mPasswordInputField.getText().toString(), ipAddr,
					ftc_Listener);
			mSendDataPackets_2
					.setBackgroundResource(R.drawable.selection_focus_btn);
			mconfigProgress_2.setVisibility(ProgressBar.VISIBLE);
		} else {
			stopPacketData2();
			multicastLock.release();
		}
	}

	// private void sendUdpData(){
	// String broadcastIP="255.255.255.255";
	// int broadcastPort=8089;
	// byte[] broadcastData = {(byte)0x21 ,(byte)0x00 ,(byte)0x0A, (byte)0x00,
	// (byte)0x00, (byte)0x00, (byte)0xD4, (byte)0xFF, (byte)0xFF, (byte)0xFF};
	// udpS.doUdpFind(broadcastData, broadcastPort, broadcastIP,
	// udpSearch_Listener);
	// }

	// private udpSearch_Listener udpSearch_Listener = new udpSearch_Listener()
	// {
	// @Override
	// public void onDeviceFound(String dataString) {
	// Log.e("====","udpresult dataString:"+dataString);
	// EasyLinkConstants.findedDevicesList.put(
	// dataString, "");
	// }
	// };

	private FTC_Listener ftc_Listener = new FTC_Listener() {
		@Override
		public void onFTCfinished(Socket s, String data) {
			Log.e("====", "onFTCfinished()");
			if (!"".equals(data)) {
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(data);
					String deviceName = jsonObj.getString("N");
					EasyLinkConstants.findedDevicesList.put(
							deviceName.toString(), data.toString());
					EasyLinkConstants.findedDevicesSocketList.put(
							deviceName.toString(), s);
					Message msg = new Message();
					msg.what = EasyLinkConstants.DEVICE_FIND_SUCCESS;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void isSmallMTU(int MTU) {
			// TODO Auto-generated method stub
			
		}
	};

	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EasyLinkConstants.DEVICE_FIND_SUCCESS:
				initDeviceListView();
				break;
			default:
				break;
			}
		}
	}

	private void initDeviceListView() {
		listView = (ListView) findViewById(R.id.config_device_find);
		list = new ArrayList<Map<String, String>>();
		list = getData();
		if (list != null && list.size() != 0) {
			String[] values = new String[] { "device_name" };
			int[] ids = new int[] { R.id.device_item_tv };
			dataAdapter = new ListAdapter(EasyLinkSearchActivity.this, list,
					R.layout.easylink_devicesearch_item, values, ids);
			listView.setAdapter(dataAdapter);
			dataAdapter.setSelectedPosition(curSelectedPosition);
			dataAdapter.notifyDataSetChanged();
			listView.setOnItemClickListener(lvItemOnClick(dataAdapter));
		} else {
			listView.setAdapter(null);
		}
	}

	private OnItemClickListener lvItemOnClick(final ListAdapter dataAdapter) {
		return new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long arg3) {
				dataAdapter.setSelectedPosition(curSelectedPosition = position);
				TextView tvDeviceNameTextView = (TextView) v
						.findViewById(R.id.device_item_tv);
				String deviceKeyString = tvDeviceNameTextView.getText()
						.toString();
				String deviceConfigString = EasyLinkConstants.findedDevicesList
						.get(deviceKeyString);
				if (deviceConfigString != null
						&& !"".equals(deviceConfigString)) {
					Intent configIntent = new Intent(
							EasyLinkSearchActivity.this,
							EasyLinkConfigActivity.class);
					configIntent.putExtra("devicekey", deviceKeyString);
					startActivity(configIntent);
				}
			}
		};
	}

	private List<Map<String, String>> getData() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Map.Entry<String, String> entry : EasyLinkConstants.findedDevicesList
				.entrySet()) {
			map = new HashMap<String, String>();
			map.put("device_name", entry.getKey());
			data.add(map);
		}
		return data;
	}

	private void stopPacketData2() {
		if (isCalled2) {
			try {
				isCalled2 = false;
				mSendDataPackets_2.setText(getResources().getString(
						R.string.start_label_2));
				mSendDataPackets_2.setBackgroundResource(R.drawable.selection);
				if (mconfigProgress_2 != null) {
					mconfigProgress_2.setVisibility(ProgressBar.INVISIBLE);
				}
				// udpS.stopUdpFind();
				ftcService.stopTransmitting();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Logic to restrict the orientation in mobiles phones only becasue of size
	 * constraint
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (!(EasyLinkUtils.isScreenXLarge(getApplicationContext()))) {
			return;
		}
	}

	/**
	 * Callback for Failure or success of the SmartConfig.jar library api
	 */
	@Override
	public void onFirstTimeConfigEvent(FtcEvent arg0, Exception arg1) {
		try {
			/**
			 * Adding the Try catch just to ensure the event doesnt retrun
			 * null.Some times observed null from Lib file.Just a safety measure
			 */
			arg1.printStackTrace();

		} catch (Exception e) {

		}

		switch (arg0) {
		case FTC_ERROR:
			/**
			 * Stop transmission
			 */
			handler.sendEmptyMessage(EasyLinkConstants.DLG_CONNECTION_FAILURE);
			break;
		case FTC_SUCCESS:

			/**
			 * Show user alert on success
			 */
			handler.sendEmptyMessage(EasyLinkConstants.DLG_CONNECTION_SUCCESS);
			break;
		case FTC_TIMEOUT:
			/**
			 * Show user alert when timed out
			 */
			handler.sendEmptyMessage(EasyLinkConstants.DLG_CONNECTION_TIMEOUT);
			break;
		default:
			break;
		}
	}

	/**
	 * Show timeout alert
	 */
	private void showConnectionTimedOut(int dialogType) {
		if (mDialogManager == null) {
			mDialogManager = new EasyLinkDialogManager(
					EasyLinkSearchActivity.this);
		}
		mDialogManager.showCustomAlertDialog(dialogType);
	}

	/**
	 * Show Failure alert
	 */
	private void showFailureAlert(int dialogType) {
		if (mDialogManager == null) {
			mDialogManager = new EasyLinkDialogManager(
					EasyLinkSearchActivity.this);
		}
		mDialogManager.showCustomAlertDialog(dialogType);
	}

	/**
	 * Throws an alert to user stating the success message recieved after
	 * configuration
	 */
	private void showConnectionSuccess(int dialogType) {
		if (mDialogManager == null) {
			mDialogManager = new EasyLinkDialogManager(
					EasyLinkSearchActivity.this);
		}
		mDialogManager.showCustomAlertDialog(dialogType);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * Check for network cases when app is minimized again
		 */
		if (!sIsNetworkAlertVisible) {
			if (isNetworkConnecting) {

			} else {
				if (!(getWiFiManagerInstance().isWifiConnected())) {
					showDialog(NO_NETWORK_DIALOG_ID);
				}
			}
			sIsNetworkAlertVisible = false;
		}
		initDeviceListView();
	}

	/**
	 * Stop data trasnfer on app exist if is running
	 */
	@Override
	protected void onStop() {
		super.onStop();
		stopPacketData2();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@SuppressWarnings("unused")
	private boolean isKeyChecked = false;

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		isKeyChecked = isChecked;
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	int textCount = 0;

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	/**
	 * Handler class for invoking dialog when a thread notifies of FTC_SUCCESS
	 * or FTC_FAILURE
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EasyLinkConstants.DLG_CONNECTION_FAILURE:
				showFailureAlert(EasyLinkConstants.DLG_CONNECTION_FAILURE);
				break;
			case EasyLinkConstants.DLG_CONNECTION_SUCCESS:
				showConnectionSuccess(EasyLinkConstants.DLG_CONNECTION_SUCCESS);
				break;

			case EasyLinkConstants.DLG_CONNECTION_TIMEOUT:
				showConnectionTimedOut(EasyLinkConstants.DLG_CONNECTION_TIMEOUT);
				break;
			}
			/**
			 * Stop transmission
			 */
			// stopPacketData();
		}
	};

	/**
	 * Timer to check network periodically at some time intervals. If network is
	 * available then the current access point details are set to the SSID field
	 * in next case else a alert dialog is shown
	 */
	void timerDelayForAPUpdate() {
		int periodicDelay = 1000; // delay for 1 sec.
		int timeInterval = 180000; // repeat every 3minutes.

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						// stuff that updates ui
						if (!sIsNetworkAlertVisible) {
							if (!(getWiFiManagerInstance().isWifiConnected())) {
								showDialog(NO_NETWORK_DIALOG_ID);
							}
						}
					}
				});
			}
		}, periodicDelay, timeInterval);

		listTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						initDeviceListView();
					}
				});
			}
		}, 1000, 3000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
		unregisterReceiver(broadcastReceiver);
		stopPacketData2();
	}

	/**
	 * Common Alert dialog instance to show no network or AP change message
	 */
	private AlertDialog alert = null;

	/**
	 * Dialog creation is done here
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder1;
		try {
			if (alert != null) {
				if (alert.isShowing()) {
					alert.dismiss();
				}
			}
		} catch (Exception e) {

		}

		switch (id) {
		case NO_NETWORK_DIALOG_ID:
			sIsNetworkAlertVisible = true;
			builder1 = new AlertDialog.Builder(this);
			builder1.setCancelable(true)
					.setTitle(
							getResources().getString(
									R.string.alert_easylink_title))
					.setMessage(
							getResources().getString(
									R.string.alert_no_network_title))
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									sIsNetworkAlertVisible = false;

								}
							});

			alert = builder1.create();
			alert.show();
			break;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * A broadcast reciever which is registered to notify the app about the
	 * changes in network or Access point is switched by the Device WIfimanager
	 */
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@SuppressLint("DefaultLocale")
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				if (intent.getBooleanExtra(
						WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {

				} else {
					// wifi connection was lost
					if (!sIsNetworkAlertVisible) {
						if (!(getWiFiManagerInstance().isWifiConnected())) {

						}
					}
					/**
					 * Clearing the previous SSID Gateway ip if accesspoint is
					 * disconnected in between connection or usage
					 */
					mSSIDInputField.setText("");
					mGateWayIPInputField.setText("");
				}
			}

			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

				NetworkInfo info = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.getType() == ConnectivityManager.TYPE_WIFI) {

				}

				if (info.getDetailedState() == DetailedState.CONNECTED) {
					isNetworkConnecting = true;
					WifiManager myWifiManager = (WifiManager) context
							.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = myWifiManager.getConnectionInfo();
					mSSIDInputField.setText(EasyLinkWifiManager
							.removeSSIDQuotes(wifiInfo.getSSID()));
					mSSIDInputField.setEnabled(false);
					mSSIDInputField.setFocusable(false);
					mSSIDInputField.setFocusableInTouchMode(false);
					int gatwayVal = myWifiManager.getDhcpInfo().gateway;
					String gateWayIP = (String.format("%d.%d.%d.%d",
							(gatwayVal & 0xff), (gatwayVal >> 8 & 0xff),
							(gatwayVal >> 16 & 0xff), (gatwayVal >> 24 & 0xff)))
							.toString();
					mGateWayIPInputField.setText(gateWayIP);
				}
			}
		}
	};

	private void clearAll() {
		for (Map.Entry<String, Socket> entry : EasyLinkConstants.findedDevicesSocketList
				.entrySet()) {
			try {
				Socket socket = entry.getValue();
				if (null != socket) {
					OutputStream oStream = socket.getOutputStream();
					oStream.write("{}".getBytes());
					oStream.close();
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		EasyLinkConstants.findedDevicesList.clear();
		EasyLinkConstants.findedDevicesSocketList.clear();
	}

	private void back() {
		stopPacketData2();
		clearAll();
		Intent configIntent = new Intent(EasyLinkSearchActivity.this,
				EasyLinkDeviceCenterActivity.class);
		startActivity(configIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		back();
	}

}