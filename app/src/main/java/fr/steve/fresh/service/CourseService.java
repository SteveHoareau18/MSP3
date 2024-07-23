package fr.steve.fresh.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.R;
import fr.steve.fresh.runnable.CourseRunnable;

public class CourseService extends Service {

    private static final String CHANNEL_ID = "CourseChannel";
    private static final int NOTIFICATION_ID = 1;
    private Handler handler;
    private CourseRunnable runnable;
    private WindowManager windowManager;
    private View overlayView;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = buildForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);
        long startMillis = getSharedPreferences(MainActivity.name, MODE_MULTI_PROCESS).getLong(MainActivity.KEY.CHRONO_START.toString(), 0);
        runnable = new CourseRunnable() {
            private long count;

            @Override
            public void run() {
                if (!isBegin) {
                    isBegin = true;
                    count = startMillis / 1000;
                }
                if (count == 0) {
                    SharedPreferences prefs = getSharedPreferences(MainActivity.name, MODE_MULTI_PROCESS);

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("TIME_ELAPSED");
                    broadcastIntent.setPackage(getPackageName());
                    sendBroadcast(broadcastIntent);
                    Notification buildFinishNotification = buildFinishNotification();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, buildFinishNotification);

                    if (!MainActivity.stop(prefs)) {
                        onDestroy();
                    }
                    return;
                }
                continueRunnable();
            }

            private void continueRunnable() {
                SharedPreferences.Editor editor = getSharedPreferences(MainActivity.name, MODE_MULTI_PROCESS).edit();
                editor.putLong(MainActivity.KEY.CHRONO_ELAPSED.toString(), count);
                editor.apply();
                count -= 1;
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence name = "My Channel";
        String description = "Channel for MyService notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 500, 100, 500}); // Same pattern as above

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SteadyWork")
                .setContentText("Vous avez un chronomètre pour travailler serieusement en cours d'execution !")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    private Notification buildOnUseCellphoneNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SteadyWork")
                .setContentText("Vous ne devez pas utiliser le téléphone pendant votre temps de travail !")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }

    private Notification buildFinishNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Fresh")
                .setContentText("Vous devez faire votre course!")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        return builder.build();
    }
}
