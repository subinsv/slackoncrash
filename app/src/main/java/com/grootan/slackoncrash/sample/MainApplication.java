package com.grootan.slackoncrash.sample;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.grootan.slackoncrash.SlackOnCrash;
import com.grootan.slackoncrash.models.SlackRequest;

import java.sql.BatchUpdateException;

/**
 * Created by lokeshravichandru on 22/10/17.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SlackOnCrash.install(getApplicationContext(),"https://hooks.slack.com/services/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");
        SlackOnCrash.addProperty("AppName",getString(R.string.app_name),true);
        SlackOnCrash.addProperty("Flavour",BuildConfig.FLAVOR,true);
        SlackOnCrash.addProperty("AppId",BuildConfig.APPLICATION_ID,false);
        SlackOnCrash.start();
    }
}
