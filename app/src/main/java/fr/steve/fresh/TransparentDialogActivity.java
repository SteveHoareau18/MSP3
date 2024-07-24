package fr.steve.fresh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.steve.fresh.listener.CourseServiceReceiver;

/**
 * Activity for displaying a transparent dialog.
 * <p>
 * This activity is used to show a dialog with a title, message, and a button.
 * It also stops a ringtone when the button is clicked.
 * </p>
 */
public class TransparentDialogActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * <p>
     * This method sets up the user interface and initializes views.
     * It retrieves the title and message from the intent and updates the UI accordingly.
     * </p>
     *
     * @param savedInstanceState a Bundle containing the activity's previously saved state.
     *                           If the activity has never been created before, this value is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_dialog);

        // Retrieve the views
        TextView titleTextView = findViewById(R.id.dialog_title);
        TextView messageTextView = findViewById(R.id.dialog_message);
        Button button = findViewById(R.id.dialog_button);

        // Retrieve the data passed by the intent (if needed)
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");

        // Update the views with the data
        titleTextView.setText(title);
        messageTextView.setText(message);

        button.setOnClickListener(this::onDialogButtonClick);
    }

    /**
     * Handles the button click event.
     * <p>
     * This method finishes the activity and stops the ringtone using the CourseServiceReceiver.
     * </p>
     *
     * @param view the view that was clicked.
     */
    public void onDialogButtonClick(View view) {
        finish();
        CourseServiceReceiver.stopRingtone();
    }
}
