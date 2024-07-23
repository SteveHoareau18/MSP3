package fr.steve.fresh;

import static org.junit.Assert.assertEquals;
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

import fr.steve.fresh.dialog.ProductDialog;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.entity.Product;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class ProductDialogTest {

    private ProductDialog productDialog;

    @Mock
    private Activity activity;

    @Mock
    private Course course;

    @Mock
    private Product product;

    @Mock
    private EditText inputName;

    @Mock
    private EditText inputQuantity;

    @Mock
    private EditText inputUnit;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.buildActivity(Activity.class).create().get();
        productDialog = new ProductDialog(activity);
        productDialog.setCourse(course);
        productDialog.setProduct(product);

        // Mock inputs
        inputName = new EditText(activity);
        inputQuantity = new EditText(activity);
        inputUnit = new EditText(activity);
    }

    @Test
    public void testOpenMainPage_withEmptyNameAndQuantity_shouldShowErrorToast() {
        // Arrange
        inputName.setText("");
        inputQuantity.setText("");

        // Act
        productDialog.open(ProductDialog.Page.MAIN);

        // Assert
        assertEquals(inputName.getText().toString(), "");
        assertEquals(inputQuantity.getText().toString(), "");
    }

    @Test
    public void testOpenGetPage_shouldShowProductDetails() {
        // Arrange
        when(product.getName()).thenReturn("Product1");
        when(product.getQuantity()).thenReturn(10);
        when(product.getUnit()).thenReturn("kg");

        // Act
        productDialog.open(ProductDialog.Page.GET);

        // Assert
        assertEquals(product.getName(), "Product1");
        assertEquals(product.getQuantity(), 10);
        assertEquals(product.getUnit(), "kg");
    }

    @Test
    public void testOpenEditPage_withValidProduct_shouldUpdateProductDetails() {
        // Arrange
        when(product.getName()).thenReturn("Product1");
        when(product.getQuantity()).thenReturn(10);
        when(product.getUnit()).thenReturn("kg");

        inputName.setText("Product1");
        inputQuantity.setText("10");
        inputUnit.setText("kg");

        // Act
        productDialog.open(ProductDialog.Page.EDIT);

        // Assert
        assertEquals(inputName.getText().toString(), "Product1");
        assertEquals(inputQuantity.getText().toString(), "10");
        assertEquals(inputUnit.getText().toString(), "kg");
    }

    @Test
    public void testOpenEditPage_withEmptyName_shouldShowErrorToast() {
        // Arrange
        inputName.setText("");
        inputQuantity.setText("10");

        // Act
        productDialog.open(ProductDialog.Page.EDIT);

        // Assert
        assertEquals(inputName.getText().toString(), "");
        assertEquals(inputQuantity.getText().toString(), "10");

    }
}

