package com.themagpi.android;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;

public class MagPiApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
	        .enableLogging()
	        .memoryCacheSize(41943040)
	        .discCacheSize(104857600)
	        .threadPoolSize(10)    
	        .build();
        ImageLoader.getInstance().init(config);
    }
}
