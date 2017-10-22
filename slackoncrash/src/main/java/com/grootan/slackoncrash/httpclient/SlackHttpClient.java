package com.grootan.slackoncrash.httpclient;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.grootan.slackoncrash.SlackOnCrash;
import com.grootan.slackoncrash.models.Attachment;
import com.grootan.slackoncrash.models.Field;
import com.grootan.slackoncrash.models.MessageType;
import com.grootan.slackoncrash.models.SlackRequest;
import com.grootan.slackoncrash.utils.DBModel;
import com.grootan.slackoncrash.utils.DatabaseHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lokeshravichandru on 22/10/17.
 */

public class SlackHttpClient {

    private static final String ERROR_COLOR_CODE = "#FF0000";
    private static final String MSG_COLOR_CODE = "#42C487";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient getHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("accept", "application/json");

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();

        return okHttpClient;
    }

    private static SlackRequest getRequest(List<Field> params, MessageType messageType) {
        SlackRequest slackRequest = new SlackRequest();
        Attachment attachment = new Attachment();
        attachment.getFields().addAll(params);
        if (messageType == MessageType.MESSAGE) {
            attachment.setColor(MSG_COLOR_CODE);
        } else if (messageType == MessageType.ERROR) {
            attachment.setColor(ERROR_COLOR_CODE);
        }
        attachment.getFields().add(getVersionName());
        attachment.getFields().add(getDeviceModelName());
        attachment.getFields().add(getOSVersion());
        Field date = getBuildDateAsString();
        if (date != null) {
            attachment.getFields().add(date);
        }
        attachment.getFields().add(getDeviceCategory());
        attachment.getMrkdwnIn().add("fields");
        slackRequest.getAttachments().add(attachment);
        return slackRequest;
    }


    public static void save(final String slackHook, final String message, final List<Field> pars, final MessageType messageType) {

        SlackRequest slackRequest = getRequest(pars, messageType);
        Field field = new Field();
        if (messageType == MessageType.MESSAGE) {
            field.setTitle("Message");
            field.setValue("```" + message + "```");

        } else if (messageType == MessageType.ERROR) {
            field.setTitle("StackTrace");
            field.setValue("```" + message + "```");
        }
        field.setShort(false);
        slackRequest.getAttachments().get(0).getFields().add(field);
        Gson gson = new Gson();
        DBModel model = new DBModel(null, slackHook, gson.toJson(slackRequest));
        DatabaseHelper.getInstance().insertObject(model);
    }

    public static boolean sendMessage(DBModel dbModel) {
        boolean check = false;
        OkHttpClient client = getHttpClient();
        RequestBody body = RequestBody.create(JSON, dbModel.getRequest());
        Request request = new Request.Builder().url(dbModel.getHook()).post(body).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            check = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return check;
    }


    /**
     * INTERNAL method that returns the device model name with correct capitalization.
     * Taken from: http://stackoverflow.com/a/12707479/1254846
     *
     * @return The device model name (i.e., "LGE Nexus 5")
     */
    @NonNull
    private static Field getDeviceModelName() {
        Field field = new Field();
        field.setTitle("DeviceName");
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            field.setValue(capitalize(model));
        } else {
            field.setValue(capitalize(manufacturer) + " " + model);
        }
        field.setShort(true);
        return field;
    }

    @NonNull
    private static Field getOSVersion() {
        Field field = new Field();
        field.setTitle("OSVersion");
        field.setValue(String.valueOf(Build.VERSION.SDK_INT));
        field.setShort(true);
        return field;
    }

    @NonNull
    private static String capitalize(@Nullable String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    @Nullable
    private static Field getBuildDateAsString() {
        Field field = new Field();
        field.setTitle("BuildDate");
        long buildDate;
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            ApplicationInfo ai = SlackOnCrash.getContext().getPackageManager().getApplicationInfo(SlackOnCrash.getContext().getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);

            //If this failed, try with the old zip method
            ZipEntry ze = zf.getEntry("classes.dex");
            buildDate = ze.getTime();


            zf.close();
        } catch (Exception e) {
            buildDate = 0;
        }

        if (buildDate > 312764400000L) {
            field.setValue(dateFormat.format(new Date(buildDate)));
            field.setShort(true);
            return field;
        } else {
            return null;
        }
    }

    @NonNull
    private static Field getVersionName() {
        Field field = new Field();
        field.setTitle("AppVersion");
        try {
            PackageInfo packageInfo = SlackOnCrash.getContext().getPackageManager().getPackageInfo(SlackOnCrash.getContext().getPackageName(), 0);
            field.setValue(packageInfo.versionName+"("+packageInfo.versionCode+")");
        } catch (Exception e) {
            field.setValue("Unknown");
        }
        field.setShort(true);
        return field;
    }

    private static Field getDeviceCategory() {
        Field field = new Field();
        field.setTitle("DeviceCategory");
        field.setValue("Android");
        field.setShort(true);
        return field;
    }

}
