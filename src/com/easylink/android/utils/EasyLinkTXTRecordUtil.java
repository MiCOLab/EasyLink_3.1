package com.easylink.android.utils;

public class EasyLinkTXTRecordUtil {
	public static String setDeviceName(String inDeviceName) {
		try {
			if (null != inDeviceName && !"".equals(inDeviceName)
					&& inDeviceName.contains("#")) {
				return inDeviceName.substring(0, inDeviceName.indexOf("#"));
			} else {
				return inDeviceName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inDeviceName;
	}

	public static String setDeviceMac(String inDeviceMac) {
		try {
			if (null != inDeviceMac && !"".equals(inDeviceMac)
					&& inDeviceMac.contains("MAC")) {
				return inDeviceMac.substring(inDeviceMac.indexOf("MAC=") + 4,
						inDeviceMac.indexOf("MAC=") + 21);
			} else {
				return inDeviceMac;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inDeviceMac;
	}

	public static String setDeviceIP(String inDeviceIP) {
		try {
			if (null != inDeviceIP && !"".equals(inDeviceIP)
					&& inDeviceIP.contains("/")) {
				return inDeviceIP.substring(1);
			} else {
				return inDeviceIP;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inDeviceIP;
	}

	public static String setHardwareRev(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("Hardware Rev")
					&& strInputString.contains("MICO OS Rev")) {
				int startIndex = strInputString.indexOf("Hardware Rev") + 13;
				int endIndex = strInputString.indexOf("MICO OS Rev");
				return strInputString.substring(startIndex, endIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

	public static String setManufacturer(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("Manufacturer=")
					&& strInputString.contains("Seed=")) {
				int startIndex = strInputString.indexOf("Manufacturer=") + 13;
				int endIndex = strInputString.indexOf("Seed=");
				return strInputString.substring(startIndex, endIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

	public static String setMac(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("MAC=")
					&& strInputString.contains("Firmware Rev=")) {
				int startIndex = strInputString.indexOf("MAC=") + 4;
				int endIndex = strInputString.indexOf("Firmware Rev=") - 1;
				return strInputString.substring(startIndex, endIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

	public static String setSeed(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("Seed=")) {
				int startIndex = strInputString.indexOf("Seed=") + 5;
				// int endIndex = strInputString.indexOf("Firmware Rev=");
				return strInputString.substring(startIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

	public static String setModel(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("Model=")
					&& strInputString.contains("Protocol=")) {
				int startIndex = strInputString.indexOf("Model=") + 6;
				int endIndex = strInputString.indexOf("Protocol=");
				return strInputString.substring(startIndex, endIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

	public static String setProtocol(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("Protocol=")
					&& strInputString.contains("Manufacturer=")) {
				int startIndex = strInputString.indexOf("Protocol=") + 9;
				int endIndex = strInputString.indexOf("Manufacturer=");
				return strInputString.substring(startIndex, endIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

	public static String setMICOOSRev(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("MICO OS Rev=")
					&& strInputString.contains("Model=")) {
				int startIndex = strInputString.indexOf("MICO OS Rev=") + 12;
				int endIndex = strInputString.indexOf("Model=");
				return strInputString.substring(startIndex, endIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

	public static String setFirmwareRev(String strInputString) {
		try {
			if (null != strInputString && !"".equals(strInputString)
					&& strInputString.contains("Firmware Rev=")
					&& strInputString.contains("Hardware Rev=")) {
				int startIndex = strInputString.indexOf("Firmware Rev=") + 13;
				int endIndex = strInputString.indexOf("Hardware Rev=");
				return strInputString.substring(startIndex, endIndex);
			} else {
				return strInputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strInputString;
	}

}
