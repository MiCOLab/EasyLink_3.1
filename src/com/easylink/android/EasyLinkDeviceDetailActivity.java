package com.easylink.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.jmdns.ServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.easylink.android.utils.EasyLinkTXTRecordUtil;
import com.easylink.android.utils.EasyLinkUtils;

public class EasyLinkDeviceDetailActivity extends Activity implements
		OnClickListener {
	private TextView tvTitle = null;
	private Button btnBack = null;
	private Button btnEdit = null;

	private TextView tvDeviceName = null;
	private TextView tvService = null;
	private TextView tvIPAddress = null;
	private TextView tvPort = null;
	private TextView tvHardware_Rev = null;
	private TextView tvManufacturer = null;
	private TextView tvMAC = null;
	private TextView tvSeed = null;
	private TextView tvModel = null;
	private TextView tvProtocol = null;
	private TextView tvMICO_OS_Rev = null;
	private TextView tvFirmware_Rev = null;

	private String deviceName = "";
	private String service = "";
	private String IPAddress = "";
	private String port = "";
	private String hardware_Rev = "";
	private String manufacturer = "";
	private String mac = "";
	private String seed = "";
	private String model = "";
	private String protocol = "";
	private String mico_OS_Rev = "";
	private String firmware_Rev = "";

	private String deviceKey = "";
	private ServiceInfo curServiceInfo = null;

	private String configRequestPort = "8000";
	private String configRequestMethod = "/config-read";
	private String configString = "";

	private AlertDialog.Builder dialogBuilder = null;
	private Dialog currenDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		EasyLinkUtils
				.setProtraitOrientationEnabled(EasyLinkDeviceDetailActivity.this);

		setContentView(R.layout.easylink_device_detail);
		getIntentData();
		initViews();
		setViewClickListeners();
	}

	private void getIntentData() {
		Intent recvIntent = getIntent();
		deviceKey = recvIntent.getStringExtra("device_key");
		if (deviceKey != null && !"".equals(deviceKey)) {
			curServiceInfo = EasyLinkDeviceCenterActivity.findDeviceMap
					.get(deviceKey);
			if (curServiceInfo != null) {
				deviceName = curServiceInfo.getName();
				String strTempService = curServiceInfo.getType();
				service = strTempService.substring(0,
						strTempService.indexOf("local"));
				IPAddress = EasyLinkTXTRecordUtil.setDeviceIP(""
						+ curServiceInfo.getAddress());
				port = "" + curServiceInfo.getPort();
				String TXT_Record = curServiceInfo.getTextString();
				hardware_Rev = EasyLinkTXTRecordUtil.setHardwareRev(TXT_Record);
				manufacturer = EasyLinkTXTRecordUtil
						.setManufacturer(TXT_Record);
				mac = EasyLinkTXTRecordUtil.setMac(TXT_Record);
				seed = EasyLinkTXTRecordUtil.setSeed(TXT_Record);
				model = EasyLinkTXTRecordUtil.setModel(TXT_Record);
				protocol = EasyLinkTXTRecordUtil.setProtocol(TXT_Record);
				mico_OS_Rev = EasyLinkTXTRecordUtil.setMICOOSRev(TXT_Record);
				firmware_Rev = EasyLinkTXTRecordUtil.setFirmwareRev(TXT_Record);
			}
		}
	}

	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.header_text);
		tvTitle.setText(R.string.detail_title);
		btnBack = (Button) findViewById(R.id.header_btn_left);
		btnBack.setText(R.string.config_back);
		btnEdit = (Button) findViewById(R.id.header_btn_right);
		btnEdit.setText(R.string.detail_right);
		tvDeviceName = (TextView) findViewById(R.id.detail_device_name);
		tvDeviceName.setText(deviceName);
		tvService = (TextView) findViewById(R.id.tv_Service);
		tvService.setText(service);
		tvIPAddress = (TextView) findViewById(R.id.detail_ip_address);
		tvIPAddress.setText(IPAddress);
		tvPort = (TextView) findViewById(R.id.detail_Port);
		tvPort.setText(port);
		tvHardware_Rev = (TextView) findViewById(R.id.detail_Hardware_Rev);
		tvHardware_Rev.setText(hardware_Rev);
		tvManufacturer = (TextView) findViewById(R.id.detail_Manufacturer);
		tvManufacturer.setText(manufacturer);
		tvMAC = (TextView) findViewById(R.id.detail_MAC);
		tvMAC.setText(mac);
		tvSeed = (TextView) findViewById(R.id.detail_Seed);
		tvSeed.setText(seed);
		tvModel = (TextView) findViewById(R.id.detail_Model);
		tvModel.setText(model);
		tvProtocol = (TextView) findViewById(R.id.detail_Protocol);
		tvProtocol.setText(protocol);
		tvMICO_OS_Rev = (TextView) findViewById(R.id.detail_MICO_OS_Rev);
		tvMICO_OS_Rev.setText(mico_OS_Rev);
		tvFirmware_Rev = (TextView) findViewById(R.id.detail_Firmware_Rev);
		tvFirmware_Rev.setText(firmware_Rev);
	}

	private void gotoConfigPage() {
		if (IPAddress == null || "".equals(IPAddress)) {
			return;
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					executeHttpGet(myHandler);
				}
			}).start();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (currenDialog != null) {
				currenDialog.dismiss();
			}

			switch (msg.what) {
			case 1:
				Bundle resultBundle = (Bundle) msg.obj;
				configString = resultBundle.getString("resultString");
//				Log.e("====", configString);
				Intent intent = new Intent(EasyLinkDeviceDetailActivity.this,
						EasyLinkDeviceConfigActivity.class);
				intent.putExtra("device_config", configString);
				intent.putExtra("IPAddress", IPAddress);
//				startActivity(intent);
				startActivityForResult(
						intent, 1);
				break;
			case 0:
				dialogBuilder.setTitle(null).setMessage(
								"Connect to " + IPAddress + " failed.")
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 2) {
			if (requestCode == 1) {
				finish();
			}
		}
	}

	public void executeHttpGet(Handler handler) {
		String result = null;
		URL url = null;
		HttpURLConnection httpConn = null;
		InputStreamReader in = null;
		Bundle bundle = null;
		Looper.prepare();
		Message message = new Message();
		try {
			// "http://10.0.2.2:8888/data/get/?token=alexzhou"
			String urlString = "http://" + IPAddress + ":" + configRequestPort
					+ configRequestMethod;
			url = new URL(urlString);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setReadTimeout(1000 * 10);
			httpConn.setConnectTimeout(1000 * 10);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("GET");
			int respCode = httpConn.getResponseCode();
//			Log.e("request", "" + respCode);
			if (respCode == HttpURLConnection.HTTP_OK) {
				in = new InputStreamReader(httpConn.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(in);
				StringBuffer strBuffer = new StringBuffer();
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					strBuffer.append(line);
				}
				result = strBuffer.toString();

				bundle = new Bundle();
				bundle.putString("resultString", result);
				message.what = 1;
				message.obj = bundle;
			} else {
				message.what = 0;
				message.obj = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.what = 0;
			message.obj = null;
		} finally {
			if (httpConn != null) {
				httpConn.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			handler.handleMessage(message);
			Looper.loop();
		}
	}

	private void setViewClickListeners() {
		btnBack.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
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
							"Connect to " + IPAddress + " \r\n on Port " + configRequestPort
									+ " ....")
					.setPositiveButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									return;
								}
							});
			currenDialog = dialogBuilder.show();
			gotoConfigPage();
			break;
		}

	}

}
