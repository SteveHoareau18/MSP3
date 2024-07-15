package fr.steve.fresh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.steve.fresh.entity.Course;
import fr.steve.fresh.service.adapter.AdapterBundleLayout;
import fr.steve.fresh.service.adapter.AdapterManager;
import fr.steve.fresh.service.factory.Repository;
import fr.steve.fresh.service.manager.EntityManager;

public class MainActivity extends Activity {

    private final String name = "fresh_db";
    private final int mode = MODE_MULTI_PROCESS;
    private EntityManager entityManager;
    private AdapterManager adapterManager;
    private ListView listCourses;
    private View historicTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        entityManager = new EntityManager(this);
        adapterManager = new AdapterManager(this, entityManager);

        listCourses = findViewById(R.id.list_courses);
        historicTitle = findViewById(R.id.textview_historic_title);

        findViewById(R.id.btn_create_course).setOnClickListener(v -> showCreateCourseDialog());

        entityManager.getRepository(Course.class).ifPresent(repo -> updateCoursesDisplay(repo, Optional.empty()));
    }

    private void showCreateCourseDialog() {
        EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setMessage("Votre course s'appellera:")
                .setView(input)
                .setPositiveButton("Yes", (dialog, id) -> createCourse(input.getText().toString()))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }

    private void createCourse(String courseName) {
        entityManager.getRepository(Course.class).ifPresent(repo -> {
            Course course = new Course();
            course.setName(courseName.isEmpty() ? "Course" : courseName);

            updateCoursesDisplay(repo, Optional.of(course));
        });
    }

    private void updateCoursesDisplay(Repository<Course> repository, Optional<Course> course) {
        entityManager.getRepository(Course.class).ifPresent(repo -> {
            boolean hasItems = !repo.findAll().isEmpty();
            historicTitle.setVisibility(hasItems ? View.INVISIBLE : View.VISIBLE);
            listCourses.setVisibility(hasItems ? View.VISIBLE : View.INVISIBLE);
        });
        AdapterBundleLayout<?, Course> adapterBundleLayout = adapterManager.getEntity(Course.class).orElse(null);

        if (adapterBundleLayout != null) {
            ArrayAdapter<Course> adapter = adapterBundleLayout.build();
            repository.setAdapter(listCourses, adapter);
            course.ifPresent(repository::add);
            updateAdapter(adapter, repository);
        }
    }

    private void updateAdapter(ArrayAdapter<Course> adapter, Repository<Course> repository) {
        adapter.clear();
        List<Course> sortedCourses = repository.findAll();
        Collections.sort(sortedCourses);
        Collections.reverse(sortedCourses);
        adapter.addAll(sortedCourses);
        adapter.notifyDataSetChanged();
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