package fr.steve.fresh.service.adapter;

import android.app.Activity;

import java.util.Optional;

import fr.steve.fresh.entity.Course;
import fr.steve.fresh.repository.CourseRepository;
import fr.steve.fresh.service.factory.Entity;
import fr.steve.fresh.service.manager.EntityManager;

public class AdapterManager {

    private final Activity activity;
    private final EntityManager entityManager;

    public AdapterManager(Activity activity, EntityManager entityManager) {
        this.activity = activity;
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> Optional<AdapterBundleLayout<?, T>> getEntity(Class<T> clazz) {
        if (clazz == Course.class) {
            return Optional.of((AdapterBundleLayout<?, T>) new AdapterBundleLayout<CourseRepository, Course>(activity)
                    .setLayout(android.R.layout.simple_spinner_item)
                    .setItems((CourseRepository) entityManager.getRepository(Course.class).get()));
        }
        return Optional.empty();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Activity getActivity() {
        return activity;
    }
}
