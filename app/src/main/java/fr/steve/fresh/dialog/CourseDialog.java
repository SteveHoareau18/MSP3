package fr.steve.fresh.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.dialog.dialog.Dialog;
import fr.steve.fresh.dialog.page.IPage;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.util.DateUtils;

/**
 * CourseDialog class handles various dialog interactions related to a Course object.
 * This class allows creating, editing, and viewing details of a Course.
 */
public class CourseDialog extends Dialog<CourseDialog.Page> {

    private Course course;

    /**
     * Constructs a CourseDialog with the specified activity.
     *
     * @param activity the activity context
     */
    public CourseDialog(Activity activity) {
        super(activity);
    }

    /**
     * Sets the Course object to be used in the dialog.
     *
     * @param course the Course object
     * @return the current instance of CourseDialog
     */
    public CourseDialog setCourse(Course course) {
        this.course = course;
        return this;
    }

    /**
     * Opens a specific page and performs actions based on the page type.
     * <p>
     * Depending on the provided page, this method will display different dialogs for creating a new course,
     * editing the course date and time, viewing the list of products, and viewing or editing course details.
     * </p>
     *
     * @param page the page to be opened. The possible values are:
     *             <ul>
     *             <li>MAIN: Opens a dialog to create a new course.</li>
     *             <li>EDIT_DATE: Opens a dialog to edit the course date.</li>
     *             <li>EDIT_TIME: Opens a dialog to edit the course time.</li>
     *             <li>PRODUCTS: Starts an activity to view the course products.</li>
     *             <li>EDIT_ALL: Opens a dialog to show a summary, edit the name, or mark the course as completed.</li>
     *             </ul>
     */
    @SuppressLint("SetTextI18n")
    public void open(Page page) {
        switch (page) {
            case MAIN:
                EditText editText = new EditText(getActivity());
                buildAlertDialog("Nouvelle course: ",
                        () -> new Dialog.LinearLayoutBuilder(getActivity()).add(() -> editText).build(),
                        "Créer", ((dialog, which) -> MainActivity.getActivityReference().get().getCourseCrud().create(editText.getText().toString())),
                        "Annuler", ((dialog, which) -> dialog.cancel()));
                break;
            case EDIT_DATE:
                DatePicker datePicker = new DatePicker(getActivity());
                buildAlertDialog("Course: " + course.getName(),
                        () -> new Dialog.LinearLayoutBuilder(getActivity()).add(() -> datePicker).build(),
                        "Suivant", ((dialog, which) -> {
                            this.course.setToDoDate(DateUtils.toDate(datePicker));
                            MainActivity.getActivityReference().get().getCourseCrud().update(() -> this.course);
                            open(Page.EDIT_TIME);
                        }),
                        "Annuler", ((dialog, which) -> dialog.cancel()));
                break;
            case EDIT_TIME:
                TimePicker timePicker = new TimePicker(getActivity());
                buildAlertDialog("Course: " + course.getName(),
                        () -> new Dialog.LinearLayoutBuilder(getActivity()).add(() -> timePicker).build(),
                        "Suivant", ((dialog, which) -> {
                            this.course.setToDoDate(DateUtils.toDate(this.course.getToDoDate(), timePicker));
                            MainActivity.getActivityReference().get().getCourseCrud().update(() -> this.course);
                            Toast.makeText(getActivity(), "Succés ! Votre course à été modifée !", Toast.LENGTH_LONG).show();
                        }),
                        "Précédent", ((dialog, which) -> open(Page.EDIT_DATE)));
                break;
            case PRODUCTS:
                MainActivity.getActivityReference().get().startCourseActivity(this.course);
                break;
            case EDIT_ALL:
                if (course.getStatus() == Course.Status.FINISH) {
                    buildAlertDialog("Course: " + course.getName(), () -> new Dialog.LinearLayoutBuilder(getActivity()).add(() -> {
                                TextView toDoDate = new TextView(getActivity());
                                toDoDate.setText("A faire le " + MainActivity.getSimpleDateFormat().format(course.getToDoDate()));
                                return toDoDate;
                            }).add(() -> {
                                TextView doDate = new TextView(getActivity());
                                doDate.setText((course.getDoDate() == null ? "" : "Fait le " + MainActivity.getSimpleDateFormat().format(course.getDoDate())));
                                return doDate;
                            }).add(() -> {
                                TextView createDate = new TextView(getActivity());
                                createDate.setText("Crée le " + MainActivity.getSimpleDateFormat().format(course.getCreateDate()));
                                return createDate;
                            }).build(),
                            "OK", ((dialog, which) -> dialog.cancel()),
                            "", null);
                    break;
                } else {
                    TextView name = new TextView(getActivity());
                    name.setText("Nom: ");
                    EditText input_name = new EditText(getActivity());
                    input_name.setText(course.getName());
                    buildAlertDialog("Course: " + course.getName(),
                            () -> new Dialog.LinearLayoutBuilder(getActivity()).add(() -> name).add(() -> input_name)
                                    .add(() -> {
                                        Button button = new Button(getActivity());
                                        button.setText("VOIR LES PRODUITS");

                                        button.setOnClickListener(v -> open(Page.PRODUCTS));
                                        return button;
                                    }).add(() ->
                                            new Dialog.LinearLayoutBuilder(getActivity()).add(() -> {
                                                TextView textView = new TextView(getActivity());
                                                textView.setText("A faire le " + MainActivity.getSimpleDateFormat().format(course.getToDoDate()));
                                                return textView;
                                            }).add(() -> {
                                                Button button = new Button(getActivity());
                                                button.setText("Modifier la date");

                                                button.setOnClickListener(v -> open(Page.EDIT_DATE));
                                                return button;
                                            }).add(() -> {
                                                TextView textView = new TextView(getActivity());
                                                textView.setText("Crée le " + MainActivity.getSimpleDateFormat().format(course.getCreateDate()));
                                                return textView;
                                            }).add(() -> {
                                                Button button = new Button(getActivity());
                                                button.setText("Marquer comme terminer");
                                                button.setBackgroundColor(Color.rgb(62, 203, 118));

                                                button.setOnClickListener(v -> MainActivity.getActivityReference().get().getCourseCrud().update(() -> {
                                                    course.setStatus(Course.Status.FINISH);
                                                    Toast.makeText(this.getActivity(), "Succès, vous avez terminé votre course !", Toast.LENGTH_LONG).show();
                                                    return course;
                                                }));
                                                return button;
                                            }).build())
                                    .build(),
                            "OK", ((dialog, which) -> {
                                this.course.setName(input_name.getText().toString());
                                MainActivity.getActivityReference().get().getCourseCrud().update(() -> this.course);
                                Toast.makeText(getActivity(), "Succés ! Votre course à été modifée !", Toast.LENGTH_LONG).show();
                            }),
                            "Annuler", ((dialog, which) -> dialog.cancel()));
                }
                break;
        }
    }

    /**
     * Enum representing the different pages that can be opened in the CourseDialog.
     */
    public enum Page implements IPage {
        MAIN,
        EDIT_DATE,
        EDIT_TIME,
        EDIT_ALL,
        PRODUCTS,
    }
}
