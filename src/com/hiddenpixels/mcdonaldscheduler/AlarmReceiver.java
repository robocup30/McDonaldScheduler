package com.hiddenpixels.mcdonaldscheduler;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	public static AlarmManager alarmManager;
	public static PendingIntent pendingIntent;
	public static Intent serviceIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("CHECKING INTENT");
		if(intent.getAction() == "ReadEmail"){
			System.out.println("EXECUTING READING EMAIL FROM MAIN SERVICE");
			new ReadingEmail().execute(context);	
		}

	}

	public static void setAlarm(Context context) {
		// TODO Auto-generated method stub
		System.out.println("ALARM HAS BEEN SET");
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		serviceIntent = new Intent(context, AlarmReceiver.class);
		serviceIntent.setAction("ReadEmail");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		pendingIntent = PendingIntent.getBroadcast(context, 0,
				serviceIntent, 0);
		alarmManager.setInexactRepeating(AlarmManager.RTC,
				System.currentTimeMillis() + 30000, 21600000, pendingIntent);
	}
	// 3600000 = 1 hour
	// 14400000 = 4 hour
	// 30000 = 30 sec
	// 300000 = 5 minutes
	// 21600000 = 6 hours
	
	public static void cancelAlarm(){
		System.out.println("CANCELLING ALARM");
		alarmManager.cancel(pendingIntent);
		System.out.println("ALARM CANCELLED");
	}

}
