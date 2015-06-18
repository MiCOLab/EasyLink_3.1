package com.easylink.android;


public class getIpMac {  
    private ICallBack callBack;
	private String mac;
	private String ipAddr;  
    public void setCallBack(ICallBack callBack) {  
        this.callBack = callBack;  
    }  
    public void doSth() {  
        callBack.onIpCb(ipAddr);
        callBack.onMacCb(mac);
    }  
} 
