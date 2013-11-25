package com.themagpi.android;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class Utils {
	
	public static String getDeviceId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String id = prefs.getString("device_id", null);
		if (id == null) {
			id = generateDeviceId(context);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("device_id", id);
			editor.commit();
		}
		return id;
	}

	private static String generateDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String tmDevice = String.valueOf(tm.getDeviceId());
		String tmSerial = String.valueOf(tm.getSimSerialNumber());
		String androidId = String.valueOf(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String UUID = deviceUuid.toString();
		UUID = UUID.replaceAll("[^a-z0-9]", "");
		while (UUID.length() < 32) {
			UUID = "0" + UUID;
		}
		return UUID;
	}
}
