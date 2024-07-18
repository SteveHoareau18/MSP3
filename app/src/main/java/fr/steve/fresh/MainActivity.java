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

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return sdf;
    }

    public static WeakReference<MainActivity> getActivityReference() {
        if (activityReference == null) throw new RuntimeException("Activity not found");
        return activityReference;
    }

    public TextView getHistoricTitle() {
        return historicTitle;
    }

    public ListView getListCourses() {
        return listCourses;
    }

    public CourseCrud getCourseCrud() {
        return courseCrud;
    }

    public ProductCrud getProductCrud(Course course) {
        productCrud.setCourse(course);
        return productCrud;
    }

    public Button getShowCoursesButton() {
        return showCourses;
    }

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

    public void startCourseActivity(Course course) {
        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
        intent.putExtra("course_id", course.getId());
        startActivity(intent);
    }

    public String getString(String k, String v) {
        return this.getSharedPreferences(name, mode).getString(k, v);
    }

    public SharedPreferences.Editor edit() {
        return this.getSharedPreferences(name, mode).edit();
    }

    public Map<String, ?> getAll() {
        return this.getSharedPreferences(name, mode).getAll();
    }
}