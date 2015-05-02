package com.xebia.eventschedule.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.details.TalkActivity;
import com.xebia.eventschedule.model.Talk;

/**
 * A BroadcastReceiver to handle Intents sent by the AlarmManager for local notifications.
 */
public class FavoritesNotificationReceiver extends BroadcastReceiver {
    /**
     * When the notification happens, it will vibrate for 750ms after 0ms.
     */
    private static final long[] VIBRATION = {0, 750};

    /**
     * Shows a notification for the given talk. Clicking the notification will open the talk.
     */
    private static void showStartNotification(Talk talk, final Context context) {
        if (!talk.isDataAvailable()) {
            throw new RuntimeException("Talk should have been fetched.");
        }

        // Set up an Intent to open the talk, with the back button going back to the schedule.
        Intent talkIntent = new Intent(context, TalkActivity.class);
        talkIntent.setData(talk.getUri());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(TalkActivity.class);
        stackBuilder.addNextIntent(talkIntent);
        PendingIntent talkPendingIntent = stackBuilder.getPendingIntent(0,
            PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the UI for the notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(R.style.Theme_EventSchedule, new int[]{R.attr.notificationIconId});
        int attributeResourceId = a.getResourceId(0, 0);
        a.recycle();

        builder.setSmallIcon(attributeResourceId);

        builder.setColor(context.getResources().getColor(R.color.notificationIconBackground));
        builder.setContentTitle(talk.getTitle());
        builder.setContentText(null != talk.getRoom() ? "Starts in 5 minutes in " + talk.getRoom().getName()
            : "Starts in 5 minutes");
        builder.setContentIntent(talkPendingIntent);
        builder.setAutoCancel(true);
        builder.setVibrate(VIBRATION);
        String talkAbstract = null != talk.getAbstract() ? "\nAbstract: " + talk.getAbstract() : "";
        Notification notification = new NotificationCompat.BigTextStyle(builder)
            .setBigContentTitle(talk.getTitle())
            .bigText(null != talk.getRoom() ? "Starts in 5 minutes in " + talk.getRoom().getName() + talkAbstract
                : "Starts in 5 minutes" + talkAbstract)

            .build();



    /*
     * Display the notification. We use the label "start" to identify this kind of notification.
     * That would be useful for cancelling the notification if we wanted.
     */
        NotificationManager manager = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify("start", talk.getObjectId().hashCode(), notification);
    }

    /**
     * This is called by Android to deliver the Intent to the receiver.
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        // Grab the data for the talk this Intent is for.
        String talkId = Talk.getTalkId(intent.getData());
        Talk.getInBackground(talkId, new GetCallback<Talk>() {
            @Override
            public void done(Talk talk, ParseException e) {
                if (talk != null) {
                    showStartNotification(talk, context);
                }
            }
        });
    }
}
