package fr.steve.fresh.crud;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.R;
import fr.steve.fresh.crud.crud.Crud;
import fr.steve.fresh.dialog.CourseDialog;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.service.factory.Repository;

public class CourseCrud extends Crud<Course, CourseDialog> {

    private Course.Status filter;

    public CourseCrud(Activity activity, Repository<Course> courseRepository) {
        super(courseRepository, activity, new CourseDialog(activity), new CourseAdapter(activity, courseRepository.findAll()));

        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.getListCourses().setAdapter(getAdapter());

            mainActivity.getListCourses().setOnItemClickListener((parent, view, position, id) -> {
                setNextFilter();

                Course selectedCourse = getSortedCourses().get(position);

                if (selectedCourse.getToDoDate() == null) {
                    getDialog().setCourse(selectedCourse).open(CourseDialog.Page.EDIT_DATE);
                } else {
                    getDialog().setCourse(selectedCourse).open(CourseDialog.Page.EDIT_ALL);
                }

                setNextFilter();
            });
        }

        setFilter(Course.Status.TO_DO);

        reload();
    }

    @Override
    public void create(String name) {
        Course course = new Course();
        course.setName(name.isEmpty() ? "Course" : name);
        getRepository().add(course);
        Toast.makeText(getActivity(), "La course: " + course.getName() + " a été crée.", Toast.LENGTH_SHORT).show();
        reload();
    }

    @Override
    public Course read(int id) {
        return getRepository().findOneById(id).orElseGet(() -> {
            Toast.makeText(getActivity().getApplicationContext(), "Course not found", Toast.LENGTH_LONG).show();
            return null;
        });
    }

    @Override
    public void update(Supplier<Course> courseSupplier) {
        Course course = courseSupplier.get();
        if (course.getName().isEmpty()) course.setName("Course");
        getRepository().add(course);
        reload();
    }

    @Override
    public void delete() {

    }

    public void reload() {
        boolean hasItems = !getSortedCourses().isEmpty();
        MainActivity.getActivityReference().get().getHistoricTitle().setText(hasItems ? getTitle() : getActivity().getString(R.string.textview_historic_title_todo));
        MainActivity.getActivityReference().get().getListCourses().setVisibility(hasItems ? View.VISIBLE : View.GONE);

        setAdapter(new CourseAdapter(getActivity(), getSortedCourses()));
        MainActivity.getActivityReference().get().getListCourses().setAdapter(getAdapter());

        MainActivity.getActivityReference().get().getShowCoursesButton().setText(getButtonText());

        setNextFilter();
    }

    public void setNextFilter() {
        if (filter == Course.Status.FINISH) {
            setFilter(Course.Status.TO_DO);
        } else {
            setFilter(Course.Status.FINISH);
        }
    }

    public Course.Status getFilter() {
        return filter;
    }

    public void setFilter(Course.Status filter) {
        this.filter = filter;
    }

    public List<Course> getSortedCourses() {
        List<Course> sortedCourses = getRepository().findAll();
        sortedCourses.sort((o1, o2) -> {
            if (o1.getToDoDate() != null && o2.getToDoDate() != null) {
                return o1.getToDoDate().compareTo(o2.getToDoDate());
            } else if (o1.getToDoDate() == null && o2.getToDoDate() != null) {
                return 1;
            } else if (o1.getToDoDate() != null && o2.getToDoDate() == null) {
                return -1;
            }
            return o1.getCreateDate().compareTo(o2.getCreateDate());
        });

        sortedCourses = sortedCourses.stream().filter(x -> x.getStatus().equals(filter)).collect(Collectors.toList());
        return sortedCourses;
    }


    public String getTitle() {
        if (getFilter() == Course.Status.TO_DO) {
            return "Prochaines courses à faire: ";
        } else if (getFilter() == Course.Status.FINISH) {
            return "Courses terminées: ";
        }
        return "";
    }

    public String getButtonText() {
        if (getFilter() == Course.Status.TO_DO) {
            return "Voir les courses déjà effectuées";
        } else if (getFilter() == Course.Status.FINISH) {
            return "Voir les courses à faire";
        }
        return "";
    }


    public static class CourseAdapter extends ArrayAdapter<Course> {

        public CourseAdapter(@NonNull Activity activity, @NonNull List<Course> courses) {
            super(activity.getApplicationContext(), 0, courses);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Course course = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            TextView courseNameTextView = convertView.findViewById(android.R.id.text1);
            TextView toDoDateTextView = convertView.findViewById(android.R.id.text2);

            assert course != null;

            courseNameTextView.setText(course.getName());
            toDoDateTextView.setText(course.getToDoDate() != null ? "A faire le " + MainActivity.getSimpleDateFormat().format(course.getToDoDate()).replace(":", "h") : "Pas encore de date à faire");

            return convertView;
        }
    }
}
