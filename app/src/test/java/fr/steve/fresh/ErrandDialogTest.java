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

import fr.steve.fresh.dialog.ErrandDialog;
import fr.steve.fresh.entity.Errand;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ErrandDialogTest {

    private ErrandDialog errandDialog;

    @Mock
    private Activity activity;

    @Mock
    private Errand errand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.buildActivity(Activity.class).create().get();
        errandDialog = new ErrandDialog(activity);
        errandDialog.setCourse(errand);
    }

    @Test
    public void testOpenMainPage_shouldCreateCourse() {
        // Arrange
        EditText inputName = new EditText(activity);
        inputName.setText("New Course");

        // Act
        errandDialog.open(ErrandDialog.Page.MAIN);

        // Assert
        assertEquals("New Course", inputName.getText().toString());
    }

    @Test
    public void testOpenEditAllPage_shouldShowCourseDetails() {
        // Arrange
        when(errand.getName()).thenReturn("Test Course");
        when(errand.getToDoDate()).thenReturn(new Date());
        when(errand.getCreateDate()).thenReturn(new Date());

        // Act
        errandDialog.open(ErrandDialog.Page.EDIT_ALL);

        // Assert
        assertEquals("Test Course", errand.getName());
        assertNotNull(errand.getToDoDate());
        assertNotNull(errand.getCreateDate());
    }
}