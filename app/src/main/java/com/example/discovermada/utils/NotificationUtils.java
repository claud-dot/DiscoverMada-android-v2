package com.example.discovermada.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.discovermada.R;
import com.example.discovermada.ui.MainActivity;

public class NotificationUtils {

    public void showNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context , MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = null;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            builder =  new NotificationCompat.Builder(context, "discover_welcome_notif");
            builder.setContentTitle(context.getString(R.string.app_name));
            builder.setContentText(context.getString(R.string.welcome_notif_message));
            builder.setSmallIcon(R.drawable.discover_vector);
            builder.setPriority(Notification.PRIORITY_DEFAULT);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
        }

        Notification notification;
        notification = builder.build();
        manager.notify(1 , notification);
    }

    public void createNotificationChannel(NotificationManager manager , Context context){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("discover_welcome_notif", context.getString(R.string.app_name) , NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Le description");

            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null);
            manager.createNotificationChannel(channel);
        }
    }
}
