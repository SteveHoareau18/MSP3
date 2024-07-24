package fr.steve.fresh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import fr.steve.fresh.crud.ErrandCrud;
import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.repository.ErrandRepository;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ErrandCrudTest {

    private ErrandCrud errandCrud;

    @Before
    public void setUp() {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        errandCrud = new ErrandCrud(activity, new ErrandRepository());
    }

    @Test
    public void testCreateValidErrand() {
        errandCrud.create("Shopping");

        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertEquals("La course: Shopping a été crée.", toastMessage);
    }

    @Test
    public void testCreateInvalidErrand() {
        errandCrud.create("A");

        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertEquals("La course: Course a été crée.", toastMessage);

        errandCrud.create("12");

        toastMessage = ShadowToast.getTextOfLatestToast();
        assertEquals("La course: Course a été crée.", toastMessage);

        errandCrud.create("1234567891011121314151617181920");

        toastMessage = ShadowToast.getTextOfLatestToast();
        assertEquals("La course: Course a été crée.", toastMessage);
    }

    @Test
    public void testReadErrandNotFound() {
        Errand result = errandCrud.read(1);

        assertNull(result);
        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertEquals("Errand not found", toastMessage);
    }

    @Test
    public void testUpdateErrand() {
        Errand errand = new Errand();
        errand.setName("Shopping");
        Supplier<Errand> supplier = () -> errand;

        errandCrud.update(supplier);
    }

    @Test
    public void testGetSortedErrands() {
        Errand errand1 = new Errand();
        errand1.setName("Errand 1");
        errand1.setToDoDate(new Date(System.currentTimeMillis() - 10000));

        Errand errand2 = new Errand();
        errand2.setName("Errand 2");
        errand2.setToDoDate(new Date(System.currentTimeMillis() + 10000));

        List<Errand> errands = Arrays.asList(errand1, errand2);

        errandCrud.setFilter(Errand.Status.TO_DO);

        assertEquals(2, errands.size());
        assertEquals("Errand 2", errands.get(1).getName());
    }

    @Test
    public void testGetTitleAndButtonText() {
        errandCrud.setFilter(Errand.Status.TO_DO);
        assertEquals("Prochaines courses à faire: ", errandCrud.getTitle());
        assertEquals("Voir les courses déjà effectuées", errandCrud.getButtonText());

        errandCrud.setFilter(Errand.Status.FINISH);
        assertEquals("Courses terminées: ", errandCrud.getTitle());
        assertEquals("Voir les courses à faire", errandCrud.getButtonText());
    }
}
