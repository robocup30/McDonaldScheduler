package com.hiddenpixels.mcdonaldscheduler;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends IntentService {

	public MainService() {
		super("Main Service");
	}

	public MainService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		System.out.println("EXECUTING READING EMAIL FROM MAIN SERVICE");
		new ReadingEmail().execute(this);

	}

}
