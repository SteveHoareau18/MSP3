package fr.steve.fresh.dialog.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.function.Supplier;

import fr.steve.fresh.dialog.page.IPage;

/**
 * Abstract class for building and managing dialogs in the application.
 * <p>
 * This class provides methods to build and display alert dialogs with custom views and buttons.
 * </p>
 *
 * @param <P> the type of page associated with this dialog.
 */
public abstract class Dialog<P extends IPage> implements IDialog<P> {

    private Activity activity;

    /**
     * Constructs a Dialog instance with the specified activity.
     *
     * @param activity the activity context in which the dialog is displayed.
     */
    public Dialog(Activity activity) {
        this.activity = activity;
    }

    /**
     * Retrieves the activity associated with this dialog.
     *
     * @return the activity context.
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity context for this dialog.
     *
     * @param activity the new activity context.
     * @return the current Dialog instance.
     */
    public Dialog<P> setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * Builds and shows an alert dialog with the specified title, layout, and buttons.
     * <p>
     * This method creates an alert dialog with a custom view provided by the `layoutSupplier`,
     * and configures the positive and negative buttons with their respective callbacks.
     * </p>
     *
     * @param title            the title of the dialog.
     * @param layoutSupplier   a supplier that provides the custom view to be set in the dialog.
     * @param positiveButton   the text for the positive button.
     * @param positiveCallback the callback to be executed when the positive button is clicked.
     * @param negativeButton   the text for the negative button.
     * @param negativeCallback the callback to be executed when the negative button is clicked.
     */
    public void buildAlertDialog(String title, Supplier<View> layoutSupplier, String positiveButton, DialogInterface.OnClickListener positiveCallback, String negativeButton, DialogInterface.OnClickListener negativeCallback) {
        new AlertDialog.Builder(activity)
                .setMessage(title)
                .setView(layoutSupplier.get())
                .setPositiveButton(positiveButton, positiveCallback)
                .setNegativeButton(negativeButton, negativeCallback)
                .show();
    }

    /**
     * Builder class for constructing a linear layout with custom views.
     */
    public static class LinearLayoutBuilder {

        private final Activity activity;
        private LinearLayout linearLayout;

        /**
         * Constructs a LinearLayoutBuilder instance with the specified activity.
         *
         * @param activity the activity context in which the linear layout is created.
         */
        public LinearLayoutBuilder(Activity activity) {
            this.activity = activity;
        }

        /**
         * Adds a view to the linear layout.
         *
         * @param view a supplier that provides the view to be added.
         * @return the current LinearLayoutBuilder instance.
         */
        public LinearLayoutBuilder add(Supplier<View> view) {
            if (linearLayout == null) {
                linearLayout = new LinearLayout(activity);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            }
            linearLayout.addView(view.get());
            return this;
        }

        /**
         * Sets the orientation of the linear layout.
         *
         * @param orientation the orientation mode (e.g., LinearLayout.VERTICAL or LinearLayout.HORIZONTAL).
         * @return the current LinearLayoutBuilder instance.
         */
        public LinearLayoutBuilder orient(@LinearLayoutCompat.OrientationMode int orientation) {
            if (linearLayout != null) linearLayout.setOrientation(orientation);
            return this;
        }

        /**
         * Builds and returns the constructed linear layout.
         *
         * @return the constructed LinearLayout.
         */
        public LinearLayout build() {
            return linearLayout;
        }
    }
}