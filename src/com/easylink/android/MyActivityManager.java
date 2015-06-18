package com.easylink.android;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

/**
 * 	储存每一个Activity，并实现关闭所有Activity的操作
 *  单纯为了退出程序用
 * @author Lyon
 * 
 */
public class MyActivityManager
{

	private List<Activity> activityList = new LinkedList<Activity>();
	private static MyActivityManager instance;

	private MyActivityManager()
	{
	}

	public static MyActivityManager getInstance()
	{
		if (null == instance)
		{
			instance = new MyActivityManager();
		}
		return instance;
	}

	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}

	/**
	 * 清空全部插入进来的Activity
	 * 
	 */
	public void subExit()
	{
		for (Activity activity : activityList)
		{
			activity.finish();
		}
		activityList.clear();
		// gc()函数的作用只是提醒虚拟机：程序员希望进行一次垃圾回收。但是它不能保证垃圾回收一定会进行，
		System.gc();
	}

	public void exit()
	{
		for (Activity activity : activityList)
		{
			activity.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.gc();
		System.exit(0);
	}
}
