package com.hengbao.tsm;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class ExitApplication extends Application {

		private List<Activity> activityList=new LinkedList<Activity>();
		private static ExitApplication instance;
		private static Context currentcontext;
		private static int Location ;
		private ExitApplication()
		{
		}
		//����ģʽ�л�ȡΨһ��ExitApplication ʵ��
		public static ExitApplication getInstance()
		{
			if(null == instance)
			 {
				 instance = new ExitApplication();
			 }
			 return instance;
		}
		//���Activity ��������
		public void addActivity(Activity activity)
		{
			currentcontext = activity;
			activityList.add(Location, activity);
			Location += 1;
		}
		
		public Context getActivity()
		{
			return currentcontext;
		}
		//����Activity ��finish��ǰActivity
		public void backAct()
		{
			//currentcontext = activityList.;
			activityList.remove(Location-1);
			Location -= 1;
			currentcontext = activityList.remove(Location-1);;
		}
		
		//��������Activity ��finish
		public void exit()
		{
			 for(Activity activity:activityList)
			 {
			 	activity.finish();
			 }
			 System.exit(0);
		}
 }