package com.grootan.slackoncrash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.grootan.slackoncrash.httpclient.SlackHttpClient;
import com.grootan.slackoncrash.models.Field;
import com.grootan.slackoncrash.models.MessageType;
import com.grootan.slackoncrash.utils.DBModel;
import com.grootan.slackoncrash.utils.DatabaseHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by lokeshravichandru on 22/10/17.
 */

public final class SlackOnCrash {

    private final static String TAG = "SlackOnCrash";

    private static final String DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os";
    private static final String SLACK_HANDLER_PACKAGE_NAME = "com.grootan.slackoncrash";

    @SuppressLint("StaticFieldLeak") //This is an application-wide component
    private static Application _application;

    private static List<Field> _params;

    private static String _slackHook;

    private static Context _context;

    /**
     * Setup SlackOnCrash on the application using the default error messanger.
     *
     * @param context   Context to use for obtaining the ApplicationContext. Must not be null.
     * @param slackHook SlackHook to send out messages visit https://api.slack.com/slack-apps for more details
     */
    public static void install(@Nullable final Context context, @Nullable final String slackHook) {
        try {

            if (context == null) {
                Log.e(TAG, "Install failed: context is null!");
            } else if (slackHook == null || slackHook == "") {
                Log.e(TAG, "Install failed: slack hook cannot be null or empty!");
            } else {
                _context = context;
                DatabaseHelper.initialize(context, context.getDatabasePath("SLACKONCRASH.db"));
                _slackHook = slackHook;
                //execute();
                //INSTALL!
                final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();

                if (oldHandler != null && oldHandler.getClass().getName().startsWith(SLACK_HANDLER_PACKAGE_NAME)) {
                    Log.e(TAG, "SlackOnCrash was already installed, doing nothing!");
                } else {
                    if (oldHandler != null && !oldHandler.getClass().getName().startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                        Log.e(TAG, "IMPORTANT WARNING! You already have an UncaughtExceptionHandler, are you sure this is correct? If you use a custom UncaughtExceptionHandler, you must initialize it AFTER SlackOnCrash! Installing anyway, but your original handler will not be called.");
                    }

                    _application = (Application) context.getApplicationContext();
                    //We define a default exception handler that does what we want so it can be called from Crashlytics/ACRA
                    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread thread, final Throwable throwable) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            throwable.printStackTrace(pw);
                            String stackTraceString = sw.toString().replaceAll("\n\t","\n");
                            SlackHttpClient.save(slackHook, stackTraceString, _params, MessageType.ERROR);
                            killCurrentProcess();
                        }
                    });
                }

                Log.i(TAG, "SlackOnCrash has been installed.");
            }
        } catch (Throwable t) {
            Log.e(TAG, "An unknown error occurred while installing SlackOnCrash, it may not have been properly initialized. Please report this as a bug if needed.", t);
        }
    }

    private static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    public static Context getContext()
    {
        return _context;
    }

    /**
     * Adds property to display as fields in slack message attachments. This can be called from any place to set
     *
     * @param key   to set as a heading for message field. Must not be null.
     * @param value to set value for message field. Must not be null
     */

    public static void addProperty(String key, String value,boolean isOneLine) {
        if (key == null || key == "" || value == null || value == "") {
            Log.e(TAG, "Key and Value must have a value");
        }
        if (_params == null) {
            _params = new ArrayList<>();
        }
        Field field =new Field();
        field.setTitle(key);
        field.setValue(value);
        field.setShort(isOneLine);
        _params.add(field);
    }

    public static void start() {
        if(isNetworkAvailable()) {
            new AsyncJob.AsyncJobBuilder<Boolean>()
                    .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                        @Override
                        public Boolean doAsync() {
                            List<DBModel> models = DatabaseHelper.getInstance().getAll();
                            for (DBModel model : models) {
                                SlackHttpClient.sendMessage(model);
                                DatabaseHelper.getInstance().deleteObject(model.getId());
                            }
                            return true;
                        }
                    }).create().start();
        }
    }


    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }






}
