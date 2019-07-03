package com.example.mymusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Utilities {


    public static NotificationManager mNotificationManager;
    @SuppressWarnings("unused")
    public static void initNotification(String songTitle, Context context)
    {
        try {
            //mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            String ns=Context.NOTIFICATION_SERVICE;
//            mNotificationManager=(NotificationManager)Context.getSystemService(ns);
            int icon = R.drawable.tam9;
            CharSequence tickerText="Playing your song";
            long when=System.currentTimeMillis();

            //notification.flags=Notification.FLAG_ONGOING_EVENT;
            //Context context =mContext.getApplicationContext();
            CharSequence songName=""+ songTitle;
            Intent notificationIntent = new Intent(context,DetailActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent,0);

            Notification.Builder builder = new Notification.Builder(context);
            builder.setTicker(tickerText);
            builder.setContentTitle(songName);
            builder.setSmallIcon(icon);
            builder.setContentIntent(contentIntent);
            builder.setOngoing(true);
            builder.setWhen(when);
            //notification.setLatestEventInfo(context,songName,null,contentIntent);
            //notification=builder.getNotification();
            mNotificationManager.notify(0,builder.build());

        }catch (Exception e) {
            e.printStackTrace();
        }
    }



	 /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
 
        // Convert total duration into time
           int hours = (int)( milliseconds / (1000*60*60));
           int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
           int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
           // Add hours if there
           if(hours > 0){
               finalTimerString = hours + ":";
           }
 
           // Prepending 0 to seconds if it is one digit
           if(seconds < 10){
               secondsString = "0" + seconds;
           }else{
               secondsString = "" + seconds;}
 
           finalTimerString = finalTimerString + minutes + ":" + secondsString;
 
        // return timer string
        return finalTimerString;
    }
    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
 
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
 
        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;
 
        // return percentage
        return percentage.intValue();
    }
    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
 
        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
