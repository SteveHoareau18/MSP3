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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.R;
import fr.steve.fresh.crud.crud.Crud;
import fr.steve.fresh.dialog.CourseDialog;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.service.factory.Repository;

/**
 * CourseCrud class handles CRUD operations for Course entities.
 * This class extends Crud and provides specific implementations
 * for creating, reading, updating, and deleting courses.
 */
public class CourseCrud extends Crud<Course, CourseDialog> {

    private Course.Status filter;

    /**
     * Constructs a CourseCrud with the specified activity and repository.
     *
     * @param activity         the activity context
     * @param courseRepository the repository for Course entities
     */
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

    /**
     * Creates a new course with the given name.
     * <p>
     * If the provided name is empty, the course will be named "Course" by default.
     * After creation, the course is added to the repository and a toast message
     * is displayed to inform the user that the course has been created.
     * Finally, the view is reloaded to reflect the changes.
     * </p>
     *
     * @param name the name of the course to be created. If empty, defaults to "Course".
     */
    @Override
    public void create(String name) {
        Course course = new Course();
        String regex = "^(?=(?:.*[A-Za-z]){2})[A-Za-z0-9]{1,30}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        boolean matches = matcher.matches();
        if (matches) {
            course.setName(name.trim());
        } else {
            course.setName("Course");
            Toast.makeText(getActivity(), "La course ne peut pas être nommé ainsi, par défaut elle est donc nommé: Course", Toast.LENGTH_LONG).show();
        }
        getRepository().add(course);
        Toast.makeText(getActivity(), "La course: " + course.getName() + " a été crée.", Toast.LENGTH_SHORT).show();
        reload();
    }

    /**
     * Reads a course by its ID.
     * <p>
     * If the course is not found, a toast message is displayed.
     * </p>
     *
     * @param id the ID of the course to be read
     * @return the Course object if found, null otherwise
     */
    @Override
    public Course read(int id) {
        return getRepository().findOneById(id).orElseGet(() -> {
            Toast.makeText(getActivity().getApplicationContext(), "Course not found", Toast.LENGTH_LONG).show();
            return null;
        });
    }

    /**
     * Updates the course information using the provided supplier.
     * <p>
     * If the course name is empty, it defaults to "Course".
     * After updating, the repository is updated and the view is reloaded.
     * </p>
     *
     * @param courseSupplier a supplier that provides the Course object to be updated
     */
    @Override
    public void update(Supplier<Course> courseSupplier) {
        Course course = courseSupplier.get();
        if (course.getName().isEmpty()) course.setName("Course");
        if (course.getToDoDate() != null) {
            //start reveil;
        }
        getRepository().add(course);
        reload();
    }

    /**
     * Deletes the specified course.
     *
     * @param course the Course object to be deleted
     */
    @Override
    public void delete(Course course) {
        // Implementation missing in the provided code
    }

    /**
     * Reloads the view to reflect changes in the course list.
     */
    public void reload() {
        boolean hasItems = !getSortedCourses().isEmpty();
        MainActivity.getActivityReference().get().getHistoricTitle().setText(hasItems ? getTitle() : getActivity().getString(R.string.textview_historic_title_todo));
        MainActivity.getActivityReference().get().getListCourses().setVisibility(hasItems ? View.VISIBLE : View.GONE);

        setAdapter(new CourseAdapter(getActivity(), getSortedCourses()));
        MainActivity.getActivityReference().get().getListCourses().setAdapter(getAdapter());

        MainActivity.getActivityReference().get().getShowCoursesButton().setText(getButtonText());

        setNextFilter();
    }

    /**
     * Sets the next filter status for courses.
     * <p>
     * Toggles between TO_DO and FINISH statuses.
     * </p>
     */
    public void setNextFilter() {
        if (filter == Course.Status.FINISH) {
            setFilter(Course.Status.TO_DO);
        } else {
            setFilter(Course.Status.FINISH);
        }
    }

    /**
     * Gets the current filter status for courses.
     *
     * @return the current filter status
     */
    public Course.Status getFilter() {
        return filter;
    }

    /**
     * Sets the filter status for courses.
     *
     * @param filter the filter status to be set
     */
    public void setFilter(Course.Status filter) {
        this.filter = filter;
    }

    /**
     * Gets the sorted list of courses based on the current filter.
     * <p>
     * Courses are sorted by their to-do date, and filtered by their status.
     * </p>
     *
     * @return the sorted list of courses
     */
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

    /**
     * Gets the title for the course list based on the current filter.
     *
     * @return the title for the course list
     */
    public String getTitle() {
        if (getFilter() == Course.Status.TO_DO) {
            return "Prochaines courses à faire: ";
        } else if (getFilter() == Course.Status.FINISH) {
            return "Courses terminées: ";
        }
        return "";
    }

    /**
     * Gets the button text for toggling the course list view based on the current filter.
     *
     * @return the button text for toggling the course list view
     */
    public String getButtonText() {
        if (getFilter() == Course.Status.TO_DO) {
            return "Voir les courses déjà effectuées";
        } else if (getFilter() == Course.Status.FINISH) {
            return "Voir les courses à faire";
        }
        return "";
    }

    /**
     * CourseAdapter class provides a custom adapter for displaying Course entities in a list view.
     */
    public static class CourseAdapter extends ArrayAdapter<Course> {

        /**
         * Constructs a CourseAdapter with the specified activity and list of courses.
         *
         * @param activity the activity context
         * @param courses  the list of courses
         */
        public CourseAdapter(@NonNull Activity activity, @NonNull List<Course> courses) {
            super(activity.getApplicationContext(), 0, courses);
        }

        /**
         * Gets the view for a specific item in the list.
         *
         * @param position    the position of the item in the list
         * @param convertView the recycled view to populate
         * @param parent      the parent view group
         * @return the view for the specified item
         */
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