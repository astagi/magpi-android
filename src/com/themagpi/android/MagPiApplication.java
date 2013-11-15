package com.themagpi.android;

import java.io.File;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;

public class MagPiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        File magpiDir = new File (Config.ISSUE_FOLDER);
        magpiDir.mkdirs();
        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .memoryCacheSize(41943040)
            .discCacheSize(104857600)
            .threadPoolSize(10)    
            .build();
        ImageLoader.getInstance().init(config);
    }
}
