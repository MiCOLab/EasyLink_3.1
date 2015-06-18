package com.easylink.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.easylink.android.FirstTimeConfig2.MyService;

public class ServiceThread implements Runnable {
	
	public void onIpCb(String ip){}
    public void onMacCb(String mac){}
	// ���嵱ǰ�̴߳����Socket
	Socket s = null;
	byte[] buffer = new byte[2048];
	String data = null;
	String mac = null;
	String ip = null;
	int count = 0;
	// ���߳��������Socket����Ӧ��������
	private InputStream inStream = null;

	public ServiceThread(Socket s) {
		this.s = s;
		try {
			inStream = s.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (readFromClient() > 0) {
			// ����socketList�е�ÿ��Socket
			// ����ȡ��������ÿ����Socket����һ��
			for (Socket s : MyService.socketList) {
				OutputStream os;
				try {
					data = new String(buffer);
					os = s.getOutputStream();
					os.write("HTTP/1.1 202 Accepted\r\nContent-Type: application/json\r\nConnection: keep-alive\r\n\r\n"
							.getBytes());
					data = data.substring(data.indexOf("\r\n\r\n")+4);
					JSONObject jsonObj = new JSONObject(data);
					JSONArray contentArray=jsonObj.getJSONArray("C");
					JSONObject object0 = contentArray.getJSONObject(0);
					JSONArray dataArray=object0.getJSONArray("C");
					JSONObject object1 = dataArray.getJSONObject(1);
					mac = object1.getString("C");
					onMacCb(mac);
					JSONObject object2 = dataArray.getJSONObject(2);
					ip = object2.getString("C");
					onIpCb(ip);
					
					Thread.sleep(1000);
					os.write("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: 3\r\nConnection: keep-alive\r\n\r\n{ }"
							.getBytes());
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	// �����ȡ�ͻ��˵���Ϣ
	public int readFromClient() {
		try {
			return count = inStream.read(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

}
