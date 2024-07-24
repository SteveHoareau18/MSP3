package fr.steve.fresh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import fr.steve.fresh.dialog.ErrandDialog;
import fr.steve.fresh.dialog.ProductDialog;
import fr.steve.fresh.entity.Errand;

/**
 * An activity that displays detailed information about a specific errand and manages interactions
 * with products related to that errand.
 * <p>
 * This activity allows users to view and edit errand details, add products to the errand, and mark
 * the errand as finished. It also provides navigation back to the main activity.
 * </p>
 */
public class ErrandActivity extends Activity {

    private static WeakReference<ErrandActivity> activityReference;
    private ErrandDialog errandDialog;

    private ListView listProducts;
    private TextView titleProducts;

    /**
     * Gets a weak reference to the current instance of {@code ErrandActivity}.
     * <p>
     * This method throws a {@link RuntimeException} if the activity reference is {@code null}.
     * </p>
     *
     * @return a weak reference to the current {@code ErrandActivity} instance
     */
    public static WeakReference<ErrandActivity> getActivityReference() {
        if (activityReference == null) throw new RuntimeException("Activity not found");
        return activityReference;
    }

    /**
     * Gets the {@code ListView} for displaying products.
     *
     * @return the {@code ListView} for products
     */
    public ListView getListProducts() {
        return listProducts;
    }

    /**
     * Gets the {@code TextView} for displaying the title of the products list.
     *
     * @return the {@code TextView} for the products list title
     */
    public TextView getTitleProducts() {
        return titleProducts;
    }

    /**
     * Initializes the activity by setting the content view and configuring UI elements and
     * event listeners. Retrieves the errand details and sets up interactions for modifying the
     * errand and managing its products.
     * <p>
     * This method also handles navigation back to the main activity.
     * </p>
     *
     * @param savedInstanceState the previously saved instance state (if any)
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityReference = new WeakReference<>(this);

        setContentView(R.layout.course_activity);

        listProducts = findViewById(R.id.list_products);
        titleProducts = findViewById(R.id.textview_historic_product_title);

        int id = getIntent().getIntExtra("errand_id", -1);

        if (id == -1) throw new RuntimeException("Errand not found");

        errandDialog = new ErrandDialog(this);

        MainActivity.getEntityManager().getRepository(Errand.class).ifPresent(repo -> {
            Errand errand = repo.findAll().stream().filter(x -> x.getId() == id).findFirst().orElseThrow(() -> new RuntimeException("Errand not found"));
            TextView course_name = findViewById(R.id.textview_course_name);
            course_name.setText("Course: " + errand.getName());
            TextView create_date = findViewById(R.id.textview_create_date);
            create_date.setText("Crée le: " + MainActivity.getSimpleDateFormat().format(errand.getCreateDate()));
            TextView todo_date = findViewById(R.id.textview_todo_date);
            todo_date.setText("A faire le: " + MainActivity.getSimpleDateFormat().format(errand.getToDoDate()));
            TextView do_date = findViewById(R.id.textview_do_date);
            boolean is_do = errand.getDoDate() != null;
            String do_date_string = !is_do ? "Pas encore terminé" : "Terminé le: " + MainActivity.getSimpleDateFormat().format(errand.getDoDate());
            do_date.setText(do_date_string);
            if (is_do) findViewById(R.id.btn_finish).setVisibility(View.GONE);

            MainActivity.getActivityReference().get().getProductCrud(errand).reload();

            findViewById(R.id.btn_modify).setOnClickListener(v -> errandDialog.setErrand(errand).open(ErrandDialog.Page.EDIT_ALL));

            findViewById(R.id.btn_add_product).setOnClickListener(v ->
                    MainActivity.getActivityReference().get()
                            .getProductCrud(errand).setActivity(this)
                            .getDialog().setErrand(errand).setActivity(this)
                            .open(ProductDialog.Page.MAIN));

            findViewById(R.id.btn_finish).setOnClickListener(v -> {
                MainActivity.getActivityReference().get().getErrandCrud().update(() -> {
                    errand.setStatus(Errand.Status.FINISH);
                    Toast.makeText(this, "Succès, vous avez terminé votre course !", Toast.LENGTH_LONG).show();
                    return errand;
                });
                Intent intent = new Intent(ErrandActivity.this, MainActivity.class);
                startActivity(intent);
            });
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            Intent intent = new Intent(ErrandActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
