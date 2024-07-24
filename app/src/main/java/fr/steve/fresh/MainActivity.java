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

import fr.steve.fresh.crud.CourseCrud;
import fr.steve.fresh.crud.ProductCrud;
import fr.steve.fresh.dialog.CourseDialog;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.repository.CourseRepository;
import fr.steve.fresh.repository.ProductRepository;
import fr.steve.fresh.service.manager.EntityManager;
import fr.steve.fresh.util.Serializer;

/**
 * The main activity of the application that serves as the entry point and central hub for managing
 * courses and products.
 * <p>
 * This activity initializes the necessary components for handling courses and products, provides
 * functionality for creating courses, viewing and managing existing courses, and navigating to course
 * details. It also manages global references and settings for the application.
 * </p>
 */
public class MainActivity extends Activity {

    public final static Serializer<Course> COURSE_SERIALIZER = new Serializer<>();
    public final static Serializer<Product> PRODUCT_SERIALIZER = new Serializer<>();

    public static final String name = "fresh_db";
    public static final int mode = MODE_MULTI_PROCESS;

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault());
    private static EntityManager entityManager;
    private static WeakReference<MainActivity> activityReference;
    private ListView listCourses;
    private TextView historicTitle;
    private Button showCourses;
    private CourseCrud courseCrud;
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
     * Gets the {@code TextView} for displaying the title of the historic courses.
     *
     * @return the {@code TextView} for the historic title
     */
    public TextView getHistoricTitle() {
        return historicTitle;
    }

    /**
     * Gets the {@code ListView} for displaying the list of courses.
     *
     * @return the {@code ListView} for courses
     */
    public ListView getListCourses() {
        return listCourses;
    }

    /**
     * Gets the {@code CourseCrud} instance used for managing course-related operations.
     *
     * @return the {@code CourseCrud} instance
     */
    public CourseCrud getCourseCrud() {
        return courseCrud;
    }

    /**
     * Gets the {@code ProductCrud} instance used for managing products related to a specific course.
     *
     * @param course the course for which to manage products
     * @return the {@code ProductCrud} instance
     */
    public ProductCrud getProductCrud(Course course) {
        productCrud.setCourse(course);
        return productCrud;
    }

    /**
     * Gets the {@code Button} for displaying or hiding completed courses.
     *
     * @return the {@code Button} for showing courses
     */
    public Button getShowCoursesButton() {
        return showCourses;
    }

    /**
     * Initializes the activity, sets up UI elements, and configures event listeners.
     * <p>
     * This method also initializes the {@code EntityManager}, {@code CourseCrud}, and {@code ProductCrud}
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

        listCourses = findViewById(R.id.list_courses);
        historicTitle = findViewById(R.id.textview_historic_title);
        showCourses = findViewById(R.id.btn_show_doCourse);

        courseCrud = new CourseCrud(this, getEntityManager().getRepository(Course.class).orElseGet(CourseRepository::new));
        productCrud = new ProductCrud(this, getEntityManager().getRepository(Product.class).orElseGet(ProductRepository::new));

        findViewById(R.id.btn_create_course).setOnClickListener(v -> courseCrud.getDialog().open(CourseDialog.Page.MAIN));

        showCourses.setOnClickListener(v -> courseCrud.reload());
    }

    /**
     * Starts the {@code CourseActivity} to display details for a specific course.
     *
     * @param course the course to display
     */
    public void startCourseActivity(Course course) {
        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
        intent.putExtra("course_id", course.getId());
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
