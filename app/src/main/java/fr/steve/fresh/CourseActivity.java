package fr.steve.fresh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import fr.steve.fresh.dialog.CourseDialog;
import fr.steve.fresh.dialog.ProductDialog;
import fr.steve.fresh.entity.Course;

public class CourseActivity extends Activity {

    private static WeakReference<CourseActivity> activityReference;
    private CourseDialog courseDialog;

    private ListView listProducts;
    private TextView titleProducts;

    public static WeakReference<CourseActivity> getActivityReference() {
        if (activityReference == null) throw new RuntimeException("Activity not found");
        return activityReference;
    }

    public ListView getListProducts() {
        return listProducts;
    }

    public TextView getTitleProducts() {
        return titleProducts;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityReference = new WeakReference<>(this);

        setContentView(R.layout.course_activity);

        listProducts = findViewById(R.id.list_products);
        titleProducts = findViewById(R.id.textview_historic_product_title);

        int id = getIntent().getIntExtra("course_id", -1);

        if (id == -1) throw new RuntimeException("Course not found");

        courseDialog = new CourseDialog(this);

        MainActivity.getEntityManager().getRepository(Course.class).ifPresent(repo -> {
            Course course = repo.findAll().stream().filter(x -> x.getId() == id).findFirst().orElseThrow(() -> new RuntimeException("course not found"));
            TextView course_name = findViewById(R.id.textview_course_name);
            course_name.setText("Course: " + course.getName());
            TextView create_date = findViewById(R.id.textview_create_date);
            create_date.setText("Crée le: " + MainActivity.getSimpleDateFormat().format(course.getCreateDate()));
            TextView todo_date = findViewById(R.id.textview_todo_date);
            todo_date.setText("A faire le: " + MainActivity.getSimpleDateFormat().format(course.getToDoDate()));
            TextView do_date = findViewById(R.id.textview_do_date);
            boolean is_do = course.getDoDate() != null;
            String do_date_string = !is_do ? "Pas encore terminé" : "Terminé le: " + MainActivity.getSimpleDateFormat().format(course.getDoDate());
            do_date.setText(do_date_string);
            if (is_do) findViewById(R.id.btn_finish).setVisibility(View.GONE);

            MainActivity.getActivityReference().get().getProductCrud(course).reload();

            findViewById(R.id.btn_modify).setOnClickListener(v -> courseDialog.setCourse(course).open(CourseDialog.Page.EDIT_ALL));

            findViewById(R.id.btn_add_product).setOnClickListener(v ->
                    MainActivity.getActivityReference().get()
                            .getProductCrud(course).setActivity(this)
                            .getDialog().setCourse(course).setActivity(this)
                            .open(ProductDialog.Page.MAIN));

            findViewById(R.id.btn_back).setOnClickListener(v -> {
                Intent intent = new Intent(CourseActivity.this, MainActivity.class);
                startActivity(intent);
            });
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            Intent intent = new Intent(CourseActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
