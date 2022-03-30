package com.journaldev.bundlednotificationsexample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBundleNotification, btnSingleNotification;
    NotificationManager notificationManager;
    int bundleNotificationId = 100;
    int singleNotificationId = 100;
    NotificationCompat.Builder summaryNotificationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        btnBundleNotification = findViewById(R.id.btnBundleNotification);
        btnSingleNotification = findViewById(R.id.btnSingleNotification);
        btnBundleNotification.setOnClickListener(this);
        btnSingleNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBundleNotification:


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel groupChannel = new NotificationChannel("bundle_channel_id", "bundle_channel_name", NotificationManager.IMPORTANCE_LOW);
                    notificationManager.createNotificationChannel(groupChannel);
                }
                bundleNotificationId += 100;
                singleNotificationId = bundleNotificationId;
                String bundle_notification_id = "bundle_notification_" + bundleNotificationId;
                Intent resultIntent = new Intent(this, MainActivity.class);
                resultIntent.putExtra("notification", "Summary Notification Clicked");
                resultIntent.putExtra("notification_id", bundleNotificationId);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, bundleNotificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                summaryNotificationBuilder = new NotificationCompat.Builder(this, "bundle_channel_id")
                        .setGroup(bundle_notification_id)
                        .setGroupSummary(true)
                        .setContentTitle("Bundled Notification. " + bundleNotificationId)
                        .setContentText("Content Text for bundle notification")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(resultPendingIntent);

                notificationManager.notify(bundleNotificationId, summaryNotificationBuilder.build());

                break;

            case R.id.btnSingleNotification:


                bundle_notification_id = "bundle_notification_" + bundleNotificationId;

                resultIntent = new Intent(this, MainActivity.class);
                resultIntent.putExtra("notification", "Summary Notification Clicked");
                resultIntent.putExtra("notification_id", bundleNotificationId);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultPendingIntent = PendingIntent.getActivity(this, bundleNotificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //We need to update the bundle notification every time a new notification comes up.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if (notificationManager.getNotificationChannels().size() < 2) {
                        NotificationChannel groupChannel = new NotificationChannel("bundle_channel_id", "bundle_channel_name", NotificationManager.IMPORTANCE_LOW);
                        notificationManager.createNotificationChannel(groupChannel);
                        NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }
                }
                summaryNotificationBuilder = new NotificationCompat.Builder(this, "bundle_channel_id")
                        .setGroup(bundle_notification_id)
                        .setGroupSummary(true)
                        .setContentTitle("Bundled Notification " + bundleNotificationId)
                        .setContentText("Content Text for group summary")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(resultPendingIntent);


                if (singleNotificationId == bundleNotificationId)
                    singleNotificationId = bundleNotificationId + 1;
                else
                    singleNotificationId++;

                resultIntent = new Intent(this, MainActivity.class);
                resultIntent.putExtra("notification", "Single notification clicked");
                resultIntent.putExtra("notification_id", singleNotificationId);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultPendingIntent = PendingIntent.getActivity(this, singleNotificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "channel_id")
                        .setGroup(bundle_notification_id)
                        .setContentTitle("New Notification " + singleNotificationId)
                        .setContentText("Content for the notification")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setGroupSummary(false)
                        .setContentIntent(resultPendingIntent);

                notificationManager.notify(singleNotificationId, notification.build());
                notificationManager.notify(bundleNotificationId, summaryNotificationBuilder.build());
                break;

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            int notification_id = extras.getInt("notification_id");
            Toast.makeText(getApplicationContext(), "Notification with ID " + notification_id + " is cancelled", Toast.LENGTH_LONG).show();
            notificationManager.cancel(notification_id);
        }
    }
}
