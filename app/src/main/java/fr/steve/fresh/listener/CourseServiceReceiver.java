package fr.steve.fresh.listener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import fr.steve.fresh.TransparentDialogActivity;

//when service is finish
public class CourseServiceReceiver extends BroadcastReceiver {

    private static Ringtone ringtone;
    private Handler handler;

    public static void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("TIME_ELAPSED".equals(intent.getAction())) {
            showAlert(context);
        }
    }

    @SuppressLint("DefaultLocale")
    private void showAlert(Context context) {
        Intent intentTransparent = new Intent(context, TransparentDialogActivity.class);
        intentTransparent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentTransparent.putExtra("title", "Vous devez faire votre course");
        context.startActivity(intentTransparent);

        playRingtone(context);
    }

    private void playRingtone(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(context, notification);
        ringtone.play();
        handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            private long count;
            private boolean isBegin = false;

            @Override
            public void run() {
                if (!isBegin) {
                    isBegin = true;
                    count = 0;
                }
                if (count == 7) {
                    stopRingtone();
                    handler.removeCallbacks(this);
                } else {
                    handler.postDelayed(this, 1000);
                    count += 1;
                }
            }
        };
        handler.post(runnable);
    }
}
