package fr.steve.fresh.dialog.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.function.Supplier;

import fr.steve.fresh.dialog.page.IPage;

public abstract class Dialog<P extends IPage> implements IDialog<P> {

    private Activity activity;

    public Dialog(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public Dialog<P> setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public void buildAlertDialog(String title, Supplier<View> layoutSupplier, String positiveButton, DialogInterface.OnClickListener positiveCallback, String negativeButton, DialogInterface.OnClickListener negativeCallback) {
        new AlertDialog.Builder(activity)
                .setMessage(title)
                .setView(layoutSupplier.get())
                .setPositiveButton(positiveButton, positiveCallback)
                .setNegativeButton(negativeButton, negativeCallback)
                .show();
    }

    public static class LinearLayoutBuilder {

        private final Activity activity;
        private LinearLayout linearLayout;

        public LinearLayoutBuilder(Activity activity) {
            this.activity = activity;
        }

        public LinearLayoutBuilder add(Supplier<View> view) {
            if (linearLayout == null) {
                linearLayout = new LinearLayout(activity);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            }
            linearLayout.addView(view.get());
            return this;
        }

        public LinearLayoutBuilder orient(@LinearLayoutCompat.OrientationMode int orientation) {
            if (linearLayout != null) linearLayout.setOrientation(orientation);
            return this;
        }

        public LinearLayout build() {
            return linearLayout;
        }
    }
}
