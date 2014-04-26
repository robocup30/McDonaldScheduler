package com.hiddenpixels.mcdonaldscheduler;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	Button updateButton, stopAutoButton, startAutoButton, saveButton, gmailButton, hotmailButton, yahooButton;
	EditText emailEditText, passwordEditText, imapEditText;
	static TextView warningTextView;
	Context context;
	public SharedPreferences pref;
	public static View vg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		updateButton = (Button) findViewById(R.id.updateButton);
		stopAutoButton = (Button) findViewById(R.id.stopAutoButton);
		startAutoButton = (Button) findViewById(R.id.startAutoButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		imapEditText = (EditText) findViewById(R.id.imapEditText);
		gmailButton = (Button) findViewById(R.id.gmailButton);
		hotmailButton = (Button) findViewById(R.id.hotmailButton);
		yahooButton = (Button) findViewById(R.id.yahooButton);
		warningTextView = (TextView) findViewById(R.id.warningTextView);
		vg = findViewById(R.layout.activity_main);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		emailEditText.setText(sharedPref.getString("Email", ""));
		passwordEditText.setText(sharedPref.getString("Password", ""));
		imapEditText.setText(sharedPref.getString("Imap", ""));
		updateButton.setOnClickListener(this);
		
		
		
		saveButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = sharedPref.edit();
				// System.out.println("Email saved as " + emailEditText.getText().toString());
				// System.out.println("Password saved as " + passwordEditText.getText().toString());
				// System.out.println("Imap saved as " + imapEditText.getText().toString());
				editor.putString("Email", emailEditText.getText().toString());
				editor.putString("Password", passwordEditText.getText().toString());
				editor.putString("Imap", imapEditText.getText().toString());
				editor.commit();
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity
								.setWarningText("Saved!");
					}
				});
			}
		});
		startAutoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("STARTING AUTO");
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = sharedPref.edit();
				// System.out.println("Email saved as " + emailEditText.getText().toString());
				// System.out.println("Password saved as " + passwordEditText.getText().toString());
				// System.out.println("Imap saved as " + imapEditText.getText().toString());
				editor.putString("Email", emailEditText.getText().toString());
				editor.putString("Password", passwordEditText.getText().toString());
				editor.putString("Imap", imapEditText.getText().toString());
				editor.commit();
				ReadingEmail.con = context;
				AlarmReceiver.setAlarm(context);
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity
								.setWarningText("Started auto update");
					}
				});
			}
		});
		stopAutoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("STOPPING AUTO");
//				AlarmReceiver.alarmManager.cancel(AlarmReceiver.pendingIntent);
// 				((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).cancel(AlarmReceiver.pendingIntent);
//				try {
//					AlarmReceiver.alarmManager
//							.cancel(AlarmReceiver.pendingIntent);
//				} catch (NullPointerException e) {
//					// System.out.println("NULL POINTER EXCEPTION");
//				}
				ReadingEmail.con = context;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity
								.setWarningText("Stopped auto update");
					}
				});
				AlarmReceiver.setAlarm(context);   // I Dont know why but i get NULL pointer exception without this
				try{
					AlarmReceiver.cancelAlarm();	
				}catch(NullPointerException np){
					// System.out.println("NULL POINTER EXCEPTION CAUGHT, I DONT KNOW WAT TO DO");
				}
			}
		});
		
		// imap-mail.outlook.com
		// imap.gmail.com
		// imap.mail.yahoo.com
		
		hotmailButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				imapEditText.setText("imap-mail.outlook.com");
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = sharedPref.edit();
				// System.out.println("Email saved as " + emailEditText.getText().toString());
				// System.out.println("Password saved as " + passwordEditText.getText().toString());
				// System.out.println("Imap saved as " + imapEditText.getText().toString());
				editor.putString("Email", emailEditText.getText().toString());
				editor.putString("Password", passwordEditText.getText().toString());
				editor.putString("Imap", imapEditText.getText().toString());
				editor.commit();
			}
		});
		gmailButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				imapEditText.setText("imap.gmail.com");
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = sharedPref.edit();
				// System.out.println("Email saved as " + emailEditText.getText().toString());
				// System.out.println("Password saved as " + passwordEditText.getText().toString());
				// System.out.println("Imap saved as " + imapEditText.getText().toString());
				editor.putString("Email", emailEditText.getText().toString());
				editor.putString("Password", passwordEditText.getText().toString());
				editor.putString("Imap", imapEditText.getText().toString());
				editor.commit();
			}
		});
		yahooButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				imapEditText.setText("imap.mail.yahoo.com");
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = sharedPref.edit();
				// System.out.println("Email saved as " + emailEditText.getText().toString());
				// System.out.println("Password saved as " + passwordEditText.getText().toString());
				// System.out.println("Imap saved as " + imapEditText.getText().toString());
				editor.putString("Email", emailEditText.getText().toString());
				editor.putString("Password", passwordEditText.getText().toString());
				editor.putString("Imap", imapEditText.getText().toString());
				editor.commit();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPref.edit();
		// System.out.println("Email saved as " + emailEditText.getText().toString());
		// System.out.println("Password saved as " + passwordEditText.getText().toString());
		// System.out.println("Imap saved as " + imapEditText.getText().toString());
		editor.putString("Email", emailEditText.getText().toString());
		editor.putString("Password", passwordEditText.getText().toString());
		editor.putString("Imap", imapEditText.getText().toString());
		editor.commit();
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MainActivity
						.setWarningText("Checking, it may take a few seconds");
			}
		});
		ReadingEmail.con = this;
		new ReadingEmail().execute(this);
	}
	
	public static void setWarningText(String text){
		warningTextView.setText(text);
	}
	
	public void runUI(Runnable run){
		runOnUiThread(run);
	}

}
