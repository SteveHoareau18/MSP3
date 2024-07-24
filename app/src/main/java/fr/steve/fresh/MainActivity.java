package fr.steve.fresh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import fr.steve.fresh.crud.ErrandCrud;
import fr.steve.fresh.crud.ProductCrud;
import fr.steve.fresh.dialog.ErrandDialog;
import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.repository.ErrandRepository;
import fr.steve.fresh.repository.ProductRepository;
import fr.steve.fresh.service.manager.EntityManager;
import fr.steve.fresh.util.Serializer;

/**
 * The main activity of the application that serves as the entry point and central hub for managing
 * errands and products.
 * <p>
 * This activity initializes the necessary components for handling errands and products, provides
 * functionality for creating errands, viewing and managing existing errands, and navigating to errand
 * details. It also manages global references and settings for the application.
 * </p>
 */
public class MainActivity extends Activity {

    public final static Serializer<Errand> COURSE_SERIALIZER = new Serializer<>();
    public final static Serializer<Product> PRODUCT_SERIALIZER = new Serializer<>();

    public static final String name = "fresh_db";
    public static final int mode = MODE_MULTI_PROCESS;

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault());
    private static EntityManager entityManager;
    private static WeakReference<MainActivity> activityReference;
    private ListView listErrands;
    private TextView historicTitle;
    private Button showErrands;
    private ErrandCrud errandCrud;
    private ProductCrud productCrud;

    /**
     * Gets the {@code EntityManager} instance used for managing repositories.
     *
     * @return the {@code EntityManager} instance
     */
    public static EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Gets the {@code SimpleDateFormat} instance used for formatting dates.
     *
     * @return the {@code SimpleDateFormat} instance
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return sdf;
    }

    /**
     * Gets a weak reference to the current instance of {@code MainActivity}.
     * <p>
     * This method throws a {@link RuntimeException} if the activity reference is {@code null}.
     * </p>
     *
     * @return a weak reference to the current {@code MainActivity} instance
     */
    public static WeakReference<MainActivity> getActivityReference() {
        if (activityReference == null) throw new RuntimeException("Activity not found");
        return activityReference;
    }

    /**
     * Sets a weak reference to the current instance of {@code MainActivity}.
     *
     * @param weakReference the weak reference to set
     */
    public static void setActivityReference(WeakReference<MainActivity> weakReference) {
        activityReference = weakReference;
    }

    /**
     * Stops a process and updates the shared preferences to reflect this change.
     * <p>
     * This method is supported on devices with API level 33 (Android 13) and higher.
     * </p>
     *
     * @param prefs the shared preferences to update
     * @return {@code true} if the process was stopped successfully; {@code false} otherwise
     */
    public static boolean stop(SharedPreferences prefs) {
        //TODO ring and stop
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY.START.toString(), false);
            editor.putLong(KEY.CHRONO_START.toString(), 0);
            editor.putLong(KEY.CHRONO_ELAPSED.toString(), 0);
            editor.apply();
            return true;
        }
        return false;
    }

    /**
     * Gets the {@code TextView} for displaying the title of the historic errands.
     *
     * @return the {@code TextView} for the historic title
     */
    public TextView getHistoricTitle() {
        return historicTitle;
    }

    /**
     * Gets the {@code ListView} for displaying the list of errands.
     *
     * @return the {@code ListView} for errands
     */
    public ListView getListErrands() {
        return listErrands;
    }

    /**
     * Gets the {@code ErrandCrud} instance used for managing errand-related operations.
     *
     * @return the {@code ErrandCrud} instance
     */
    public ErrandCrud getErrandCrud() {
        return errandCrud;
    }

    /**
     * Gets the {@code ProductCrud} instance used for managing products related to a specific errand.
     *
     * @param errand the errand for which to manage products
     * @return the {@code ProductCrud} instance
     */
    public ProductCrud getProductCrud(Errand errand) {
        productCrud.setErrand(errand);
        return productCrud;
    }

    /**
     * Gets the {@code Button} for displaying or hiding completed errands.
     *
     * @return the {@code Button} for showing errands
     */
    public Button getShowErrandsButton() {
        return showErrands;
    }

    /**
     * Initializes the activity, sets up UI elements, and configures event listeners.
     * <p>
     * This method also initializes the {@code EntityManager}, {@code ErrandCrud}, and {@code ProductCrud}
     * instances and sets up their interactions with the UI.
     * </p>
     *
     * @param savedInstanceState the previously saved instance state (if any)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityReference = new WeakReference<>(this);

        setContentView(R.layout.main_activity);

        entityManager = new EntityManager();

        listErrands = findViewById(R.id.list_courses);
        historicTitle = findViewById(R.id.textview_historic_title);
        showErrands = findViewById(R.id.btn_show_doCourse);

        errandCrud = new ErrandCrud(this, getEntityManager().getRepository(Errand.class).orElseGet(ErrandRepository::new));
        productCrud = new ProductCrud(this, getEntityManager().getRepository(Product.class).orElseGet(ProductRepository::new));

        findViewById(R.id.btn_create_course).setOnClickListener(v -> errandCrud.getDialog().open(ErrandDialog.Page.MAIN));

        showErrands.setOnClickListener(v -> errandCrud.reload());
    }

    /**
     * Starts the {@code ErrandActivity} to display details for a specific errand.
     *
     * @param errand the errand to display
     */
    public void startErrandActivity(Errand errand) {
        Intent intent = new Intent(MainActivity.this, ErrandActivity.class);
        intent.putExtra("errand_id", errand.getId());
        startActivity(intent);
    }

    /**
     * Retrieves a string value from shared preferences, or a default value if the key is not found.
     *
     * @param k the key to retrieve
     * @param v the default value if the key is not found
     * @return the string value associated with the key, or the default value
     */
    public String getString(String k, String v) {
        return this.getSharedPreferences(name, mode).getString(k, v);
    }

    /**
     * Gets an editor for modifying shared preferences.
     *
     * @return the shared preferences editor
     */
    public SharedPreferences.Editor edit() {
        return this.getSharedPreferences(name, mode).edit();
    }

    /**
     * Gets all key-value pairs from shared preferences.
     *
     * @return a map of all key-value pairs in shared preferences
     */
    public Map<String, ?> getAll() {
        return this.getSharedPreferences(name, mode).getAll();
    }

    /**
     * Enumeration of keys used in shared preferences.
     */
    public enum KEY {
        START,
        CHRONO_START,
        CHRONO_ELAPSED;
    }
}
