package com.hiddenpixels.mcdonaldscheduler;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
		// AlarmManager alarmManager = (AlarmManager) context
		// .getSystemService(Context.ALARM_SERVICE);
		// Intent serviceIntent = new Intent(context, MainService.class);
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(System.currentTimeMillis());
		// PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
		// 0, serviceIntent, 0);
		// alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		// System.currentTimeMillis() + 10000, 1000 * 10,
		// pendingIntent);
		// }

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			//System.out.println("BOOT COMPLETED SETTING ALARM");
			AlarmReceiver.setAlarm(context);
		}

		// if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
		// Intent serviceIntent = new Intent(context, MainService.class);
		// context.startService(serviceIntent);
		// }
	}
	
	public void startAlarm(){
		AlarmReceiver.setAlarm(getAppContext());
	}

	public static Context getAppContext() {
		return getAppContext();
	}
}