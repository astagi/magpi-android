<p align="center">
  <img src="https://raw.github.com/astagi/magpi-android/master/pub/TheMagPi.png" alt="MagPi client for Android."/>
</p>

MagPi for Android
-----------

MagPi client for Android. Read MagPi issues with your Android device :)

Dependencies
------------

- ActionBarSherlock (http://actionbarsherlock.com)
- Async-http (http://loopj.com/android-async-http)
- Android-rss (https://github.com/ahorn/android-rss)
- GCM for Android (http://developer.android.com/google/gcm/gs.html#libs)

Add configuration file
----------------------

First thing you've to do is adding a Config.java file under src/com/themagpi/android

	public class Config {
		public static final String SENDER_ID = "17516895224";
		public static final String SERVICE_URL = "http://andreastagi.dyndns.org";
	}

leave SENDER_ID and SERVICE_URL equal to "" if you don't need to use push notifications

How to build and install
------------------------

Requires:

- Apache Ant http://ant.apache.org
- Android SDK http://developer.android.com/sdk

Steps:

- Set ANDROID_SDK environment variable with the path where your Android SDK folder is placed.
- Run "make" to build the .apk
- Run "make upload" to launch the app on your device. For any problem read the YMCA Makefile.

Alternatively you can import all the source in a new Android project and use eclipse to build and install.

License
-------

This software is released under MIT License. Copyright (c) 2013 MagPi <emailthemagpi@gmail.com>, Andrea Stagi <stagi.andrea@gmail.com>
