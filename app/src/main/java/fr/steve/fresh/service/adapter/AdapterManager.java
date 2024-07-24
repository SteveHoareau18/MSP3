package fr.steve.fresh.service.adapter;

import android.app.Activity;

import java.util.Optional;

import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.repository.ErrandRepository;
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
        if (clazz == Errand.class) {
            return Optional.of((AdapterBundleLayout<?, T>) new AdapterBundleLayout<ErrandRepository, Errand>(activity)
                    .setLayout(android.R.layout.simple_spinner_item)
                    .setItems((ErrandRepository) entityManager.getRepository(Errand.class).get()));
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
