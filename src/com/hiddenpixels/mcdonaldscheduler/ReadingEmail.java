package com.hiddenpixels.mcdonaldscheduler;

import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

import org.jsoup.Jsoup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Patterns;

public class ReadingEmail extends AsyncTask<Context, Void, Void> {

	public static Context con;

	@Override
	protected Void doInBackground(Context... params) {
		// TODO Auto-generated method stub
		System.out.println("BEGIN READING EMAIL");
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(params[0]);
		SharedPreferences.Editor editor = sharedPref.edit();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.setTimeInMillis(cal.getTimeInMillis() - 604800000); // week before
																// current

		long lastCheckedEmailDate = sharedPref.getLong("LastEmailChecked",
				cal.getTimeInMillis()); // get date of last checked email. If
										// there is none, get current date - 1
										// week
		// check until this date has been reached

		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			// imap-mail.outlook.com
			// imap.gmail.com
			// imap.mail.yahoo.com
			String imap = sharedPref.getString("Imap", "");
			String email = sharedPref.getString("Email", "");
			String password = sharedPref.getString("Password", "");
			if (email.equals("")) {
				email = "someemail@example.com";
			}
			store.connect(imap, email, password);
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);
			editor.commit();

			int i = 0;
			long dateToSave = lastCheckedEmailDate;
			while (true) {
				System.out.println("CHECKING " + i + "TH EMAIL");
				if (i == inbox.getMessageCount()) {
					System.out.println("BROKEN LOOP DUE TO MESSAGE COUNT");
					editor.putLong("LastEmailChecked", dateToSave);
					editor.commit();
					break;
				}

				Message msg = inbox.getMessage(inbox.getMessageCount() - i);
				if (i == 0 || dateToSave == lastCheckedEmailDate) {
					try {
						dateToSave = msg.getSentDate().getTime();
					} catch (MessagingException me) {
						me.printStackTrace();
						i++;
						continue;
					}
				}

				try {
					if (msg.getSentDate().getTime() <= lastCheckedEmailDate) {
						System.out.println("BROKEN LOOP DUE TO DATE REACHED");
						editor.putLong("LastEmailChecked", dateToSave);
						editor.commit();
						break;
					}
				} catch (MessagingException me) {
					me.printStackTrace();
					i++;
					continue;
				}

				Address[] in = msg.getFrom();

				for (Address address : in) {
					if (address.toString().contains("mcdonalds")) {
						if (msg.getContentType().contains("TEXT")) {
							// THIS ONE
							String contentString = (String) Jsoup.parse(
									(String) msg.getContent()).text();
							contentString = contentString
									.substring(
											contentString
													.indexOf("Here is your schedule for the week of"),
											contentString
													.indexOf("If you have any questions about your schedule"));
							System.out.println(contentString);
							String contentStringDates = contentString
									.substring(contentString.indexOf(":") + 2,
											contentString.indexOf("You"));
							System.out.println(contentStringDates);
							if (contentStringDates
									.contains("No shifts for this week")) {
								continue;
							}
							String[] dateStrings = contentStringDates
									.split("Sunday, |Monday, |Tuesday, |Wednesday, |Thursday, |Friday, |Saturday, ");
							for (int k = 1; k < dateStrings.length; k++) {
								String dateString = dateStrings[k];
								System.out.println("");
								System.out.println(dateString);
								System.out.println("Time is from "
										+ dateString.substring(
												dateString.indexOf(",") + 7,
												dateString.indexOf("-")));
								System.out
										.println("Time is to "
												+ dateString.substring(
														dateString.indexOf("-") + 2,
														dateString
																.indexOf(
																		",",
																		dateString
																				.indexOf(",") + 7)));

								long calID = 1;
								long startMillis = 0;
								long endMillis = 0;
								int year = Integer.parseInt(dateString
										.substring(dateString.indexOf(",") + 2,
												dateString.indexOf(",") + 6));
								int month = 0;
								int day = Integer.parseInt(dateString
										.substring(dateString.indexOf(" ") + 1,
												dateString.indexOf(",")));
								int beginHour = Integer.parseInt(dateString
										.substring(dateString.indexOf(",") + 7,
												dateString.indexOf(":")));
								int beginMinute = Integer.parseInt(dateString
										.substring(dateString.indexOf(":") + 1,
												dateString.indexOf(":") + 3));
								boolean beginIsAM = dateString.substring(
										dateString.indexOf(":") + 4,
										dateString.indexOf(":") + 6)
										.equalsIgnoreCase("AM");
								if (!beginIsAM) {
									beginHour = beginHour + 12;
								}
								int endHour = Integer
										.parseInt(dateString.substring(
												dateString.indexOf("-") + 2,
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2)));
								int endMinute = Integer
										.parseInt(dateString.substring(
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 1,
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 3));
								boolean endIsAM = dateString
										.substring(
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 4,
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 6)
										.equalsIgnoreCase("AM");
								if (!endIsAM) {
									endHour = endHour + 12;
								}
								if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("january")) {
									month = 0;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("february")) {
									month = 1;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("march")) {
									month = 2;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("april")) {
									month = 4;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("may")) {
									month = 4;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("june")) {
									month = 5;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("july")) {
									month = 6;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("august")) {
									month = 7;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("september")) {
									month = 8;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("october")) {
									month = 9;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("november")) {
									month = 10;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("december")) {
									month = 11;
								}
								System.out.println("Year is " + year);
								System.out.println("Month is " + month);
								System.out.println("Day is " + day);
								System.out
										.println("Begin Hour is " + beginHour);
								System.out.println("Begin Minute is "
										+ beginMinute);
								System.out.println("End Hour is " + endHour);
								System.out
										.println("End Minute is " + endMinute);

								Calendar beginTime = Calendar.getInstance();
								beginTime.set(year, month, day, beginHour,
										beginMinute);
								startMillis = beginTime.getTimeInMillis();
								Calendar endTime = Calendar.getInstance();
								endTime.set(year, month, day, endHour,
										endMinute);
								endMillis = endTime.getTimeInMillis();

								// ///////////////////////////////////////////////
								ContentResolver cr = params[0]
										.getContentResolver();
								ContentValues values = new ContentValues();
								values.put(Events.DTSTART, startMillis);
								values.put(Events.DTEND, endMillis);
								values.put(Events.TITLE, "McD");
								values.put(Events.DESCRIPTION, "Work at McD");
								values.put(Events.CALENDAR_ID, calID);

								TimeZone tz = TimeZone.getDefault();
								values.put(Events.EVENT_TIMEZONE, tz.getID());
								Uri uri = cr.insert(Events.CONTENT_URI, values);
								long eventID = Long.parseLong(uri
										.getLastPathSegment());

								values = new ContentValues();
								values.put(Reminders.MINUTES, 1440);
								values.put(Reminders.EVENT_ID, eventID);
								values.put(Reminders.METHOD,
										Reminders.METHOD_ALERT);
								uri = cr.insert(Reminders.CONTENT_URI, values);
							}

						} else if (msg.getContentType().contains(("multipart"))) {
							Multipart mp = (Multipart) msg.getContent();
							BodyPart bp = mp.getBodyPart(0);
							String contentString = (String) bp.getContent();
							System.out.println(contentString);

							contentString = contentString
									.substring(
											contentString
													.indexOf("Here is your schedule for the week of"),
											contentString
													.indexOf("If you have any questions about your schedule"));
							System.out.println(contentString);
							String contentStringDates = contentString
									.substring(contentString.indexOf(":") + 2,
											contentString.indexOf("You"));
							System.out.println(contentStringDates);
							if (contentStringDates
									.contains("No shifts for this week")) {
								continue;
							}
							String[] dateStrings = contentStringDates
									.split("Sunday, |Monday, |Tuesday, |Wednesday, |Thursday, |Friday, |Saturday, ");
							for (int k = 1; k < dateStrings.length; k++) {
								String dateString = dateStrings[k];
								System.out.println("");
								System.out.println(dateString);
								System.out.println("Time is from "
										+ dateString.substring(
												dateString.indexOf(",") + 7,
												dateString.indexOf("-")));
								System.out
										.println("Time is to "
												+ dateString.substring(
														dateString.indexOf("-") + 2,
														dateString
																.indexOf(
																		",",
																		dateString
																				.indexOf(",") + 7)));

								long calID = 1;
								long startMillis = 0;
								long endMillis = 0;
								int year = Integer.parseInt(dateString
										.substring(dateString.indexOf(",") + 2,
												dateString.indexOf(",") + 6));
								int month = 0;
								int day = Integer.parseInt(dateString
										.substring(dateString.indexOf(" ") + 1,
												dateString.indexOf(",")));
								int beginHour = Integer.parseInt(dateString
										.substring(dateString.indexOf(",") + 7,
												dateString.indexOf(":")));
								int beginMinute = Integer.parseInt(dateString
										.substring(dateString.indexOf(":") + 1,
												dateString.indexOf(":") + 3));
								boolean beginIsAM = dateString.substring(
										dateString.indexOf(":") + 4,
										dateString.indexOf(":") + 6)
										.equalsIgnoreCase("AM");
								if (!beginIsAM) {
									beginHour = beginHour + 12;
								}
								int endHour = Integer
										.parseInt(dateString.substring(
												dateString.indexOf("-") + 2,
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2)));
								int endMinute = Integer
										.parseInt(dateString.substring(
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 1,
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 3));
								boolean endIsAM = dateString
										.substring(
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 4,
												dateString
														.indexOf(
																":",
																dateString
																		.indexOf("-") + 2) + 6)
										.equalsIgnoreCase("AM");
								if (!endIsAM) {
									endHour = endHour + 12;
								}
								if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("january")) {
									month = 0;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("february")) {
									month = 1;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("march")) {
									month = 2;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("april")) {
									month = 4;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("may")) {
									month = 4;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("june")) {
									month = 5;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("july")) {
									month = 6;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("august")) {
									month = 7;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("september")) {
									month = 8;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("october")) {
									month = 9;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("november")) {
									month = 10;
								} else if (dateString.substring(0,
										dateString.indexOf(" "))
										.equalsIgnoreCase("december")) {
									month = 11;
								}
								System.out.println("Year is " + year);
								System.out.println("Month is " + month);
								System.out.println("Day is " + day);
								System.out
										.println("Begin Hour is " + beginHour);
								System.out.println("Begin Minute is "
										+ beginMinute);
								System.out.println("End Hour is " + endHour);
								System.out
										.println("End Minute is " + endMinute);

								Calendar beginTime = Calendar.getInstance();
								beginTime.set(year, month, day, beginHour,
										beginMinute);
								startMillis = beginTime.getTimeInMillis();
								Calendar endTime = Calendar.getInstance();
								endTime.set(year, month, day, endHour,
										endMinute);
								endMillis = endTime.getTimeInMillis();

								// ///////////////////////////////////////////////
								ContentResolver cr = params[0]
										.getContentResolver();
								ContentValues values = new ContentValues();
								values.put(Events.DTSTART, startMillis);
								values.put(Events.DTEND, endMillis);
								values.put(Events.TITLE, "McD");
								values.put(Events.DESCRIPTION, "Work at McD");
								values.put(Events.CALENDAR_ID, calID);

								TimeZone tz = TimeZone.getDefault();
								values.put(Events.EVENT_TIMEZONE, tz.getID());
								Uri uri = cr.insert(Events.CONTENT_URI, values);
								long eventID = Long.parseLong(uri
										.getLastPathSegment());

								values = new ContentValues();
								values.put(Reminders.MINUTES, 1440);
								values.put(Reminders.EVENT_ID, eventID);
								values.put(Reminders.METHOD,
										Reminders.METHOD_ALERT);
								uri = cr.insert(Reminders.CONTENT_URI, values);
							}
						}
					}
				}
				i++;
			}
			System.out.println("FINISHED READING EMAILS");
			try {
				((MainActivity) con).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity.setWarningText("Completed successfully!");
					}
				});
			} catch (Exception e) {

			}
		} catch (AuthenticationFailedException afex) {
			System.out.println("AUTHENTICATION FAILED");
			try {
				((MainActivity) con).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity
								.setWarningText("Authentication failed, check email/password");
					}
				});
			} catch (Exception e) {
				// e.printStackTrace();
			}
			// afex.printStackTrace();
		} catch (MessagingException meex) {
			System.out.println("FAILED TO CONNECT TO HOST, CHECK IMAP");
			meex.printStackTrace();
			try {
				((MainActivity) con).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity
								.setWarningText("Connection failed, check IMAP address");
					}
				});
			} catch (Exception e) {
				// System.out.println("SOME ERROR");
				// e.printStackTrace();
			}
			// meex.printStackTrace();
		} catch (Exception mex) {
			// System.out.println("EXCEPTION OCCURED");
			mex.printStackTrace();
		}
		return null;
	}
}