package fr.steve.fresh;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import fr.steve.fresh.crud.ProductCrud;
import fr.steve.fresh.repository.ProductRepository;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ProductCrudTest {

    private ProductCrud productCrud;

    @Before
    public void setUp() {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        productCrud = new ProductCrud(activity, new ProductRepository());
    }

    @Test
    public void testCanAddOrUpdate_withValidInput_shouldReturnTrue() {
        assertTrue(productCrud.canAddOrUpdate("Jus", 1, ""));
        assertTrue(productCrud.canAddOrUpdate("Jus", 1, "cl"));
        assertTrue(productCrud.canAddOrUpdate("Jus       ", 1, ""));
    }

    @Test
    public void testCanAddOrUpdate_withInvalidQuantity_shouldReturnFalse() {
        assertFalse(productCrud.canAddOrUpdate("Jus", -1, ""));
        assertFalse(productCrud.canAddOrUpdate("Jus", 10000, ""));
    }

    @Test
    public void testCanAddOrUpdate_withInvalidName_shouldShowToast() {
        productCrud.canAddOrUpdate("", 1, "");
        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertTrue(toastMessage.contains("Le nom du produit ne doit pas être vide et doit avoir au moins 2 lettres et ne doit pas dépasser 30 caractères"));

        productCrud.canAddOrUpdate("J", 1, "");
        toastMessage = ShadowToast.getTextOfLatestToast();
        assertTrue(toastMessage.contains("Le nom du produit ne doit pas être vide et doit avoir au moins 2 lettres et ne doit pas dépasser 30 caractères"));
    }

    @Test
    public void testCanAddOrUpdate_withInvalidUnit_shouldShowToast() {
        assertTrue(productCrud.canAddOrUpdate("Jus", 1, "x"));

        productCrud.canAddOrUpdate("Jus", 1, "abcdefghijklmnopqrstuvwxyz");
        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertTrue(toastMessage.contains("L'unité du produit peut être au moins 1 lettre et ne doit pas dépasser 15 caractères"));
    }

    @Test
    public void testCanAddOrUpdate_withInvalidQuantity_shouldShowToast() {
        productCrud.canAddOrUpdate("Jus", -1, "");
        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertTrue(toastMessage.contains("La quantité doit être positive et supérieure ou égale à 1 et inférieur à 1000"));

        productCrud.canAddOrUpdate("Jus", 10000, "");
        toastMessage = ShadowToast.getTextOfLatestToast();
        assertTrue(toastMessage.contains("La quantité doit être positive et supérieure ou égale à 1 et inférieur à 1000"));
    }
}