# slackoncrash
Small Android library to send crash notificaiton of your app to slack channel

## Download Lib
http://bit.ly/2xX0jFv

## How to use
Add following code to your application class
```
    @Override
    public void onCreate() {
        super.onCreate();
        SlackOnCrash.install(getApplicationContext(),"Your Slack App Web hook");
        SlackOnCrash.addProperty("AppName",getString(R.string.app_name),true); // setting true will allow property as compact
        SlackOnCrash.addProperty("AppId",BuildConfig.APPLICATION_ID,false); // setting false will allow property singleline
        //Here you can add properties to display in slack channel
        SlackOnCrash.start();
        //start can be called in application or in your main activity onresume
    }
        
```

## What you will see in your channel
![alt text](https://github.com/grootan/slackoncrash/blob/master/Screen%20Shot%202017-10-23%20at%201.10.55%20AM.png)

## Notes

The Following fields are added as default for every message.
DeviceCategory
OSVersion
DeviceName
AppVersion
StackTrace

## Roadmap
Allow to record custom or debug messages
Add count of the crash occurs


