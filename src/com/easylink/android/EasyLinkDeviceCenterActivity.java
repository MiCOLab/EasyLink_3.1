package com.easylink.android;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylink.android.MyListView.OnRefreshListener;
import com.easylink.android.utils.EasyLinkTXTRecordUtil;
import com.easylink.android.utils.EasyLinkUtils;

/**
 * 	主界面
 * @author Lyon
 *
 */
public class EasyLinkDeviceCenterActivity extends Activity implements
		OnClickListener {
	private Button btnGetGuide = null;
	// 增加新设备的按键
	private Button btnAddNewDevice = null;

	// InetAddress是Java对IP地址的封装
	public static InetAddress intf = null;
	
	public static JmDNS jmdns = null;
	public static WifiManager wm = null;
	// 组播锁
	public static MulticastLock lock = null;
	public static SampleListener sl = null;
	public static ServiceInfo info = null;

	public boolean isStarted = false;
	
	// ??????????????????不懂？？？？？？？？？？？
	public static Map<String, ServiceInfo> findDeviceMap = new HashMap<String, ServiceInfo>();
	public List<HashMap<String, String>> deviceList = new ArrayList<HashMap<String, String>>();
	private Timer timer = new Timer();
	ServiceInfo sInfo;

	private LayoutInflater mInflater;
	private MyAdapter adapter;
	// private ListView listView;
	private MyListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		EasyLinkUtils
				.setProtraitOrientationEnabled(EasyLinkDeviceCenterActivity.this);
		// 应该是显示已存在的地址列表
		setContentView(R.layout.easylink_devicecenter);
		initViews();
		setViewClickListeners();

//		Thread thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					while (!isStarted) {
//						if (intf != null && jmdns != null) {
//							startDeviceSearch();
//							Thread.sleep(1000 * 3);
//						} else {
//							if (intf == null) {
//								intf = getLocalIpAddress();
//							}
//
//							if (jmdns == null) {
//								jmdns = JmDNS.create(intf);
//							}
//						}
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		thread.start();
//		timerDelayForAPUpdate();
		searchThreadStart();
	}

	private void initViews() {
		btnGetGuide = (Button) findViewById(R.id.header_btn_left);
		btnGetGuide.setVisibility(Button.GONE);
		btnAddNewDevice = (Button) findViewById(R.id.header_btn_right);
	}

	private void setViewClickListeners() {
		btnGetGuide.setOnClickListener(this);
		btnAddNewDevice.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			Intent scroIntent = new Intent(EasyLinkDeviceCenterActivity.this,
					MainActivity.class);
			startActivity(scroIntent);
			break;
		case R.id.header_btn_right:
			// 跳转到增加新设备的位置
			Intent configIntent = new Intent(EasyLinkDeviceCenterActivity.this,
					EasyLinkSearchActivity.class);
			startActivity(configIntent);
			finish();
			break;
		}
	}
	
	private void searchThreadStart(){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!isStarted) {
						if (intf != null && jmdns != null) {
							startDeviceSearch();
							Thread.sleep(1000 * 3);
						} else {
							if (intf == null) {
								intf = getLocalIpAddress();
							}

							if (jmdns == null) {
								jmdns = JmDNS.create(intf);
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		timerDelayForAPUpdate();
	}

	private void startDeviceSearch() {
		try {
			if (wm == null)
				wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			// 需要学习一下lock锁的使用
			lock = wm.createMulticastLock("mylock");
			lock.setReferenceCounted(true);
			lock.acquire();

			sl = new SampleListener();
			jmdns.addServiceListener("_easylink._tcp.local.", sl);
			// Log.i("DISCOVER", "Turning ON Discovery Service");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void timerDelayForAPUpdate() {
		int periodicDelay = 1000; // delay for 1 sec.
		int timeInterval = 1000; // repeat every 3minutes.

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						updateListView();
					}
				});
			}
		}, periodicDelay, timeInterval);
	}

	private void updateListView() {
		getDeviceList();
		// listView = (ListView) findViewById(R.id.devicecenter_devicelist);
		listView = (MyListView) findViewById(R.id.devicecenter_devicelist);
		adapter = new MyAdapter(EasyLinkDeviceCenterActivity.this);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				findDeviceMap.clear();
				
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(3000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// data.addFirst("刷新后的内容");
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						// adapter.notifyDataSetChanged();
//						findDeviceMap.clear();
						listView.onRefreshComplete();
					}

				}.execute();
			}
		});
	}

	private void getDeviceList() {
		deviceList.clear();
		HashMap<String, String> deviceMap = null;
		for (Map.Entry<String, ServiceInfo> entry : findDeviceMap.entrySet()) {
			deviceMap = new HashMap<String, String>();
			ServiceInfo si = entry.getValue();
			deviceMap.put("device_name", si.getName());
			deviceMap.put(
					"device_mac",
					"MAC:"
							+ EasyLinkTXTRecordUtil.setDeviceMac(""
									+ si.getTextString()));
			deviceMap.put(
					"device_IP",
					"IP:"
							+ EasyLinkTXTRecordUtil.setDeviceIP(""
									+ si.getAddress()));
			deviceList.add(deviceMap);
		}
	}

	public class MyAdapter extends BaseAdapter {
		public MyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return findDeviceMap.size();
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
				arg1 = mInflater.inflate(R.layout.easylink_devicecenter_item,
						null);
				holder.ivDeviceLogo = (ImageView) arg1
						.findViewById(R.id.iv_device_logo);
				holder.tvName = (TextView) arg1
						.findViewById(R.id.tv_devicecenter_name);
				holder.tvMac = (TextView) arg1
						.findViewById(R.id.tv_devicecenter_mac);
				holder.tvIP = (TextView) arg1
						.findViewById(R.id.tv_devicecenter_ip);
				holder.btnInfo = (Button) arg1
						.findViewById(R.id.btn_devicecenter_info);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}
			try {
				String deviceNameString = deviceList.get(arg0)
						.get("device_name").toString();
				if(deviceNameString.contains("#")){
					deviceNameString = deviceNameString.substring(0,deviceNameString.indexOf("#"));
				}
				holder.tvName.setText(deviceNameString);
				holder.tvMac.setText(deviceList.get(arg0).get("device_mac")
						.toString());
				holder.tvIP.setText(deviceList.get(arg0).get("device_IP")
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			holder.btnInfo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent curIntent = new Intent(
							EasyLinkDeviceCenterActivity.this,
							EasyLinkDeviceDetailActivity.class);
					curIntent.putExtra("device_key",
							deviceList.get(itemPosition).get("device_name"));
					startActivity(curIntent);
				}
			});
			return arg1;
		}
	}

	public final class ViewHolder {
		public ImageView ivDeviceLogo;
		public TextView tvName;
		public TextView tvMac;
		public TextView tvIP;
		public Button btnInfo;
	}

	class SampleListener implements ServiceListener, ServiceTypeListener {

		public void serviceAdded(ServiceEvent event) {
			// Log.i("ADD", "service type = " + event.getType() + ", name = "
			// + event.getName() + ",IP:" + jmdns.getHostName()+",port:");
			sInfo = jmdns.getServiceInfo("_easylink._tcp.local.",
					event.getName());
			if (null != sInfo) {
				Log.i("====", "serviceInfo:" + sInfo.getTextString());
				Log.i("====",
						"Name:" + sInfo.getName() + "Service:"
								+ sInfo.getType() + "IP:" + sInfo.getAddress()
								+ "Mac:" + sInfo.getPriority());
				findDeviceMap.put(event.getName(), sInfo);
			}
		}

		public void serviceRemoved(ServiceEvent event) {
			Log.i("REMOVE", "service type = " + event.getType() + ", name = "
					+ event.getName());
			if (findDeviceMap.containsKey(event.getName())) {
				findDeviceMap.remove(event.getName());
			}
		}

		public void serviceResolved(ServiceEvent event) {
			Log.i("RESOLVE", "service type = " + event.getType() + ", name = "
					+ event.getName());
		}

		public void serviceTypeAdded(ServiceEvent event) {
			Log.i("TYPE-ADDED", "service type = " + event.getType()
					+ ", name = " + event.getName());
		}
	}

	public InetAddress getLocalIpAddress() throws Exception {
		if (wm == null)
			wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiinfo = wm.getConnectionInfo();
		int intaddr = wifiinfo.getIpAddress();
		byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff),
				(byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff),
				(byte) (intaddr >> 24 & 0xff) };
		InetAddress addr = InetAddress.getByAddress(byteaddr);
		return addr;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		searchThreadStart();
//		updateListView();
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
				.setTitle("EasyLink").setMessage("确定要退出吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
						MyActivityManager.getInstance().exit();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				});
		alertDialog.show();
	}
}
