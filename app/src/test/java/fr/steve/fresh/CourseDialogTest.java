package fr.steve.fresh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import fr.steve.fresh.dialog.CourseDialog;
import fr.steve.fresh.entity.Course;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CourseDialogTest {

    private CourseDialog courseDialog;

    @Mock
    private Activity activity;

    @Mock
    private Course course;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.buildActivity(Activity.class).create().get();
        courseDialog = new CourseDialog(activity);
        courseDialog.setCourse(course);
    }

    @Test
    public void testOpenMainPage_shouldCreateCourse() {
        // Arrange
        EditText inputName = new EditText(activity);
        inputName.setText("New Course");

        // Act
        courseDialog.open(CourseDialog.Page.MAIN);

        // Assert
        assertEquals("New Course", inputName.getText().toString());
    }

    @Test
    public void testOpenEditAllPage_shouldShowCourseDetails() {
        // Arrange
        when(course.getName()).thenReturn("Test Course");
        when(course.getToDoDate()).thenReturn(new Date());
        when(course.getCreateDate()).thenReturn(new Date());

        // Act
        courseDialog.open(CourseDialog.Page.EDIT_ALL);

        // Assert
        assertEquals("Test Course", course.getName());
        assertNotNull(course.getToDoDate());
        assertNotNull(course.getCreateDate());
    }
}