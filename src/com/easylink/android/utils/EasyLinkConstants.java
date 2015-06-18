package com.easylink.android.utils;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class EasyLinkConstants 
{
	/**
	 * Dialog ID constant for wifi available
	 */
	public static final int DLG_NO_WIFI_AVAILABLE=1;
	/**
	 * Dialog ID constant for SSID invalid
	 */
	public static final int DLG_SSID_INVALID=2;
	/**
	 * Dialog ID constant for password invalid
	 */
	public static final int DLG_PASSWORD_INVALID=3;
	/**
	 * Dialog ID constant for gateway ip invalid
	 */
	public static final int DLG_GATEWAY_IP_INVALID=4;
	/**
	 * Dialog ID constant for encryption key invalid
	 */
	public static final int DLG_KEY_INVALID=5;
	/**
	 * Dialog ID constant for success callback alert
	 */
	public static final int DLG_CONNECTION_SUCCESS=6;
	/**
	 * Dialog ID constant for failure callback alert
	 */
	public static final int DLG_CONNECTION_FAILURE=7;
	/**
	 * Dialog ID constant for timeout alert
	 */
	public static final int DLG_CONNECTION_TIMEOUT=8;
	/**
	 * Dialog ID constant for time delay in showing splash screen
	 */
	public static final int SPLASH_DELAY=1500;
	/**
	 * 
	 */
	public static final int DEVICE_FIND_SUCCESS=1;
	public static Map<String,String> findedDevicesList=new HashMap<String,String>();
	public static Map<String,Socket> findedDevicesSocketList=new HashMap<String,Socket>();


}
